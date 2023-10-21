package com.yeon.colorpalette.member.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yeon.colorpalette.auth.domain.Provider;
import com.yeon.colorpalette.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);
	boolean existsByNickname(String nickname);
	Optional<Member> findByEmailAndPasswordAndIsDeleted(String email, String password, boolean isDeleted);
	Optional<Member> findByEmailAndProviderAndIsDeleted(String email, Provider provider, boolean isDeleted);
	Optional<Member> findByIdAndIsDeleted(long memberId, boolean isDeleted);

	@Modifying
	@Query(value = "UPDATE Member m SET m.isDeleted = true WHERE id = :id ")
	int deleteSoftlyById(long id);

}
