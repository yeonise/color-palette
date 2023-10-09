package com.yeon.colorpalette.member.presentation.request;

import com.yeon.colorpalette.member.application.request.MemberCreateServiceRequest;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberCreateRequest {

	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
		message = "이메일 형식이 올바르지 않습니다")
	private String email;

	@Pattern(regexp = "^(?=.*[a-zA-Z가-힣])[a-zA-Z가-힣0-9]{2,20}$",
		message = "닉네임은 2자 이상 20자 이하로 영문 또는 한글을 포함해야 합니다")
	private String nickname;

	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$",
		message = "비밀번호는 8자 이상 20자 이하로 영문, 숫자, 특수문자를 최소 1개씩 포함해야 합니다")
	private String password;

	public MemberCreateServiceRequest toService() {
		return new MemberCreateServiceRequest(email, nickname, password);
	}

	@Builder
	private MemberCreateRequest(String email, String nickname, String password) {
		this.email = email;
		this.nickname = nickname;
		this.password = password;
	}

}
