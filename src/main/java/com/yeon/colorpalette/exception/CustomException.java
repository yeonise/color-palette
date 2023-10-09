package com.yeon.colorpalette.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

	private final HttpStatus status;
	private final String message;

	public CustomException(ErrorType errorType) {
		super(errorType.getMessage());
		this.status = errorType.getStatus();
		this.message = errorType.getMessage();
	}

	public HttpStatus getStatus() {
		return this.status;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
