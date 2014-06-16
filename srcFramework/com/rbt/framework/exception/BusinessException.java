package com.rbt.framework.exception;

import com.rbt.exception.BaseException;

/**
 * 處理業務邏輯異常
 * @see com.rbt.framework.exception.BaseException
 *
 */
public class BusinessException extends BaseException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param String errorCode 錯誤代碼(設定檔中定義其message)
	 */
	public BusinessException(String errorCode) {
		super(null, errorCode, null, null);
	}

	/**
	 * Constructor
	 * @param String errorCode 錯誤代碼(設定檔中定義其message)
	 * @param Object[] parameters 參數列表(錯誤代碼對應之message需要的參數列表)
	 */
	public BusinessException(String errorCode, Object[] parameters) {
		super(null, errorCode, parameters, null);
	}

	/**
	 * Constructor
	 * @param String errorCode 錯誤代碼(設定檔中定義其message)
	 * @param Exception e 錯誤發生原因(原始Exception)
	 */
	public BusinessException(String errorCode, Exception e) {
		super(null, errorCode, null, e);
	}

	/**
	 * Constructor
	 * @param String errorCode 錯誤代碼(設定檔中定義其message)
	 * @param Object[] parameters 參數列表(錯誤代碼對應之message需要的參數列表)
	 * @param Exception e 錯誤發生原因(原始Exception)
	 */
	public BusinessException(String errorCode, Object[] parameters, Exception e) {
		super(null, errorCode, parameters, e);
	}

	/**
	 * Constructor
	 * @param String errorMsg 錯誤信息(For debug)
	 * @param String errorCode 錯誤代碼(設定檔中定義其message)
	 */
	public BusinessException(String errorMsg, String errorCode) {
		super(errorMsg, errorCode, null, null);
	}

	/**
	 * Constructor
	 * @param String errorMsg 錯誤信息(For debug)
	 * @param String errorCode 錯誤代碼(設定檔中定義其message)
	 * @param Object[] parameters 參數列表(錯誤代碼對應之message需要的參數列表)
	 */
	public BusinessException(String errorMsg, String errorCode, Object[] parameters) {
		super(errorMsg, errorCode, parameters, null);
	}

	/**
	 * Constructor
	 * @param String errorMsg 錯誤信息(For debug)
	 * @param String errorCode 錯誤代碼(設定檔中定義其message)
	 * @param Object[] parameters 參數列表(錯誤代碼對應之message需要的參數列表)
	 * @param Exception e 錯誤發生原因(原始Exception)
	 */
	public BusinessException(String errorMsg, String errorCode, Object[] parameters, Exception e) {
		super(errorMsg, errorCode, parameters, e);
	}
}
