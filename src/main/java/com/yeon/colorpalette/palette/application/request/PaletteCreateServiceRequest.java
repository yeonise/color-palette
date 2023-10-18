package com.yeon.colorpalette.palette.application.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaletteCreateServiceRequest {

	private Long memberId;
	private String color1;
	private String color2;
	private String color3;
	private String color4;
	private Long tagId;

}
