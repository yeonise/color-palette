package com.yeon.colorpalette.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RefreshToken {

	private long memberId;
	private String token;

}
