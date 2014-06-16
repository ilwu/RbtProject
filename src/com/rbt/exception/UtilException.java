package com.rbt.exception;


/**
 * UtilException
 * @author Allen
 */
public class UtilException extends BaseException {

	private static final long serialVersionUID = 1L;

	public UtilException(String errorMsg) {
		super(errorMsg, null, null, null);
	}

	public UtilException(String errorMsg, Exception e) {
		super(errorMsg, null, null, e);
	}

	public UtilException(String errorCode, Object[] parameters, Exception e) {
		super(null, errorCode, parameters, e);
	}
}
