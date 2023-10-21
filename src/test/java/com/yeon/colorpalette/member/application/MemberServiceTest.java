package com.yeon.colorpalette.member.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import com.yeon.colorpalette.IntegrationTestSupport;
import com.yeon.colorpalette.auth.application.response.LoginResponse;
import com.yeon.colorpalette.auth.domain.Account;
import com.yeon.colorpalette.auth.presentation.request.LoginRequest;
import com.yeon.colorpalette.exception.ErrorType;
import com.yeon.colorpalette.exception.auth.InvalidLoginException;
import com.yeon.colorpalette.exception.auth.InvalidTokenException;
import com.yeon.colorpalette.exception.member.EmailAlreadyInUseException;
import com.yeon.colorpalette.exception.member.NicknameAlreadyInUseException;
import com.yeon.colorpalette.exception.palette.BookmarkAlreadyExistsException;
import com.yeon.colorpalette.exception.palette.PaletteNotFoundException;
import com.yeon.colorpalette.member.application.request.MemberCreateServiceRequest;
import com.yeon.colorpalette.member.domain.Member;
import com.yeon.colorpalette.palette.domain.Palette;

class MemberServiceTest extends IntegrationTestSupport {

	@DisplayName("일반 회원가입 시나리오")
	@TestFactory
	Collection<DynamicTest> createMember() {

		return List.of(
			dynamicTest("미가입 정보로 회원가입 요청 시 사용자 생성에 성공한다", () -> {
				// given
				MemberCreateServiceRequest serviceRequest =
					new MemberCreateServiceRequest("white@email.com", "white", "white100!");

				// when
				Member member = memberService.create(serviceRequest);

				// then
				assertAll(
					() -> assertThat(member.getEmail()).isEqualTo(serviceRequest.getEmail()),
					() -> assertThat(member.getNickname()).isEqualTo(serviceRequest.getNickname())
				);
			}),
			dynamicTest("가입된 이메일로 회원가입 요청 시 예외가 발생한다", () -> {
				// given
				MemberCreateServiceRequest serviceRequest =
					new MemberCreateServiceRequest("white@email.com", "black", "black100!");

				// when & then
				assertThatThrownBy(() -> memberService.create(serviceRequest))
					.isInstanceOf(EmailAlreadyInUseException.class)
					.hasMessage(ErrorType.EMAIL_ALREADY_IN_USE.getMessage());
			}),
			dynamicTest("가입된 닉네임으로 회원가입 요청 시 예외가 발생한다", () -> {
				// given
				MemberCreateServiceRequest serviceRequest =
					new MemberCreateServiceRequest("black@email.com", "white", "black100!");

				// when & then
				assertThatThrownBy(() -> memberService.create(serviceRequest))
					.isInstanceOf(NicknameAlreadyInUseException.class)
					.hasMessage(ErrorType.NICKNAME_ALREADY_IN_USE.getMessage());
			})
		);
	}

	@DisplayName("회원탈퇴 시나리오")
	@TestFactory
	Collection<DynamicTest> deleteMember() {
		// given
		Member member = makeMemberFixture();

		LoginRequest loginRequest = LoginRequest.builder()
			.email("white@email.com")
			.password("white100!")
			.build();

		LoginResponse loginResponse = authService.login(loginRequest);

		return List.of(
			dynamicTest("회원탈퇴에 성공하면 기존 정보로 로그인 요청 시 에러가 발생한다", () -> {
					// when
					memberService.delete(new Account(member.getId(), loginResponse.getAccessToken()));

					// then
					assertThatThrownBy(() -> authService.login(loginRequest))
						.isInstanceOf(InvalidLoginException.class);
				}
			),
			dynamicTest("기존 사용자의 Refresh 토큰을 무효화한다", () -> {
				// when & then
				assertThatThrownBy(
					() -> authService.reissueAccessToken(new Account(member.getId(), loginResponse.getRefreshToken())))
					.isInstanceOf(InvalidTokenException.class);
			})
		);
	}

	@DisplayName("북마크 생성 및 삭제 시나리오")
	@TestFactory
	Collection<DynamicTest> bookmarks() {
		// given
		Member member = makeMemberFixture();
		Palette palette = makePaletteFixture(member, "DDDDDD", false);

		return List.of(
			dynamicTest("북마크 생성에 성공한다", () -> {
				// when
				boolean registered = memberService.registerBookmark(palette.getId(), member.getId());

				// then
				assertThat(registered).isTrue();
			}),
			dynamicTest("삭제된 팔레트로 북마크 생성을 시도하는 경우 에러가 발생한다", () -> {
				// given
				Palette deletedPalette = makePaletteFixture(member, "FFFFFF", true);

				// when & then
				assertThatThrownBy(() -> memberService.registerBookmark(deletedPalette.getId(), member.getId()))
					.isInstanceOf(PaletteNotFoundException.class);
			}),
			dynamicTest("이미 북마크에 등록된 팔레트로 북마크 생성을 시도하는 경우 에러가 발생한다", () -> {
				// when & then
				assertThatThrownBy(() -> memberService.registerBookmark(palette.getId(), member.getId()))
					.isInstanceOf(BookmarkAlreadyExistsException.class);
			}),
			dynamicTest("북마크 삭제에 성공한다", () -> {
				// when
				boolean unregistered = memberService.unregisterBookmark(1L, member.getId());

				// then
				assertThat(unregistered).isTrue();
			})
		);
	}

}
