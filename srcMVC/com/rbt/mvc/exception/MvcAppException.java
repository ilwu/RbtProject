package com.rbt.mvc.exception;

import com.rbt.exception.BaseException;


/**
 * MvcAppException
 * @author Allen
 */
public class MvcAppException extends BaseException {

	private static final long serialVersionUID = 1L;

	public MvcAppException(String errorMsg) {
		super(errorMsg, null, null, null);
	}

	public MvcAppException(String errorMsg, Exception e) {
		super(errorMsg, null, null, e);
	}

	public MvcAppException(String errorCode, Object[] parameters, Exception e) {
		super(null, errorCode, parameters, e);
	}
}
