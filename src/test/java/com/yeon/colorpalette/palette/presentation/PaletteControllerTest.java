package com.yeon.colorpalette.palette.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.yeon.colorpalette.RestDocsSupport;
import com.yeon.colorpalette.auth.domain.Account;
import com.yeon.colorpalette.exception.member.MemberNotFoundException;
import com.yeon.colorpalette.exception.palette.InvalidPaletteCreatorException;
import com.yeon.colorpalette.exception.palette.PaletteAlreadyExistsException;
import com.yeon.colorpalette.exception.palette.PaletteNotFoundException;
import com.yeon.colorpalette.exception.palette.TagNotFoundException;
import com.yeon.colorpalette.palette.application.request.PaletteCreateServiceRequest;
import com.yeon.colorpalette.palette.application.response.PaletteCreateResponse;
import com.yeon.colorpalette.palette.presentation.request.PaletteCreateRequest;

class PaletteControllerTest extends RestDocsSupport {

	@DisplayName("팔레트 생성 API")
	@Test
	void createPalette() throws Exception {
		// given
		PaletteCreateRequest request = PaletteCreateRequest.builder()
			.color1("FFFFFF")
			.color2("FFFFFF")
			.color3("FFFFFF")
			.color4("FFFFFF")
			.tagId(1L)
			.build();

		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		given(paletteService.create(any(PaletteCreateServiceRequest.class)))
			.willReturn(new PaletteCreateResponse(1L, "white", "FFFFFF", "FFFFFF", "FFFFFF", "FFFFFF", "RED",
				LocalDateTime.of(2023, 12, 27, 12, 0, 0, 0)));

		// when & then
		mockMvc.perform(post("/api/palettes")
				.header(HttpHeaders.AUTHORIZATION, makeAuthorizationHeader(60000L))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("palette-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("color1").type(JsonFieldType.STRING).description("첫 번째 색"),
					fieldWithPath("color2").type(JsonFieldType.STRING).description("두 번째 색"),
					fieldWithPath("color3").type(JsonFieldType.STRING).description("세 번째 색"),
					fieldWithPath("color4").type(JsonFieldType.STRING).description("네 번째 색"),
					fieldWithPath("tagId").type(JsonFieldType.NUMBER).description("태그 아이디").optional()
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
					fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("아이디"),
					fieldWithPath("data.creator").type(JsonFieldType.STRING).description("생성자"),
					fieldWithPath("data.color1").type(JsonFieldType.STRING).description("첫 번째 색"),
					fieldWithPath("data.color2").type(JsonFieldType.STRING).description("두 번째 색"),
					fieldWithPath("data.color3").type(JsonFieldType.STRING).description("세 번째 색"),
					fieldWithPath("data.color4").type(JsonFieldType.STRING).description("네 번째 색"),
					fieldWithPath("data.tagName").type(JsonFieldType.STRING).description("태그 이름").optional(),
					fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일")
				)));
	}

	@DisplayName("팔레트 생성 API with Bind Exception")
	@Test
	void createPaletteWithBindException() throws Exception {
		// given
		PaletteCreateRequest request = PaletteCreateRequest.builder()
			.color1("FFFFFG")
			.color2("FFFGFF")
			.color3("FGFFFF")
			.color4("FF")
			.tagId(1L)
			.build();

		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		// when & then
		mockMvc.perform(post("/api/palettes")
				.header(HttpHeaders.AUTHORIZATION, makeAuthorizationHeader(60000L))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("palette-create-bind-exception",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("color1").type(JsonFieldType.STRING).description("첫 번째 색"),
					fieldWithPath("color2").type(JsonFieldType.STRING).description("두 번째 색"),
					fieldWithPath("color3").type(JsonFieldType.STRING).description("세 번째 색"),
					fieldWithPath("color4").type(JsonFieldType.STRING).description("네 번째 색"),
					fieldWithPath("tagId").type(JsonFieldType.NUMBER).description("태그 아이디").optional()
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

	@DisplayName("팔레트 생성 API with No Color")
	@Test
	void createPaletteWithNoColor() throws Exception {
		// given
		PaletteCreateRequest request = PaletteCreateRequest.builder()
			.color1(" ")
			.color2("  ")
			.color3("AAAAAG")
			.tagId(1L)
			.build();

		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		// when & then
		mockMvc.perform(post("/api/palettes")
				.header(HttpHeaders.AUTHORIZATION, makeAuthorizationHeader(60000L))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("palette-create-no-color",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("color1").type(JsonFieldType.STRING).description("첫 번째 색"),
					fieldWithPath("color2").type(JsonFieldType.STRING).description("두 번째 색"),
					fieldWithPath("color3").type(JsonFieldType.STRING).description("세 번째 색"),
					fieldWithPath("color4").type(JsonFieldType.NULL).description("네 번째 색"),
					fieldWithPath("tagId").type(JsonFieldType.NUMBER).description("태그 아이디").optional()
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

	@DisplayName("팔레트 생성 API with Palette Already Exists")
	@Test
	void createPaletteAlreadyExists() throws Exception {
		// given
		PaletteCreateRequest request = PaletteCreateRequest.builder()
			.color1("FFFFFF")
			.color2("FFFFFF")
			.color3("FFFFFF")
			.color4("FFFFFF")
			.tagId(1L)
			.build();

		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		given(paletteService.create(any(PaletteCreateServiceRequest.class)))
			.willThrow(new PaletteAlreadyExistsException());

		// when & then
		mockMvc.perform(post("/api/palettes")
				.header(HttpHeaders.AUTHORIZATION, makeAuthorizationHeader(60000L))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("palette-create-already-exists",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("color1").type(JsonFieldType.STRING).description("첫 번째 색"),
					fieldWithPath("color2").type(JsonFieldType.STRING).description("두 번째 색"),
					fieldWithPath("color3").type(JsonFieldType.STRING).description("세 번째 색"),
					fieldWithPath("color4").type(JsonFieldType.STRING).description("네 번째 색"),
					fieldWithPath("tagId").type(JsonFieldType.NUMBER).description("태그 아이디").optional()
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	@DisplayName("팔레트 생성 API with Member Not Found Exception")
	@Test
	void createPaletteWithMemberNotFoundException() throws Exception {
		// given
		PaletteCreateRequest request = PaletteCreateRequest.builder()
			.color1("FFFFFF")
			.color2("FFFFFF")
			.color3("FFFFFF")
			.color4("FFFFFF")
			.tagId(1L)
			.build();

		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		given(paletteService.create(any(PaletteCreateServiceRequest.class)))
			.willThrow(new MemberNotFoundException());

		// when & then
		mockMvc.perform(post("/api/palettes")
				.header(HttpHeaders.AUTHORIZATION, makeAuthorizationHeader(60000L))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("palette-create-member-not-found",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("color1").type(JsonFieldType.STRING).description("첫 번째 색"),
					fieldWithPath("color2").type(JsonFieldType.STRING).description("두 번째 색"),
					fieldWithPath("color3").type(JsonFieldType.STRING).description("세 번째 색"),
					fieldWithPath("color4").type(JsonFieldType.STRING).description("네 번째 색"),
					fieldWithPath("tagId").type(JsonFieldType.NUMBER).description("태그 아이디").optional()
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	@DisplayName("팔레트 생성 API with Tag Not Found Exception")
	@Test
	void createPaletteWithTagNotFoundException() throws Exception {
		// given
		PaletteCreateRequest request = PaletteCreateRequest.builder()
			.color1("FFFFFF")
			.color2("FFFFFF")
			.color3("FFFFFF")
			.color4("FFFFFF")
			.tagId(10L)
			.build();

		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		given(paletteService.create(any(PaletteCreateServiceRequest.class)))
			.willThrow(new TagNotFoundException());

		// when & then
		mockMvc.perform(post("/api/palettes")
				.header(HttpHeaders.AUTHORIZATION, makeAuthorizationHeader(60000L))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("palette-create-tag-not-found",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("color1").type(JsonFieldType.STRING).description("첫 번째 색"),
					fieldWithPath("color2").type(JsonFieldType.STRING).description("두 번째 색"),
					fieldWithPath("color3").type(JsonFieldType.STRING).description("세 번째 색"),
					fieldWithPath("color4").type(JsonFieldType.STRING).description("네 번째 색"),
					fieldWithPath("tagId").type(JsonFieldType.NUMBER).description("태그 아이디").optional()
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	@DisplayName("팔레트 삭제 API")
	@Test
	void deletePalette() throws Exception {
		// given
		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		// when & then
		mockMvc.perform(delete("/api/palettes/1")
				.header(HttpHeaders.AUTHORIZATION, makeAuthorizationHeader(60000L)))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("palette-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	@DisplayName("팔레트 삭제 API with Palette Not Found Exception")
	@Test
	void deletePaletteWithPaletteNotFoundException() throws Exception {
		// given
		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		given(paletteService.delete(anyLong(), anyLong()))
			.willThrow(new PaletteNotFoundException());

		// when & then
		mockMvc.perform(delete("/api/palettes/100")
				.header(HttpHeaders.AUTHORIZATION, makeAuthorizationHeader(60000L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("palette-delete-not-found",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)));
	}

	@DisplayName("팔레트 삭제 API with Invalid Creator Exception")
	@Test
	void deletePaletteWithInvalidCreatorException() throws Exception {
		// given
		given(authService.extractAccount(anyString()))
			.willReturn(new Account(1L, null));

		given(paletteService.delete(anyLong(), anyLong()))
			.willThrow(new InvalidPaletteCreatorException());

		// when & then
		mockMvc.perform(delete("/api/palettes/1")
				.header(HttpHeaders.AUTHORIZATION, makeAuthorizationHeader(60000L)))
			.andDo(print())
			.andExpect(status().isForbidden())
			.andDo(document("palette-delete-invalid-creator",
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
