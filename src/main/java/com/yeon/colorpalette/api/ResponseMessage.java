package com.yeon.colorpalette.api;

public enum ResponseMessage {

	// Member
	MEMBER_CREATE_SUCCESS("회원가입이 성공적으로 완료되었습니다"),
	MEMBER_DELETE_SUCCESS("회원탈퇴가 정상적으로 처리되었습니다");

	private final String message;

	ResponseMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}
