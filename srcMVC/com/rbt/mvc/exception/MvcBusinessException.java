package com.rbt.mvc.exception;

import com.rbt.exception.BaseException;


/**
 * MvcBusinessException
 * @author Allen
 */
public class MvcBusinessException extends BaseException {

	private static final long serialVersionUID = 1L;

	public MvcBusinessException(String errorMsg) {
		super(errorMsg, null, null, null);
	}

	public MvcBusinessException(String errorMsg, Exception e) {
		super(errorMsg, null, null, e);
	}

	public MvcBusinessException(String errorCode, Object[] parameters, Exception e) {
		super(null, errorCode, parameters, e);
	}
}
