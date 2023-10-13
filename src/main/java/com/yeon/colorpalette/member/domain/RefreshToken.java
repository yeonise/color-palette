package com.yeon.colorpalette.member.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RefreshToken {

	private long memberId;
	private String token;

}
