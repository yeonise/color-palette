package com.yeon.colorpalette.exception.member;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class InvalidTokenException extends CustomException {

	public InvalidTokenException() {
		super(ErrorType.INVALID_TOKEN);
	}

}
