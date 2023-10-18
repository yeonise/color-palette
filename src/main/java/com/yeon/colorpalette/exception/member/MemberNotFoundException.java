package com.yeon.colorpalette.exception.member;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class MemberNotFoundException extends CustomException {

	public MemberNotFoundException() {
		super(ErrorType.MEMBER_NOT_FOUND);
	}

}
