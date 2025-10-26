package com.yasidu.weather_app.exception;

public class WeatherDataNotFoundException extends RuntimeException {
	public WeatherDataNotFoundException(String message) {
		super(message);
	}
}
