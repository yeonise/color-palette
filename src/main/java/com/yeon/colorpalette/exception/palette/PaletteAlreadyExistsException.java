package com.yeon.colorpalette.exception.palette;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class PaletteAlreadyExistsException extends CustomException {

	public PaletteAlreadyExistsException() {
		super(ErrorType.PALETTE_ALREADY_EXISTS);
	}

}
