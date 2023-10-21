package com.yeon.colorpalette;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.yeon.colorpalette.auth.application.AuthService;
import com.yeon.colorpalette.member.application.MemberService;
import com.yeon.colorpalette.member.application.request.MemberCreateServiceRequest;
import com.yeon.colorpalette.member.domain.Member;
import com.yeon.colorpalette.palette.application.PaletteService;
import com.yeon.colorpalette.palette.application.request.PaletteCreateServiceRequest;
import com.yeon.colorpalette.palette.domain.Palette;
import com.yeon.colorpalette.palette.infrastructure.PaletteRepository;

@Sql(value = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

	@Autowired
	protected AuthService authService;

	@Autowired
	protected MemberService memberService;

	@Autowired
	protected PaletteService paletteService;

	@Autowired
	PaletteRepository paletteRepository;

	protected Member makeMemberFixture() {
		return memberService.create(new MemberCreateServiceRequest("white@email.com", "white", "white100!"));
	}

	protected Palette makePaletteFixture(Member member, String color, boolean isDeleted) {
		return paletteRepository.save(new Palette(null, member, "AAAAAA", "BBBBBB", "CCCCCC", color,
			"AAAAAABBBBBBCCCCCC" + color, null, 0, LocalDateTime.now(), isDeleted));
	}

	protected PaletteCreateServiceRequest makePaletteCreateServiceRequest(List<String> colors, Long memberId, Long tagId) {
		return new PaletteCreateServiceRequest(memberId, colors.get(0), colors.get(1), colors.get(2), colors.get(3), tagId);
	}

}
