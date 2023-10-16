package com.yeon.colorpalette.auth.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("oauth")
@Component
public class OAuthProperties {

	private String clientId;
	private String clientSecret;
	private String tokenUrl;
	private String informationUrl;
	private String redirectUri;

}
