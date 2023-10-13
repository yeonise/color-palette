package com.yeon.colorpalette.member.presentation;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.yeon.colorpalette.RestDocsSupport;
import com.yeon.colorpalette.exception.member.EmailAlreadyInUseException;
import com.yeon.colorpalette.exception.member.InvalidLoginException;
import com.yeon.colorpalette.exception.member.NicknameAlreadyInUseException;
import com.yeon.colorpalette.member.application.MemberService;
import com.yeon.colorpalette.member.application.TokenManager;
import com.yeon.colorpalette.member.application.TokenProperties;
import com.yeon.colorpalette.member.application.request.MemberCreateServiceRequest;
import com.yeon.colorpalette.member.application.response.LoginResponse;
import com.yeon.colorpalette.member.presentation.request.LoginRequest;
import com.yeon.colorpalette.member.presentation.request.MemberCreateRequest;

import io.jsonwebtoken.security.Keys;

class MemberControllerTest extends RestDocsSupport {

	@MockBean
	MemberService memberService;

	@Autowired
	TokenManager tokenManager;

	@Autowired
	TokenProperties tokenProperties;

	@DisplayName("일반 회원가입 API")
	@Test
	void createMember() throws Exception {
		// given
		MemberCreateRequest request = makeMemberCreateRequest("white@email.com", "white", "white100!");

		// when & then
		mockMvc.perform(post("/api/members")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("member-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	@DisplayName("일반 회원가입 API with Bind Exception")
	@Test
	void createMemberWithBindException() throws Exception {
		// given
		MemberCreateRequest request = makeMemberCreateRequest("white@email", "w", "white100");

		// when & then
		mockMvc.perform(post("/api/members")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("member-create-bind-exception",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.NULL).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.ARRAY).description("데이터"),
					fieldWithPath("data[].field").type(JsonFieldType.STRING).description("필드"),
					fieldWithPath("data[].message").type(JsonFieldType.STRING).description("에러 메시지")
				)));
	}

	@DisplayName("일반 회원가입 API with Email In Use")
	@Test
	void createMemberWithEmailInUse() throws Exception {
		// given
		MemberCreateRequest request = makeMemberCreateRequest("white@email.com", "black", "black100!");

		given(memberService.create(any(MemberCreateServiceRequest.class)))
			.willThrow(new EmailAlreadyInUseException());

		// when & then
		mockMvc.perform(post("/api/members")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("member-create-email-in-use",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	@DisplayName("일반 회원가입 API with Nickname In Use")
	@Test
	void createMemberWithNicknameInUse() throws Exception {
		// given
		MemberCreateRequest request = makeMemberCreateRequest("black@email.com", "white", "black100!");

		given(memberService.create(any(MemberCreateServiceRequest.class)))
			.willThrow(new NicknameAlreadyInUseException());

		// when & then
		mockMvc.perform(post("/api/members")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("member-create-nickname-in-use",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	private static MemberCreateRequest makeMemberCreateRequest(String email, String nickname, String password) {
		return MemberCreateRequest.builder()
			.email(email)
			.nickname(nickname)
			.password(password)
			.build();
	}

	@DisplayName("회원탈퇴 API")
	@Test
	void deleteMember() throws Exception {
		String authorizationHeader = makeAuthorizationHeader(60000L);

		// when & then
		mockMvc.perform(delete("/api/members")
				.header(HttpHeaders.AUTHORIZATION, authorizationHeader))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("member-delete",
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

	@DisplayName("로그아웃 API")
	@Test
	void logout() throws Exception {
		// given
		String authorizationHeader = makeAuthorizationHeader(6000L);

		// when & then
		mockMvc.perform(delete("/api/members/logout")
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

	private String makeAuthorizationHeader(long duration) {
		Map<String, Object> claims = Map.of("id", 1L);
		return "Bearer " + tokenManager.generateToken(claims, tokenProperties.getIssuer(),
			duration, Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes()));
	}

	@DisplayName("일반 로그인 API")
	@Test
	void login() throws Exception {
		// given
		LoginRequest request = makeLoginRequest("white@email.com", "white100!");

		given(memberService.login(any(LoginRequest.class)))
			.willReturn(
				new LoginResponse("accessToken will be displayed here.", "refreshToken will be displayed here."));

		// when & then
		mockMvc.perform(post("/api/members/login")
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

		given(memberService.login(any(LoginRequest.class)))
			.willThrow(new InvalidLoginException());

		// when & then
		mockMvc.perform(post("/api/members/login")
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

}
