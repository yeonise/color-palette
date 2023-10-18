package com.yeon.colorpalette.exception.palette;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class TagNotFoundException extends CustomException {

	public TagNotFoundException() {
		super(ErrorType.TAG_NOT_FOUND);
	}

}
