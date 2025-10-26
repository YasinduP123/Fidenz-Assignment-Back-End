package com.yasidu.weather_app.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yasidu.weather_app.dto.CityDto;
import com.yasidu.weather_app.exception.CityLoadException;
import com.yasidu.weather_app.service.WeatherService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Value("${openweather.api.key}")
	private String apiKey;

	@Value("${openweather.api.base-url}")
	private String baseUrl;

	@Value("${openweather.api.units:metric}")
	private String units;

	@Value("${app.cities.file-path:cities.json}")
	private String citiesFilePath;

	private final List<CityDto> cities = new ArrayList<>();

	@Lazy
	private WeatherService self;

	@Autowired
	public void setSelf(@Lazy WeatherService self){
		this.self = self;
	}

	@PostConstruct
	public void loadCityCodes() {
		try {
			log.info("Loading city codes from file: {}", citiesFilePath);
			ClassPathResource resource = new ClassPathResource(citiesFilePath);
			try (InputStream inputStream = resource.getInputStream()) {

				JsonNode root = objectMapper.readTree(inputStream);
				JsonNode listNode = root.has("List") ? root.get("List") : root;

				CityDto[] cityArray = objectMapper.readValue(listNode.toString(), CityDto[].class);
				cities.clear();
				cities.addAll(Arrays.asList(cityArray));

				log.info("Loaded {} cities", cities.size());
			}
		} catch (Exception e) {
			log.error("Failed to load cities: {}", e.getMessage(), e);
			throw new CityLoadException("Failed to load cities from source");
		}

	}

	@Override
	@Cacheable(value = "weatherCache", key = "#cityCode")
	public Map<String, Object> fetchWeatherForCity(String cityCode) {
		try {
			String url = String.format("%s/weather?id=%s&appid=%s&units=%s",
					baseUrl, cityCode, apiKey, units);

			String response = restTemplate.getForObject(url, String.class);
			JsonNode jsonNode = objectMapper.readTree(response);
			Map<String, Object> weatherData = new LinkedHashMap<>();
			weatherData.put("cityCode", cityCode);
			weatherData.put("name", jsonNode.path("name").asText("Unknown City"));

			// Weather Description
			JsonNode weatherArr = jsonNode.path("weather");
			if (weatherArr.isArray() && !weatherArr.isEmpty()) {
				JsonNode weather = weatherArr.get(0);
				weatherData.put("description", weather.path("description").asText("N/A"));
				weatherData.put("staticStatus", weather.path("main").asText("N/A"));
				weatherData.put("icon", weather.path("icon").asText(""));
			}

			// Main Data
			JsonNode main = jsonNode.path("main");
			weatherData.put("temp", main.path("temp").asDouble());
			weatherData.put("feels_like", main.path("feels_like").asDouble());
			weatherData.put("temp_min", main.path("temp_min").asDouble());
			weatherData.put("temp_max", main.path("temp_max").asDouble());
			weatherData.put("pressure", main.path("pressure").asInt());
			weatherData.put("humidity", main.path("humidity").asInt());

			// Visibility
			weatherData.put("visibility", jsonNode.path("visibility").asInt());

			// Wind Data
			JsonNode wind = jsonNode.path("wind");
			weatherData.put("wind_speed", wind.path("speed").asDouble());
			weatherData.put("wind_deg", wind.path("deg").asInt());

			// Sunrise / Sunset
			JsonNode sys = jsonNode.path("sys");
			if (!sys.isMissingNode()) {
				SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
				sdf.setTimeZone(TimeZone.getDefault());

				long sunriseUnix = sys.path("sunrise").asLong(0);
				long sunsetUnix = sys.path("sunset").asLong(0);

				if (sunriseUnix > 0) {
					weatherData.put("sunrise", sdf.format(new Date(sunriseUnix * 1000)));
				}
				if (sunsetUnix > 0) {
					weatherData.put("sunset", sdf.format(new Date(sunsetUnix * 1000)));
				}
			}

			weatherData.put("cached", false);
			return weatherData;

		} catch (Exception e) {
			log.error("Error fetching weather for city {}: {}", cityCode, e.getMessage());
			return Map.of(
					"cityCode", cityCode,
					"error", "Failed to fetch weather data",
					"message", e.getMessage()
			);
		}
	}

	@Override
	@Cacheable(value = "weatherAllCitiesCache")
	public List<Map<String, Object>> fetchWeatherForAllCities() {
		List<Map<String, Object>> allWeatherData = new ArrayList<>();

		for (CityDto city : cities) {
			try {
				Map<String, Object> weatherData = self.fetchWeatherForCity(city.getCityCode());

				weatherData.put("cityName", city.getCityName());
				weatherData.put("staticStatus", city.getStatus());
				weatherData.put("staticTemp", city.getTemp());

				if (!weatherData.containsKey("error")) {
					allWeatherData.add(weatherData);
				}
			} catch (Exception e) {
				log.error("Error fetching weather for {} ({}): {}", city.getCityName(), city.getCityCode(), e.getMessage());
			}
		}
		return allWeatherData;
	}

	@Override
	@CacheEvict(value = "weatherCache", key = "#cityCode")
	public void evictCityCache(String cityCode) {
		log.info("Cache evicted for city: {}", cityCode);
	}

	@Override
	@CacheEvict(value = "weatherCache", allEntries = true)
	public void evictAllCache() {
		log.info("All weather cache cleared");
	}
}
