package com.yeon.colorpalette.member.presentation.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginRequest {

	private String email;
	private String password;

	@Builder
	private LoginRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

}
