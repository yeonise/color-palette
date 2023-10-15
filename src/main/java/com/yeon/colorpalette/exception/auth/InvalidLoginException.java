package com.yeon.colorpalette.exception.auth;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class InvalidLoginException extends CustomException {

	public InvalidLoginException() {
		super(ErrorType.INVALID_LOGIN);
	}

}
