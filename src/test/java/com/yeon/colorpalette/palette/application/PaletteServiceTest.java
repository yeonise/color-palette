package com.yeon.colorpalette.palette.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.PageRequest;

import com.yeon.colorpalette.IntegrationTestSupport;
import com.yeon.colorpalette.exception.member.MemberNotFoundException;
import com.yeon.colorpalette.exception.palette.InvalidPaletteCreatorException;
import com.yeon.colorpalette.exception.palette.PaletteAlreadyExistsException;
import com.yeon.colorpalette.exception.palette.PaletteNotFoundException;
import com.yeon.colorpalette.exception.palette.TagNotFoundException;
import com.yeon.colorpalette.member.domain.Member;
import com.yeon.colorpalette.palette.application.request.PaletteCreateServiceRequest;
import com.yeon.colorpalette.palette.application.response.PaletteCreateResponse;
import com.yeon.colorpalette.palette.application.response.PaletteReadSliceResponse;

class PaletteServiceTest extends IntegrationTestSupport {

	@DisplayName("팔레트 생성 시나리오")
	@TestFactory
	Collection<DynamicTest> create() {
		// given
		Member member = makeMemberFixture();

		return List.of(
			dynamicTest("새로운 팔레트 생성에 성공한다", () -> {
				// given
				List<String> colors = List.of("AAAAAA", "BBBBBB", "CCCCCC", "DDDDDD");
				PaletteCreateServiceRequest request = makePaletteCreateServiceRequest(colors, member.getId(), null);

				// when
				PaletteCreateResponse paletteCreateResponse = paletteService.create(request);

				// then
				assertAll(
					() -> assertThat(paletteCreateResponse.getCreator()).isEqualTo(member.getNickname()),
					() -> assertThat(paletteCreateResponse.getColor1()).isEqualTo(request.getColor1()),
					() -> assertThat(paletteCreateResponse.getColor2()).isEqualTo(request.getColor2()),
					() -> assertThat(paletteCreateResponse.getColor3()).isEqualTo(request.getColor3()),
					() -> assertThat(paletteCreateResponse.getColor4()).isEqualTo(request.getColor4()),
					() -> assertThat(paletteCreateResponse.getTagName()).isNull()
				);
			}),
			dynamicTest("이미 등록된 팔레트 생성을 시도하는 경우 예외가 발생한다", () -> {
				// given
				List<String> colors = List.of("DDDDDD", "AAAAAA", "CCCCCC", "BBBBBB");
				PaletteCreateServiceRequest request = makePaletteCreateServiceRequest(colors, member.getId(), null);

				// when & then
				assertThatThrownBy(() -> paletteService.create(request))
					.isInstanceOf(PaletteAlreadyExistsException.class);
			}),
			dynamicTest("존재하지 않는 사용자의 팔레트 생성을 시도하는 경우 예외가 발생한다", () -> {
				// given
				List<String> colors = List.of("AAAAAB", "BBBBBB", "CCCCCC", "DDDDDD");
				PaletteCreateServiceRequest request = makePaletteCreateServiceRequest(colors, 2L, null);

				// when & then
				assertThatThrownBy(() -> paletteService.create(request))
					.isInstanceOf(MemberNotFoundException.class);
			}),
			dynamicTest("존재하지 않는 태그를 추가하여 팔레트 생성을 시도하는 경우 예외가 발생한다", () -> {
				// given
				List<String> colors = List.of("AAAAAB", "BBBBBB", "CCCCCC", "DDDDDD");
				PaletteCreateServiceRequest request = makePaletteCreateServiceRequest(colors, member.getId(), 1L);

				// when & then
				assertThatThrownBy(() -> paletteService.create(request))
					.isInstanceOf(TagNotFoundException.class);
			}),
			dynamicTest("새로운 팔레트 생성에 성공한다", () -> {
				// given
				List<String> colors = List.of("AAAAAB", "BBBBBB", "CCCCCC", "DDDDDD");
				PaletteCreateServiceRequest request = makePaletteCreateServiceRequest(colors, member.getId(), null);

				// when
				PaletteCreateResponse paletteCreateResponse = paletteService.create(request);

				// then
				assertAll(
					() -> assertThat(paletteCreateResponse.getCreator()).isEqualTo(member.getNickname()),
					() -> assertThat(paletteCreateResponse.getColor1()).isEqualTo(request.getColor1()),
					() -> assertThat(paletteCreateResponse.getColor2()).isEqualTo(request.getColor2()),
					() -> assertThat(paletteCreateResponse.getColor3()).isEqualTo(request.getColor3()),
					() -> assertThat(paletteCreateResponse.getColor4()).isEqualTo(request.getColor4()),
					() -> assertThat(paletteCreateResponse.getTagName()).isNull()
				);
			})
		);
	}

	@DisplayName("팔레트 삭제 시나리오")
	@TestFactory
	Collection<DynamicTest> delete() {
		// given
		Member member = makeMemberFixture();

		List<String> colors = List.of("AAAAAA", "BBBBBB", "CCCCCC", "DDDDDD");
		PaletteCreateServiceRequest request = makePaletteCreateServiceRequest(colors, member.getId(), null);
		PaletteCreateResponse paletteCreateResponse = paletteService.create(request);

		return List.of(
			dynamicTest("존재하지 않는 팔레트 삭제 요청 시 예외가 발생한다", () -> {
				// when & then
				assertThatThrownBy(() -> paletteService.delete(2L, member.getId()))
					.isInstanceOf(PaletteNotFoundException.class);
			}),
			dynamicTest("팔레트 삭제를 요청한 사용자와 팔레트의 생성자가 일치하지 않는 경우 예외가 발생한다", () -> {
				// when & then
				assertThatThrownBy(() -> paletteService.delete(paletteCreateResponse.getId(), 2L))
					.isInstanceOf(InvalidPaletteCreatorException.class);
			}),
			dynamicTest("팔레트를 정상적으로 삭제한다", () -> {
				// when
				int deleted = paletteService.delete(paletteCreateResponse.getId(), member.getId());

				// then
				assertThat(deleted).isEqualTo(1);
			})
		);
	}

	@DisplayName("팔레트 조회 시나리오")
	@CsvSource({"AAAAAA,1,false", "111111,1,false", "DDDDDD,1,true", "A1A1A1,0,false"})
	@ParameterizedTest
	void read(String color, int size, boolean hasNext) {
		// given
		Member member = makeMemberFixture();

		List<String> colors1 = List.of("AAAAAA", "BBBBBB", "CCCCCC", "DDDDDD");
		List<String> colors2 = List.of("FFFFFF", "111111", "222222", "DDDDDD");
		paletteService.create(makePaletteCreateServiceRequest(colors1, member.getId(), null));
		paletteService.create(makePaletteCreateServiceRequest(colors2, member.getId(), null));

		PageRequest pageRequest = PageRequest.of(0, 1);

		// when
		PaletteReadSliceResponse sliceResponse = paletteService.read(pageRequest, color, null);

		// then
		assertAll(
			() -> assertThat(sliceResponse.getPalettes().size()).isEqualTo(size),
			() -> assertThat(sliceResponse.isHasNext()).isEqualTo(hasNext)
		);
	}

}
