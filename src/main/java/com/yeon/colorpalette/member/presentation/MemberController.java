package com.yeon.colorpalette.member.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yeon.colorpalette.api.ApiResponse;
import com.yeon.colorpalette.api.ResponseMessage;
import com.yeon.colorpalette.auth.domain.Account;
import com.yeon.colorpalette.config.resolver.Login;
import com.yeon.colorpalette.member.application.MemberService;
import com.yeon.colorpalette.member.presentation.request.MemberCreateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

	private final MemberService memberService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<Void> createMember(@Valid @RequestBody MemberCreateRequest request) {
		memberService.create(request.toService());
		return ApiResponse.noData(HttpStatus.CREATED, ResponseMessage.MEMBER_CREATE_SUCCESS.getMessage());
	}

	@DeleteMapping
	public ApiResponse<Void> deleteMember(@Login Account account) {
		memberService.delete(account);
		return ApiResponse.noData(HttpStatus.OK, ResponseMessage.MEMBER_DELETE_SUCCESS.getMessage());
	}

}
