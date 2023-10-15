package com.yeon.colorpalette.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yeon.colorpalette.IntegrationTestSupport;
import com.yeon.colorpalette.auth.domain.Account;

import io.jsonwebtoken.security.Keys;

class TokenManagerTest extends IntegrationTestSupport {

	@Autowired
	TokenProperties tokenProperties;

	@Autowired
	TokenManager tokenManager;

	@DisplayName("토큰에서 사용자 정보를 추출할 수 있다")
	@Test
	void extractMember() {
		// given
		Map<String, Object> claims = Map.of(Account.ID, 1L);
		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes());

		String accessToken = tokenManager.generateToken(claims, tokenProperties.getIssuer(),
			tokenProperties.getAccessTokenDuration(), key);
		String refreshToken = tokenManager.generateToken(claims, tokenProperties.getIssuer(),
			tokenProperties.getRefreshTokenDuration(), key);

		// when & then
		assertAll(
			() -> assertThat(tokenManager.parse(accessToken, key).get("id")).isEqualTo(1),
			() -> assertThat(tokenManager.parse(refreshToken, key).get("id")).isEqualTo(1)
		);
	}

}
