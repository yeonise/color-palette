package com.yeon.colorpalette.api;

public enum ResponseMessage {

	// Member
	MEMBER_CREATE_SUCCESS("회원가입이 성공적으로 완료되었습니다"),
	MEMBER_DELETE_SUCCESS("회원탈퇴가 정상적으로 처리되었습니다"),
	LOGIN_SUCCESS("로그인에 성공했습니다"),
	LOGOUT_SUCCESS("로그아웃이 정상적으로 처리되었습니다"),
	REISSUE_ACCESS_TOKEN_SUCCESS("Access 토큰 재발급에 성공했습니다"),

	// Palette
	PALETTE_CREATE_SUCCESS("팔레트 등록에 성공했습니다"),
	PALETTE_DELETE_SUCCESS("팔레트가 정상적으로 삭제되었습니다");

	private final String message;

	ResponseMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}
