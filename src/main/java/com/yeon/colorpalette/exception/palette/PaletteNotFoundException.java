package com.yeon.colorpalette.exception.palette;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class PaletteNotFoundException extends CustomException {

	public PaletteNotFoundException() {
		super(ErrorType.PALETTE_NOT_FOUND);
	}

}
