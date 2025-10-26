package com.yasidu.weather_app.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yasidu.weather_app.dto.CityDto;
import com.yasidu.weather_app.exception.CityLoadException;
import com.yasidu.weather_app.service.CitySyncService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitySyncServiceImpl implements CitySyncService {

	private final ObjectMapper objectMapper;

	@Value("${app.cities.file-path:cities.json}")
	private String citiesFilePath;

	private final List<CityDto> cities = new ArrayList<>();

	@PostConstruct
	public void loadCitiesFromJson() {
		try {
			log.info("Loading cities from: {}", citiesFilePath);
			ClassPathResource resource = new ClassPathResource(citiesFilePath);

			try (InputStream inputStream = resource.getInputStream()) {
				JsonNode root = objectMapper.readTree(inputStream);
				JsonNode listNode;

				if (root.has("List") && root.get("List").isArray()) {
					listNode = root.get("List");
				} else if (root.isArray()) {
					listNode = root;
				} else {
					throw new IllegalArgumentException("Invalid cities.json format");
				}

				CityDto[] cityArray = objectMapper.readValue(listNode.toString(), CityDto[].class);
				cities.clear();
				cities.addAll(Arrays.asList(cityArray));

				log.info("Loaded {} cities successfully", cities.size());
			}
		} catch (Exception e) {
			log.error("Failed to load cities: {}", e.getMessage(), e);
			throw new CityLoadException("Failed to load cities");
		}
	}

	@Override
	public List<CityDto> getAllCityWeather() throws IOException {
		return new ArrayList<>(cities);
	}

	@Override
	public List<CityDto> searchCities(String name) {
		if (name == null || name.isBlank()) {
			return new ArrayList<>(cities);
		}

		String searchTerm = name.toLowerCase().trim();
		return cities.stream()
				.filter(city -> city.getCityName().toLowerCase().contains(searchTerm))
				.toList();
	}
}