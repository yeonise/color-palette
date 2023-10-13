package com.yeon.colorpalette.member.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Account {

	public static final String ID = "id";
	public static final String TOKEN = "token";

	private long id;
	private String token;

}
