package com.yeon.colorpalette.auth.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthProviderTest {

	@DisplayName("OAuth 결과를 바탕으로 생성한 로그인 계정을 반환한다")
	@Test
	void getOAuthAccount() {
	    // given
		Map<String, String> attributes = Map.of("email", "white@email.com");

	    // when
		OAuthAccount oAuthAccount = OAuthProvider.getOAuthAccount(attributes, Provider.GOOGLE);

		// then
		assertAll(
			() -> assertThat(oAuthAccount.getEmail()).isEqualTo(attributes.get("email")),
			() -> assertThat(oAuthAccount.getNickname()).startsWith("white")
		);
	}

}
