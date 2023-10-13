package com.yeon.colorpalette.config.interceptor;

import java.util.Arrays;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.yeon.colorpalette.member.application.AuthService;
import com.yeon.colorpalette.member.domain.Account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String ACCOUNT_ATTRIBUTE_NAME = "account";

	private final AuthService authService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		if (CorsUtils.isPreFlightRequest(request))
			return true;

		if (Whitelist.contains(request))
			return true;

		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		authService.checkTokenValidity(authorizationHeader);

		Account account = authService.extractAccount(authorizationHeader);
		request.setAttribute(ACCOUNT_ATTRIBUTE_NAME, account);

		return true;
	}

	enum Whitelist {

		SIGNUP(HttpMethod.POST, "/api/members"),
		LOGIN(HttpMethod.POST, "/api/members/login");

		private final HttpMethod httpMethod;
		private final String url;

		Whitelist(HttpMethod httpMethod, String url) {
			this.httpMethod = httpMethod;
			this.url = url;
		}

		public static boolean contains(HttpServletRequest request) {
			return Arrays.stream(values())
				.anyMatch(whitelist -> whitelist.httpMethod.name().equals(request.getMethod()) &&
					whitelist.url.equals(request.getRequestURI()));
		}

	}

}
