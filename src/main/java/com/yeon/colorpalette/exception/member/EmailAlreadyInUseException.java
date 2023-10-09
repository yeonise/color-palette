package com.yeon.colorpalette.exception.member;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class EmailAlreadyInUseException extends CustomException {

	public EmailAlreadyInUseException() {
		super(ErrorType.EMAIL_ALREADY_IN_USE);
	}

}
