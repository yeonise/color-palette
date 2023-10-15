package com.yeon.colorpalette.auth.application;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.yeon.colorpalette.auth.application.response.OAuthTokenResponse;
import com.yeon.colorpalette.auth.domain.OAuthAccount;
import com.yeon.colorpalette.auth.domain.Provider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuthManager {

	private final OAuthProperties oAuthProperties;

	public OAuthAccount generateOAuthAccount(String code) {
		OAuthTokenResponse oAuthTokenResponse = generateAccessToken(code);

		Map<String, String> result = WebClient.create()
			.get()
			.uri(oAuthProperties.getInformationUrl())
			.headers(header -> header.setBearerAuth(oAuthTokenResponse.getAccessToken()))
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
			.block();

		return OAuthAccount.of(result, Provider.GOOGLE);
	}

	private OAuthTokenResponse generateAccessToken(String code) {
		return WebClient.create()
			.post()
			.uri(oAuthProperties.getTokenUrl())
			.headers(header -> {
				header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				header.setAccept(List.of(MediaType.APPLICATION_JSON));
				header.setAcceptCharset(List.of(StandardCharsets.UTF_8));
			})
			.bodyValue(generateTokenRequest(code))
			.retrieve()
			.bodyToMono(OAuthTokenResponse.class)
			.block();
	}

	private MultiValueMap<String, String> generateTokenRequest(String code) {
		MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
		data.add("code", code);
		data.add("client_id", oAuthProperties.getClientId());
		data.add("client_secret", oAuthProperties.getClientSecret());
		data.add("redirect_uri", oAuthProperties.getRedirectUri());
		data.add("grant_type", "authorization_code");
		return data;
	}

}
