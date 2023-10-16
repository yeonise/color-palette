package com.yeon.colorpalette.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yeon.colorpalette.auth.application.AuthService;
import com.yeon.colorpalette.auth.domain.Account;
import com.yeon.colorpalette.auth.domain.Provider;
import com.yeon.colorpalette.exception.member.EmailAlreadyInUseException;
import com.yeon.colorpalette.exception.member.NicknameAlreadyInUseException;
import com.yeon.colorpalette.member.application.request.MemberCreateServiceRequest;
import com.yeon.colorpalette.member.domain.Member;
import com.yeon.colorpalette.member.infrastructure.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final AuthService authService;

	@Transactional
	public Member create(MemberCreateServiceRequest serviceRequest) {
		checkEmailAvailability(serviceRequest.getEmail());
		checkNicknameAvailability(serviceRequest.getNickname());
		return memberRepository.save(serviceRequest.toEntity(Provider.BASIC));
	}

	@Transactional
	public void delete(Account account) {
		authService.saveInvalidAccessToken(account.getToken());
		authService.deleteRefreshToken(account.getId());
		memberRepository.deleteSoftlyById(account.getId());
	}

	private void checkEmailAvailability(String email) {
		if (memberRepository.existsByEmail(email)) {
			throw new EmailAlreadyInUseException();
		}
	}

	private void checkNicknameAvailability(String nickname) {
		if (memberRepository.existsByNickname(nickname)) {
			throw new NicknameAlreadyInUseException();
		}
	}

}
