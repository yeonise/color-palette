package com.yeon.colorpalette.member.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yeon.colorpalette.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);
	boolean existsByNickname(String nickname);

}
