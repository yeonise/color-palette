package com.yeon.colorpalette.auth.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public enum OAuthProvider {

	GOOGLE(Provider.GOOGLE, Mapper.google);

	private final Provider provider;
	private final Function<Map<String, String>, OAuthAccount> mapper;

	OAuthProvider(Provider provider, Function<Map<String, String>, OAuthAccount> mapper) {
		this.provider = provider;
		this.mapper = mapper;
	}

	public static OAuthAccount getOAuthAccount(Map<String, String> attributes, Provider provider) {
		return Arrays.stream(values())
			.filter(oAuthProvider -> oAuthProvider.provider == provider)
			.findAny()
			.map(oAuthProvider -> oAuthProvider.mapper.apply(attributes))
			.orElseThrow();
	}

	protected static class Mapper {

		protected static Function<Map<String, String>, OAuthAccount> google =
			attributes -> new OAuthAccount(attributes.get("email"), makeRandomNickname(attributes.get("email")));

		private static String makeRandomNickname(String email) {
			String random = email.split("@")[0] + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);

			if (random.length() > 20) return random.substring(0, 20);

			return random;
		}

	}

}
