package com.yeon.colorpalette.palette.application.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaletteReadSliceResponse {

	private boolean hasNext;
	private List<PaletteReadResponse> palettes;

}
