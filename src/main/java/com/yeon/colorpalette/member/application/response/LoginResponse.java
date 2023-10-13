package com.yeon.colorpalette.member.application.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {

	private String accessToken;
	private String refreshToken;

}
