package com.yeon.colorpalette.auth.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.yeon.colorpalette.RestDocsSupport;
import com.yeon.colorpalette.auth.application.TokenManager;
import com.yeon.colorpalette.auth.application.TokenProperties;
import com.yeon.colorpalette.auth.application.response.LoginResponse;
import com.yeon.colorpalette.auth.presentation.request.LoginRequest;
import com.yeon.colorpalette.exception.auth.ExpiredTokenException;
import com.yeon.colorpalette.exception.auth.InvalidLoginException;
import com.yeon.colorpalette.exception.auth.InvalidTokenException;

import io.jsonwebtoken.security.Keys;

public class AuthControllerTest extends RestDocsSupport {

	@Autowired
	TokenManager tokenManager;

	@Autowired
	TokenProperties tokenProperties;

	@DisplayName("OAuth 로그인 API")
	@Test
	void loginByOAuth() throws Exception {
		// given
		String code = "4%2F0AfJohXnjv2bbay5fBNK5HUuisDWKmX5poUsd6nrqSK3CC9xTf8TA1tcdmVGIKF6YEZKdrQ&scope=email+openid+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&authuser=0&prompt=none";

		given(authService.loginByOAuth(anyString()))
			.willReturn(new LoginResponse("Access token will be displayed here.", "Refresh token will be displayed here."));

		// when & then
		mockMvc.perform(get("/api/oauth")
				.param("code", code))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("member-login-oauth",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
					fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("Access Token"),
					fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("Refresh Token")
				)));
	}

	@DisplayName("일반 로그인 API")
	@Test
	void login() throws Exception {
		// given
		LoginRequest request = makeLoginRequest("white@email.com", "white100!");

		given(authService.login(any(LoginRequest.class)))
			.willReturn(new LoginResponse("Access token will be displayed here.", "Refresh token will be displayed here."));

		// when & then
		mockMvc.perform(post("/api/login")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("member-login",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
					fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("Access Token"),
					fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("Refresh Token")
				)));
	}

	@DisplayName("일반 로그인 API with Invalid Request")
	@Test
	void loginWithInvalidRequest() throws Exception {
		// given
		LoginRequest request = makeLoginRequest("black@email.com", "white100!");

		given(authService.login(any(LoginRequest.class)))
			.willThrow(new InvalidLoginException());

		// when & then
		mockMvc.perform(post("/api/login")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andDo(document("member-login-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	private static LoginRequest makeLoginRequest(String email, String password) {
		return LoginRequest.builder()
			.email(email)
			.password(password)
			.build();
	}

	@DisplayName("로그아웃 API")
	@Test
	void logout() throws Exception {
		// given
		String authorizationHeader = makeAuthorizationHeader(6000L);

		// when & then
		mockMvc.perform(delete("/api/logout")
				.header(HttpHeaders.AUTHORIZATION, authorizationHeader))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("member-logout",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	@DisplayName("Request With 만료된 토큰")
	@Test
	void requestWithExpiredToken() throws Exception {
		// given
		String authorizationHeader = makeAuthorizationHeader(0L);

		given(authService.extractAccount(anyString()))
			.willThrow(new ExpiredTokenException());

		// when & then
		mockMvc.perform(delete("/api/members")
				.header(HttpHeaders.AUTHORIZATION, authorizationHeader))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andDo(document("request-with-expired-token",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	@DisplayName("Request With 유효하지 않은 토큰")
	@Test
	void requestWithInvalidToken() throws Exception {
		// given
		String authorizationHeader = makeAuthorizationHeader(60000L) + "-";

		given(authService.extractAccount(anyString()))
			.willThrow(new InvalidTokenException());

		// when & then
		mockMvc.perform(delete("/api/members")
				.header(HttpHeaders.AUTHORIZATION, authorizationHeader))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andDo(document("request-with-invalid-token",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	private String makeAuthorizationHeader(long duration) {
		Map<String, Object> claims = Map.of("id", 1L);
		return "Bearer " + tokenManager.generateToken(claims, tokenProperties.getIssuer(),
			duration, Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes()));
	}

}
