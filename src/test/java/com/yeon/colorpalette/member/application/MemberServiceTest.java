package com.yeon.colorpalette.member.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.yeon.colorpalette.IntegrationTestSupport;
import com.yeon.colorpalette.exception.ErrorType;
import com.yeon.colorpalette.exception.member.EmailAlreadyInUseException;
import com.yeon.colorpalette.exception.member.NicknameAlreadyInUseException;
import com.yeon.colorpalette.member.application.request.MemberCreateServiceRequest;
import com.yeon.colorpalette.member.domain.Member;

class MemberServiceTest extends IntegrationTestSupport {

	@Autowired
	MemberService memberService;

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

}
