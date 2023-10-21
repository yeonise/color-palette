package com.yeon.colorpalette.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yeon.colorpalette.auth.application.AuthService;
import com.yeon.colorpalette.auth.domain.Account;
import com.yeon.colorpalette.auth.domain.Provider;
import com.yeon.colorpalette.exception.member.EmailAlreadyInUseException;
import com.yeon.colorpalette.exception.member.MemberNotFoundException;
import com.yeon.colorpalette.exception.member.NicknameAlreadyInUseException;
import com.yeon.colorpalette.exception.palette.PaletteNotFoundException;
import com.yeon.colorpalette.member.application.request.MemberCreateServiceRequest;
import com.yeon.colorpalette.member.domain.Bookmark;
import com.yeon.colorpalette.member.domain.Member;
import com.yeon.colorpalette.member.infrastructure.MemberRepository;
import com.yeon.colorpalette.palette.domain.Palette;
import com.yeon.colorpalette.palette.infrastructure.PaletteRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final PaletteRepository paletteRepository;
	private final AuthService authService;

	@Transactional
	public Member create(MemberCreateServiceRequest serviceRequest) {
		checkEmailAvailability(serviceRequest.getEmail());
		checkNicknameAvailability(serviceRequest.getNickname());
		return memberRepository.save(serviceRequest.toEntity(Provider.BASIC));
	}

	@Transactional
	public int delete(Account account) {
		authService.saveInvalidAccessToken(account.getToken());
		authService.deleteRefreshToken(account.getId());
		return memberRepository.deleteSoftlyById(account.getId());
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
	public boolean registerBookmark(Long paletteId, Long memberId) {
		Palette palette = paletteRepository.findByIdAndIsDeleted(paletteId, false)
			.orElseThrow(PaletteNotFoundException::new);
		Member member = memberRepository.findByIdAndIsDeleted(memberId, false)
			.orElseThrow(MemberNotFoundException::new);

		return member.registerBookmark(new Bookmark(null, member, palette));
	}

	@Transactional
	public boolean unregisterBookmark(Long bookmarkId, Long memberId) {
		Member member = memberRepository.findByIdAndIsDeleted(memberId, false)
			.orElseThrow(MemberNotFoundException::new);

		return member.unregisterBookmark(bookmarkId);
	}

}
