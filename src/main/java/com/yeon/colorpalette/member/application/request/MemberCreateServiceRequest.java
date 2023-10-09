package com.yeon.colorpalette.member.application.request;

import com.yeon.colorpalette.member.domain.Member;
import com.yeon.colorpalette.member.domain.Provider;
import com.yeon.colorpalette.member.domain.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberCreateServiceRequest {

	private String email;
	private String nickname;
	private String password;

	public Member toEntity() {
		return new Member(null, email, nickname, password, Provider.BASIC, Role.MEMBER, false);
	}

}
