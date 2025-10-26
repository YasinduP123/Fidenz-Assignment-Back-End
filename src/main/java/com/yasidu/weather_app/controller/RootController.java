package com.yasidu.weather_app.controller;

import com.yasidu.weather_app.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

	@GetMapping("/")
	public ResponseEntity<ApiResponse<Map<String, Object>>> root() {
		Map<String, Object> info = Map.of(
				"name", "Weather API",
				"version", "1.0.0",
				"status", "running",
				"authentication", "Auth0 JWT",
				"endpoints", Map.of(
						"cities", "/api/v1/weather/cities",
						"weather", "/api/v1/weather/{cityCode}",
						"user", "/api/v1/user/profile"
				)
		);

		ApiResponse<Map<String, Object>> response = new ApiResponse<>(
				true,
				"Weather API is running",
				info,
				HttpStatus.OK.value()
		);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/health")
	public ResponseEntity<Map<String, String>> health() {
		return ResponseEntity.ok(Map.of("status", "UP"));
	}
}