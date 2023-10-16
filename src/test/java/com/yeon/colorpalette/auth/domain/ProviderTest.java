package com.yeon.colorpalette.auth.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProviderTest {

	@DisplayName("전달받은 이름과 일치하는 Provider를 반환한다")
	@Test
	void find() {
		// given
		String basic = "BASIC";
		String google = "GOOGLE";
		String none = "NONE";

		// when & then
		assertAll(
			() -> assertThat(Provider.find(basic).get()).isEqualTo(Provider.BASIC),
			() -> assertThat(Provider.find(google).get()).isEqualTo(Provider.GOOGLE),
			() -> assertThat(Provider.find(none)).isEmpty()
		);
	}

}
