package com.yasidu.weather_app.exception;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Setter
@Getter
public class ErrorResponse {
	private Integer status;
	private String error;
	private String message;
	private LocalDateTime timestamp;
	private Map<String, String> fieldErrors;
	public ErrorResponse(Integer status, String error, String message, Map<String, String> fieldErrors) {
		this.status = status;
		this.error = error;
		this.message = message;
		this.timestamp = LocalDateTime.now();
		this.fieldErrors = fieldErrors;
	}
}

