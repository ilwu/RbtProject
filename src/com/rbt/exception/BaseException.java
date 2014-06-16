
package com.rbt.exception;

/**
 * 異常 Base
 * @author Allen
 */
public class BaseException extends RuntimeException {

	private static final long serialVersionUID = -5154615570448990996L;

	protected String errorCode;     // 錯誤代碼(設定檔中定義其message)
	protected String errorMsg;      // 錯誤信息(For debug)
	protected Object[] parameters;  // 參數列表(錯誤代碼對應之message需要的參數列表)
	protected Exception errorCause; // 錯誤發生原因(原始Exception)

	/**
	 * Constructor
	 * @param String errorMsg 錯誤信息(For debug)
	 * @param String errorCode 錯誤代碼(設定檔中定義其message)
	 * @param Object[] parameters 參數列表(錯誤代碼對應之message需要的參數列表)
	 * @param Exception e 錯誤發生原因(原始Exception)
	 */
	public BaseException(String errorMsg, String errorCode, Object[] parameters, Exception e) {
		super(e==null? errorMsg : e.getMessage());
		this.errorCode  = errorCode;
		this.errorMsg   = errorMsg;
		this.errorCause = e;
		this.parameters = parameters;
	}

	public Exception getErrorCause() {
		return this.errorCause;
	}

	public void setErrorCause(Exception errorCause) {
		this.errorCause = errorCause;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return this.errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Object[] getParameters() {
		return this.parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}
}
