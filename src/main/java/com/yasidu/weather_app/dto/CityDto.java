package com.yasidu.weather_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CityDto
 * -------
 * Represents a city entry from cities.json:
 * {
 *   "CityCode": "1248991",
 *   "CityName": "Colombo",
 *   "Temp": "33.0",
 *   "Status": "Clouds"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityDto {

	@JsonProperty("CityCode")
	private String cityCode;

	@JsonProperty("CityName")
	private String cityName;

	@JsonProperty("Temp")
	private String temp;

	@JsonProperty("Status")
	private String status;
}
