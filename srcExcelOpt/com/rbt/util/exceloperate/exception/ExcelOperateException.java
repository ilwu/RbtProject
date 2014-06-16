package com.rbt.util.exceloperate.exception;

import com.rbt.exception.BaseException;

/**
 * Excel Operate 套件處理錯誤時拋出
 * @author Allen
 */
public class ExcelOperateException extends BaseException {

	/**
	 *
	 */
	private static final long serialVersionUID = 6519843988004132472L;

	/**
	 * @param errorMsg
	 */
	public ExcelOperateException(String errorMsg) {
		super(errorMsg, null, null, null);
	}

	/**
	 * @param errorMsg
	 * @param e
	 */
	public ExcelOperateException(String errorMsg, Exception e) {
		super(errorMsg, null, null, e);
	}

	/**
	 * @param errorCode
	 * @param parameters
	 * @param e
	 */
	public ExcelOperateException(String errorCode, Object[] parameters, Exception e) {
		super(null, errorCode, parameters, e);
	}
}
