package com.yeon.colorpalette.exception;

import org.springframework.http.HttpStatus;

public enum ErrorType {

	// Member
	EMAIL_ALREADY_IN_USE(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다"),
	NICKNAME_ALREADY_IN_USE(HttpStatus.BAD_REQUEST, "이미 가입된 닉네임입니다");

	private final HttpStatus status;
	private final String message;

	ErrorType(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return this.status;
	}

	public String getMessage() {
		return this.message;
	}

}
