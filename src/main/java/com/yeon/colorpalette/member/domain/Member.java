package com.yeon.colorpalette.member.domain;

import com.yeon.colorpalette.auth.domain.Provider;
import com.yeon.colorpalette.auth.domain.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class Member {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;
	private String nickname;
	private String password;

	@Enumerated(EnumType.STRING)
	private Provider provider;

	@Enumerated(EnumType.STRING)
	private Role role;

	private boolean isDeleted;

}
