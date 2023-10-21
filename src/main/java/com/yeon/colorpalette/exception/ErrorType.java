package com.yeon.colorpalette.exception;

import org.springframework.http.HttpStatus;

public enum ErrorType {

	// Member
	EMAIL_ALREADY_IN_USE(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다"),
	NICKNAME_ALREADY_IN_USE(HttpStatus.BAD_REQUEST, "이미 가입된 닉네임입니다"),
	INVALID_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "Authorization 헤더를 찾을 수 없거나 형식이 올바르지 않습니다"),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다"),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
	INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다"),
	MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다"),
	BOOKMARK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 북마크에 등록된 팔레트입니다"),

	// Palette
	PALETTE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 등록된 팔레트입니다"),
	TAG_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 태그입니다"),
	PALETTE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 팔레트입니다"),
	INVALID_PALETTE_CREATOR(HttpStatus.FORBIDDEN, "팔레트에 대한 권한이 없는 사용자입니다");

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
