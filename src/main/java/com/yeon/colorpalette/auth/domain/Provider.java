package com.yeon.colorpalette.auth.domain;

import java.util.Arrays;
import java.util.Optional;

public enum Provider {

	BASIC, GOOGLE;

	public static Optional<Provider> find(String providerName) {
		return Arrays.stream(values()).filter(provider -> provider.name().equals(providerName)).findAny();
	}

}
