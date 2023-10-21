package com.yeon.colorpalette.exception.palette;

import com.yeon.colorpalette.exception.CustomException;
import com.yeon.colorpalette.exception.ErrorType;

public class BookmarkAlreadyExistsException extends CustomException {

	public BookmarkAlreadyExistsException() {
		super(ErrorType.BOOKMARK_ALREADY_EXISTS);
	}

}
