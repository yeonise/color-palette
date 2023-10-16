package com.yeon.colorpalette.member.application.request;

import com.yeon.colorpalette.member.domain.Member;
import com.yeon.colorpalette.auth.domain.Provider;
import com.yeon.colorpalette.auth.domain.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberCreateServiceRequest {

	private String email;
	private String nickname;
	private String password;

	public Member toEntity(Provider provider) {
		return new Member(null, email, nickname, password, provider, Role.MEMBER, false);
	}

}
