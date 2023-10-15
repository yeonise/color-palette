package com.yeon.colorpalette.auth.domain;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OAuthAccount {

	private String email;
	private String nickname;

	public static OAuthAccount of(Map<String, String> attributes, Provider provider) {
		return OAuthProvider.getOAuthAccount(attributes, provider);
	}

}
