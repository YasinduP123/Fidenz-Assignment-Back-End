package com.yasidu.weather_app.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(fieldError ->
				fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage())
		);

		ErrorResponse response = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"Invalid Input Data",
				"One or more fields contain invalid values. Please check and try again.",
				fieldErrors
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
		Map<String, String> violations = new HashMap<>();
		ex.getConstraintViolations().forEach(violation ->
				violations.put(violation.getPropertyPath().toString(), violation.getMessage())
		);

		ErrorResponse response = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"Constraint Violation",
				"Some constraints were violated. Ensure all input values meet the required rules.",
				violations
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(HttpClientErrorException.Forbidden.class)
	public ResponseEntity<ErrorResponse> handleHttpForbidden(HttpClientErrorException.Forbidden ex) {
		ErrorResponse response = new ErrorResponse(
				HttpStatus.FORBIDDEN.value(),
				"Access Restricted",
				"You are not authorized to access this resource.",
				null
		);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
		ErrorResponse response = new ErrorResponse(
				HttpStatus.UNAUTHORIZED.value(),
				"Authentication Failed",
				"Your credentials are invalid or expired. Please log in again.",
				null
		);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
		ErrorResponse response = new ErrorResponse(
				HttpStatus.FORBIDDEN.value(),
				"Access Denied",
				"You do not have permission to perform this action.",
				null
		);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
		ErrorResponse response = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error",
				"An unexpected error occurred. Please try again later.",
				null
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

}
