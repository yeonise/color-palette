package com.yeon.colorpalette.exception.member;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class NicknameAlreadyInUseException extends CustomException {

	public NicknameAlreadyInUseException() {
		super(ErrorType.NICKNAME_ALREADY_IN_USE);
	}

}
