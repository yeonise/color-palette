package com.yeon.colorpalette.exception.member;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class ExpiredTokenException extends CustomException {

	public ExpiredTokenException() {
		super(ErrorType.EXPIRED_TOKEN);
	}

}
