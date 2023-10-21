package com.yeon.colorpalette.exception.palette;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class InvalidPaletteCreatorException extends CustomException {

	public InvalidPaletteCreatorException() {
		super(ErrorType.INVALID_PALETTE_CREATOR);
	}

}
