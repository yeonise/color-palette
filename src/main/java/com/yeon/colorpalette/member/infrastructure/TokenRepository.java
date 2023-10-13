package com.yeon.colorpalette.member.infrastructure;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.yeon.colorpalette.member.application.TokenProperties;
import com.yeon.colorpalette.member.domain.RefreshToken;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class TokenRepository {

	private static final String REFRESH_TOKEN_PREFIX = "user:";

	private final RedisTemplate<String, String> redisTemplate;
	private final TokenProperties tokenProperties;

	public Optional<RefreshToken> findRefreshToken(Long memberId) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		String refreshToken = valueOperations.get(REFRESH_TOKEN_PREFIX + memberId);

		if (Objects.isNull(refreshToken)) {
			return Optional.empty();
		}

		return Optional.of(new RefreshToken(memberId, refreshToken));
	}

	public void saveRefreshToken(RefreshToken refreshToken) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		valueOperations.set(REFRESH_TOKEN_PREFIX + refreshToken.getMemberId(), refreshToken.getToken());
		redisTemplate.expire(REFRESH_TOKEN_PREFIX + refreshToken.getToken(), tokenProperties.getRefreshTokenDuration(), TimeUnit.MILLISECONDS);
	}

	public void saveInvalidAccessToken(String accessToken, long expiration) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		valueOperations.set(accessToken, "invalid_access_token");
		redisTemplate.expire(accessToken, expiration, TimeUnit.MILLISECONDS);
	}

	public void deleteRefreshToken(Long memberId) {
		redisTemplate.unlink(REFRESH_TOKEN_PREFIX + memberId);
	}

	public boolean isInBlacklist(String accessToken) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(accessToken));
	}

}
