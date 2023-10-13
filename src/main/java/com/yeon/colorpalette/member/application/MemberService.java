package com.yeon.colorpalette.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yeon.colorpalette.exception.member.EmailAlreadyInUseException;
import com.yeon.colorpalette.exception.member.InvalidLoginException;
import com.yeon.colorpalette.exception.member.NicknameAlreadyInUseException;
import com.yeon.colorpalette.member.application.request.MemberCreateServiceRequest;
import com.yeon.colorpalette.member.application.response.AccessTokenResponse;
import com.yeon.colorpalette.member.application.response.LoginResponse;
import com.yeon.colorpalette.member.domain.Account;
import com.yeon.colorpalette.member.domain.Member;
import com.yeon.colorpalette.member.infrastructure.MemberRepository;
import com.yeon.colorpalette.member.presentation.request.LoginRequest;

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
		return memberRepository.save(serviceRequest.toEntity());
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

	@Transactional
	public LoginResponse login(LoginRequest request) {
		Member member = memberRepository.findByEmailAndPasswordAndIsDeleted(request.getEmail(), request.getPassword(), false)
			.orElseThrow(InvalidLoginException::new);

		return authService.issueTokens(new Account(member.getId(), null));
	}

	@Transactional
	public void logout(Account account) {
		authService.saveInvalidAccessToken(account.getToken());
		authService.deleteRefreshToken(account.getId());
	}

	public AccessTokenResponse reissueAccessToken(Account account) {
		return authService.reissueAccessToken(account);
	}

}
