package com.yasidu.weather_app.service;

import java.util.List;
import java.util.Map;

public interface WeatherService {
	Map<String, Object> fetchWeatherForCity(String cityId);
	void evictCityCache(String cityId);
	void evictAllCache();
	List<Map<String, Object>> fetchWeatherForAllCities();
}
