package com.yeon.colorpalette.palette.application.response;

import java.time.LocalDateTime;

import com.yeon.colorpalette.palette.domain.Palette;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaletteCreateResponse {

	private Long id;
	private String creator;
	private String color1;
	private String color2;
	private String color3;
	private String color4;
	private String tagName;
	private LocalDateTime createdAt;

	public static PaletteCreateResponse from(Palette palette) {
		return new PaletteCreateResponse(
			palette.getId(),
			palette.findCreator(),
			palette.getColor1(),
			palette.getColor2(),
			palette.getColor3(),
			palette.getColor4(),
			palette.findTagName(),
			palette.getCreatedAt()
		);
	}

}
