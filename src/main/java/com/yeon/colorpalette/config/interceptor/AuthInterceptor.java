package com.yeon.colorpalette.config.interceptor;

import java.util.Arrays;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.yeon.colorpalette.auth.application.AuthService;
import com.yeon.colorpalette.auth.domain.Account;

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
		LOGIN(HttpMethod.POST, "/api/login"),
		REISSUE_ACCESS_TOKEN(HttpMethod.GET, "/api/auth/reissue"),
		READ_PALETTE(HttpMethod.GET, "/api/palettes");

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
