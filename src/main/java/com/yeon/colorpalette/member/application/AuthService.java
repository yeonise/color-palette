package com.yeon.colorpalette.member.application;

import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yeon.colorpalette.exception.member.AuthorizationHeaderException;
import com.yeon.colorpalette.exception.member.InvalidTokenException;
import com.yeon.colorpalette.member.application.response.AccessTokenResponse;
import com.yeon.colorpalette.member.application.response.LoginResponse;
import com.yeon.colorpalette.member.domain.Account;
import com.yeon.colorpalette.member.domain.RefreshToken;
import com.yeon.colorpalette.member.infrastructure.TokenRepository;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

	private static final String TOKEN_PREFIX = "Bearer ";

	private final TokenProperties tokenProperties;
	private final TokenManager tokenManager;
	private final TokenRepository tokenRepository;

	@Transactional
	public LoginResponse issueTokens(Account account) {
		return new LoginResponse(generateAccessToken(account), generateRefreshToken(account));
	}

	private String generateAccessToken(Account account) {
		return generateToken(account, tokenProperties.getAccessTokenDuration());
	}

	private String generateRefreshToken(Account account) {
		String refreshToken = generateToken(account, tokenProperties.getRefreshTokenDuration());
		tokenRepository.saveRefreshToken(new RefreshToken(account.getId(), refreshToken));
		return refreshToken;
	}

	private String generateToken(Account account, long duration) {
		final SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes());
		return tokenManager.generateToken(Map.of(Account.ID, account.getId()), tokenProperties.getIssuer(), duration, key);
	}

	public AccessTokenResponse reissueAccessToken(Account account) {
		RefreshToken refreshToken = tokenRepository.findRefreshToken(account.getId())
			.orElseThrow(InvalidTokenException::new);

		if (refreshToken.getToken().equals(account.getToken())) {
			return new AccessTokenResponse(generateAccessToken(account));
		}

		throw new InvalidTokenException();
	}

	public Account extractAccount(String authorizationHeader) {
		final SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes());
		String accessToken = extract(authorizationHeader);
		return new Account(tokenManager.parse(accessToken, key).get(Account.ID, Long.class), accessToken);
	}

	private String extract(String authorizationHeader) {
		checkAuthorizationValidity(authorizationHeader);
		return authorizationHeader.substring(TOKEN_PREFIX.length());
	}

	private void checkAuthorizationValidity(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
			throw new AuthorizationHeaderException();
		}
	}

	@Transactional
	public void saveInvalidAccessToken(String accessToken) {
		final SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes());
		tokenRepository.saveInvalidAccessToken(accessToken, tokenManager.extractExpiry(accessToken, key));
	}

	@Transactional
	public void deleteRefreshToken(Long memberId) {
		tokenRepository.deleteRefreshToken(memberId);
	}

	public void checkTokenValidity(String authorizationHeader) {
		if (tokenRepository.isInBlacklist(extract(authorizationHeader))) {
			throw new InvalidTokenException();
		}
	}

}
