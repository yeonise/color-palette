package com.yeon.colorpalette.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.yeon.colorpalette.api.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException exception) {
		return ResponseEntity.status(exception.getStatus()).body(ApiResponse.noData(exception.getStatus(), exception.getMessage()));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BindException.class)
	public ApiResponse<Object> handleBindException(BindException exception) {
		return ApiResponse.of(HttpStatus.BAD_REQUEST, null,
			exception.getBindingResult().getFieldErrors().stream()
				.map(error -> {
					Map<String, String> errors = new HashMap<>();
					errors.put("field", error.getField());
					errors.put("message", error.getDefaultMessage());
					return errors;
				}));
	}

}
