package com.yasidu.weather_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transferring full weather details shown in the weather card.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDto {

	@JsonProperty("city_name")
	private String cityName;

	@JsonProperty("date_time")
	private String dateTime;

	@JsonProperty("description")
	private String description;

	@JsonProperty("temp")
	private Double temp;

	@JsonProperty("temp_min")
	private Double tempMin;

	@JsonProperty("temp_max")
	private Double tempMax;

	@JsonProperty("pressure")
	private Integer pressure;

	@JsonProperty("humidity")
	private Integer humidity;

	@JsonProperty("staticStatus")
	private String staticStatus;

	@JsonProperty("visibility")
	private Double visibility;

	@JsonProperty("wind_speed")
	private Double windSpeed;

	@JsonProperty("wind_deg")
	private Integer windDeg;

	@JsonProperty("sunrise")
	private String sunrise;

	@JsonProperty("sunset")
	private String sunset;

	@JsonProperty("icon")
	private String icon;
}
