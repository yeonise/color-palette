package com.yeon.colorpalette.auth.application;

import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yeon.colorpalette.auth.application.response.AccessTokenResponse;
import com.yeon.colorpalette.auth.application.response.LoginResponse;
import com.yeon.colorpalette.auth.domain.Account;
import com.yeon.colorpalette.auth.domain.OAuthAccount;
import com.yeon.colorpalette.auth.domain.Provider;
import com.yeon.colorpalette.auth.domain.RefreshToken;
import com.yeon.colorpalette.auth.infrastructure.TokenRepository;
import com.yeon.colorpalette.auth.presentation.request.LoginRequest;
import com.yeon.colorpalette.exception.auth.AuthorizationHeaderException;
import com.yeon.colorpalette.exception.auth.InvalidLoginException;
import com.yeon.colorpalette.exception.auth.InvalidTokenException;
import com.yeon.colorpalette.member.application.request.MemberCreateServiceRequest;
import com.yeon.colorpalette.member.domain.Member;
import com.yeon.colorpalette.member.infrastructure.MemberRepository;

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
	private final OAuthManager oAuthManager;
	private final MemberRepository memberRepository;

	@Transactional
	public LoginResponse login(LoginRequest request) {
		Member member = memberRepository.findByEmailAndPasswordAndIsDeleted(request.getEmail(), request.getPassword(),
				false)
			.orElseThrow(InvalidLoginException::new);

		return issueTokens(new Account(member.getId(), null));
	}

	@Transactional
	public LoginResponse loginByOAuth(String code) {
		OAuthAccount oAuthAccount = oAuthManager.generateOAuthAccount(code);

		Member member = memberRepository.findByEmailAndProviderAndIsDeleted(oAuthAccount.getEmail(), Provider.GOOGLE,
				false)
			.orElseGet(() -> {
				MemberCreateServiceRequest request =
					new MemberCreateServiceRequest(oAuthAccount.getEmail(), oAuthAccount.getNickname(), null);

				return memberRepository.save(request.toEntity(Provider.GOOGLE));
			});

		return issueTokens(new Account(member.getId(), null));
	}

	@Transactional
	public void logout(Account account) {
		saveInvalidAccessToken(account.getToken());
		deleteRefreshToken(account.getId());
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

	private LoginResponse issueTokens(Account account) {
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
		return tokenManager.generateToken(Map.of(Account.ID, account.getId()), tokenProperties.getIssuer(), duration,
			key);
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

}
