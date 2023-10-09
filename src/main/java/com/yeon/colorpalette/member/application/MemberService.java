package com.yeon.colorpalette.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public Member create(MemberCreateServiceRequest serviceRequest) {
		checkEmailAvailability(serviceRequest.getEmail());
		checkNicknameAvailability(serviceRequest.getNickname());
		return memberRepository.save(serviceRequest.toEntity());
	}

	@Transactional
	public void delete(Long id) {
		memberRepository.deleteById(id);
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
