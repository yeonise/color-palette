package com.yeon.colorpalette.exception.auth;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class AuthorizationHeaderException extends CustomException {

	public AuthorizationHeaderException() {
		super(ErrorType.INVALID_AUTHORIZATION_HEADER);
	}

}
