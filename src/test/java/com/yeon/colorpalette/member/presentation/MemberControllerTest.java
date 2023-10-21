package com.yeon.colorpalette.member.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.yeon.colorpalette.RestDocsSupport;
import com.yeon.colorpalette.auth.domain.Account;
import com.yeon.colorpalette.exception.member.EmailAlreadyInUseException;
import com.yeon.colorpalette.exception.member.NicknameAlreadyInUseException;
import com.yeon.colorpalette.exception.palette.BookmarkAlreadyExistsException;
import com.yeon.colorpalette.member.application.request.MemberCreateServiceRequest;
import com.yeon.colorpalette.member.presentation.request.MemberCreateRequest;

class MemberControllerTest extends RestDocsSupport {

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

		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

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

	@DisplayName("북마크 생성 API")
	@Test
	void registerBookmark() throws Exception {
		String authorizationHeader = makeAuthorizationHeader(60000L);

		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		// when & then
		mockMvc.perform(post("/api/members/bookmarks/1")
				.header(HttpHeaders.AUTHORIZATION, authorizationHeader))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("bookmark-register",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	@DisplayName("북마크 생성 API with Bookmark Already Exists Exception")
	@Test
	void registerBookmarkWithBookmarkAlreadyExistsException() throws Exception {
		String authorizationHeader = makeAuthorizationHeader(60000L);

		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		given(memberService.registerBookmark(anyLong(), anyLong()))
			.willThrow(new BookmarkAlreadyExistsException());

		// when & then
		mockMvc.perform(post("/api/members/bookmarks/1")
				.header(HttpHeaders.AUTHORIZATION, authorizationHeader))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("bookmark-register-already-exists",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	@DisplayName("북마크 삭제 API")
	@Test
	void unregisterBookmark() throws Exception {
		String authorizationHeader = makeAuthorizationHeader(60000L);

		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		// when & then
		mockMvc.perform(delete("/api/members/bookmarks/1")
				.header(HttpHeaders.AUTHORIZATION, authorizationHeader))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("bookmark-unregister",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

}
