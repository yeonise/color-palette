package com.yeon.colorpalette.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.yeon.colorpalette.IntegrationTestSupport;
import com.yeon.colorpalette.auth.application.response.AccessTokenResponse;
import com.yeon.colorpalette.auth.application.response.LoginResponse;
import com.yeon.colorpalette.auth.domain.Account;
import com.yeon.colorpalette.auth.infrastructure.TokenRepository;
import com.yeon.colorpalette.auth.presentation.request.LoginRequest;
import com.yeon.colorpalette.exception.auth.AuthorizationHeaderException;
import com.yeon.colorpalette.exception.auth.InvalidLoginException;
import com.yeon.colorpalette.exception.auth.InvalidTokenException;
import com.yeon.colorpalette.member.domain.Member;

class AuthServiceTest extends IntegrationTestSupport {

	@Autowired
	TokenRepository tokenRepository;

	@DisplayName("일반 로그인 시나리오")
	@TestFactory
	Collection<DynamicTest> login() {
		// given
		Member member = makeMemberFixture();

		return List.of(
			dynamicTest("유효하지 않은 사용자 정보인 경우 예외가 발생한다", () -> {
				// given
				LoginRequest loginRequest = makeLoginRequest("black@email.com", "white100!");

				// when & then
				assertThatThrownBy(() -> authService.login(loginRequest))
					.isInstanceOf(InvalidLoginException.class);
			}),
			dynamicTest("유효한 사용자 정보인 경우 로그인에 성공한다", () -> {
				// given
				LoginRequest loginRequest = LoginRequest.builder()
					.email(member.getEmail())
					.password(member.getPassword())
					.build();

				// when & then
				assertDoesNotThrow(() -> authService.login(loginRequest));
			})
		);
	}

	@DisplayName("Access 토큰을 재발급 시나리오")
	@TestFactory
	Collection<DynamicTest> reissueAccessToken() {
		// given
		makeMemberFixture();
		LoginResponse loginResponse = authService.login(makeLoginRequest("white@email.com", "white100!"));

		return List.of(
			dynamicTest("Refresh 토큰이 유효하다면 Access 토큰을 재발급한다", () -> {
				// when
				AccessTokenResponse accessTokenResponse = authService.reissueAccessToken(new Account(1L, loginResponse.getRefreshToken()));

				// then
				assertThat(authService.extractAccount("Bearer " + accessTokenResponse.getAccessToken())
					.getId()).isEqualTo(1L);
			}),
			dynamicTest("Refresh 토큰이 유효하지 않다면 예외가 발생한다", () -> {
				// when & then
				assertThatThrownBy(() -> authService.reissueAccessToken(new Account(1L, loginResponse.getAccessToken())))
					.isInstanceOf(InvalidTokenException.class);
			})
		);
	}

	@DisplayName("로그아웃 요청 시 사용자의 모든 토큰을 무효화한다")
	@Test
	void logout() {
	    // given
		makeMemberFixture();
		LoginResponse loginResponse = authService.login(makeLoginRequest("white@email.com", "white100!"));

	    // when
		authService.logout(new Account(1L, loginResponse.getAccessToken()));

	    // then
		assertAll(
			() -> assertThat(tokenRepository.findRefreshToken(1L)).isEmpty(),
			() -> assertThat(tokenRepository.isInBlacklist(loginResponse.getAccessToken())).isTrue()
		);
	}

	private static LoginRequest makeLoginRequest(String email, String password) {
		return LoginRequest.builder()
			.email(email)
			.password(password)
			.build();
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
