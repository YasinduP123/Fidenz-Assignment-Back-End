package com.yasidu.weather_app.service;

import com.yasidu.weather_app.dto.CityDto;
import java.io.IOException;
import java.util.List;

public interface CitySyncService {
	List<CityDto> getAllCityWeather() throws IOException;
	List<CityDto> searchCities(String name);
}