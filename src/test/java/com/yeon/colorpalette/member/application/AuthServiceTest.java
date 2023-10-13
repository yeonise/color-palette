package com.yeon.colorpalette.member.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.yeon.colorpalette.IntegrationTestSupport;
import com.yeon.colorpalette.exception.member.AuthorizationHeaderException;
import com.yeon.colorpalette.member.application.response.AccessTokenResponse;
import com.yeon.colorpalette.member.application.response.LoginResponse;
import com.yeon.colorpalette.member.domain.Account;

class AuthServiceTest extends IntegrationTestSupport {

	@Autowired
	AuthService authService;

	@DisplayName("Refresh 토큰이 유효하다면 Access 토큰을 재발급한다")
	@Test
	void reissueAccessToken() {
		// given
		final Long ID = 1L;
		LoginResponse loginResponse = authService.issueTokens(new Account(ID, null));

		// when
		AccessTokenResponse accessTokenResponse = authService.reissueAccessToken(new Account(ID, loginResponse.getRefreshToken()));

		// then
		assertThat(authService.extractAccount("Bearer " + accessTokenResponse.getAccessToken())
			.getId()).isEqualTo(ID);
	}

	@DisplayName("Authorization 헤더를 찾을 수 없거나 형식이 올바르지 않은 경우 예외가 발생한다")
	@NullAndEmptySource
	@ValueSource(strings = {"", " ", "Bear token"})
	@ParameterizedTest
	void extractAccountWithIncorrectAuthorizationHeader(String authorizationHeader) {
		// when & then
		assertThatThrownBy(() -> authService.extractAccount(authorizationHeader))
			.isInstanceOf(AuthorizationHeaderException.class);
	}

}
