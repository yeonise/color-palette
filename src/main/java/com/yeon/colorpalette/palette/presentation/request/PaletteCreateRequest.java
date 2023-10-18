package com.yeon.colorpalette.palette.presentation.request;

import com.yeon.colorpalette.palette.application.request.PaletteCreateServiceRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PaletteCreateRequest {

	@NotNull(message = "색상 코드가 존재하지 않습니다")
	@Pattern(regexp = "^([A-Fa-f0-9]{6})$", message = "유효한 색상 코드 형식이 아닙니다")
	private String color1;

	@NotNull(message = "색상 코드가 존재하지 않습니다")
	@Pattern(regexp = "^([A-Fa-f0-9]{6})$", message = "유효한 색상 코드 형식이 아닙니다")
	private String color2;

	@NotNull(message = "색상 코드가 존재하지 않습니다")
	@Pattern(regexp = "^([A-Fa-f0-9]{6})$", message = "유효한 색상 코드 형식이 아닙니다")
	private String color3;

	@NotNull(message = "색상 코드가 존재하지 않습니다")
	@Pattern(regexp = "^([A-Fa-f0-9]{6})$", message = "유효한 색상 코드 형식이 아닙니다")
	private String color4;

	private Long tagId;

	public PaletteCreateServiceRequest toService(Long memberId) {
		return new PaletteCreateServiceRequest(memberId, color1, color2, color3, color4, tagId);
	}

	@Builder
	private PaletteCreateRequest(String color1, String color2, String color3, String color4, Long tagId) {
		this.color1 = color1;
		this.color2 = color2;
		this.color3 = color3;
		this.color4 = color4;
		this.tagId = tagId;
	}

}
