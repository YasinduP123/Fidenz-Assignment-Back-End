package com.yasidu.weather_app.controller;

import com.yasidu.weather_app.dto.CityDto;
import com.yasidu.weather_app.response.ApiResponse;
import com.yasidu.weather_app.service.CitySyncService;
import com.yasidu.weather_app.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

	private final WeatherService weatherService;
	private final CitySyncService cityService;

	@GetMapping("/cities")
	public ResponseEntity<ApiResponse<List<CityDto>>> getAllCities() {
		try {
			List<CityDto> cities = cityService.getAllCityWeather();
			ApiResponse<List<CityDto>> response = new ApiResponse<>(
					true,
					"Cities retrieved successfully",
					cities,
					HttpStatus.OK.value()
			);
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			ApiResponse<List<CityDto>> response = new ApiResponse<>(
					false,
					"Failed to fetch cities: " + e.getMessage(),
					null,
					HttpStatus.INTERNAL_SERVER_ERROR.value()
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/cities/search")
	public ResponseEntity<ApiResponse<List<CityDto>>> searchCities(@RequestParam String name) {
		try {
			List<CityDto> cities = cityService.searchCities(name);
			ApiResponse<List<CityDto>> response = new ApiResponse<>(
					true,
					cities.isEmpty() ? "No cities found matching: " + name : "Cities found successfully",
					cities,
					HttpStatus.OK.value()
			);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<List<CityDto>> response = new ApiResponse<>(
					false,
					"Error searching cities: " + e.getMessage(),
					null,
					HttpStatus.INTERNAL_SERVER_ERROR.value()
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/{cityCode}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> getWeather(
			@PathVariable String cityCode,
			@AuthenticationPrincipal Jwt jwt) {

		try {
			Map<String, Object> weatherData = weatherService.fetchWeatherForCity(cityCode);
			if (weatherData == null || weatherData.containsKey("error")) {
				ApiResponse<Map<String, Object>> response = new ApiResponse<>(
						false,
						"Weather data not found for city code: " + cityCode,
						null,
						HttpStatus.NOT_FOUND.value()
				);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			ApiResponse<Map<String, Object>> response = new ApiResponse<>(
					true,
					"Weather data retrieved successfully",
					weatherData,
					HttpStatus.OK.value()
			);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<Map<String, Object>> response = new ApiResponse<>(
					false,
					"Error fetching weather data: " + e.getMessage(),
					null,
					HttpStatus.INTERNAL_SERVER_ERROR.value()
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/cities/all-weather")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllCitiesWeather() {
		try {
			List<Map<String, Object>> weatherData = weatherService.fetchWeatherForAllCities();
			ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>(
					true,
					"Weather data for all cities retrieved successfully",
					weatherData,
					HttpStatus.OK.value()
			);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>(
					false,
					"Failed to fetch weather data: " + e.getMessage(),
					null,
					HttpStatus.INTERNAL_SERVER_ERROR.value()
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@DeleteMapping("/cache/{cityCode}")
	public ResponseEntity<ApiResponse<String>> clearCityCache(@PathVariable String cityCode) {
		try {
			weatherService.evictCityCache(cityCode);
			ApiResponse<String> response = new ApiResponse<>(
					true,
					"Cache cleared successfully",
					"Cache cleared for city code: " + cityCode,
					HttpStatus.OK.value()
			);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<String> response = new ApiResponse<>(
					false,
					"Failed to clear cache: " + e.getMessage(),
					null,
					HttpStatus.INTERNAL_SERVER_ERROR.value()
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@DeleteMapping("/cache/all")
	public ResponseEntity<ApiResponse<String>> clearAllCache() {
		try {
			weatherService.evictAllCache();
			ApiResponse<String> response = new ApiResponse<>(
					true,
					"All cache cleared successfully",
					"All cache entries cleared",
					HttpStatus.OK.value()
			);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<String> response = new ApiResponse<>(
					false,
					"Failed to clear cache: " + e.getMessage(),
					null,
					HttpStatus.INTERNAL_SERVER_ERROR.value()
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}