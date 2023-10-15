package com.yeon.colorpalette.auth.application;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.yeon.colorpalette.exception.auth.ExpiredTokenException;
import com.yeon.colorpalette.exception.auth.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class TokenManager {

	public String generateToken(Map<String, Object> claims, String issuer, long duration, SecretKey key) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + duration);

		return Jwts.builder()
			.claims(claims)
			.issuer(issuer)
			.issuedAt(now)
			.expiration(expiry)
			.signWith(key)
			.compact();
	}

	public Claims parse(String token, SecretKey key) {
		try {
			return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (
			ExpiredJwtException e) {
			throw new ExpiredTokenException();
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
			throw new InvalidTokenException();
		}
	}

	public long extractExpiry(String token, SecretKey key) {
		Date expiry = this.parse(token, key).getExpiration();
		return expiry.getTime() - new Date().getTime();
	}

}
