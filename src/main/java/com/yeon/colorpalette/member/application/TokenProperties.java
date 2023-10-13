package com.yeon.colorpalette.member.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("jwt")
@Component
public class TokenProperties {

	private long accessTokenDuration;
	private long refreshTokenDuration;
	private String issuer;
	private String secret;

}
