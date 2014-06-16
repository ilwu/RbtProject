package com.rbt.framework.model;


/**
 * BaseCommand類為實現Command接口之基類
 * @author jmiuhan
 *
 */
public class BaseCommand implements Command{
	private static final long serialVersionUID = -5624786483050930531L;
	private String funcId;

	/**
	 * 頁面上所有CheckBox 的 name
	 */
	private String checkBoxRegister;


	/**
	 * 功能：取得當前作業的功能ID(funcId)
	 */
	public String getFuncId() {
		return this.funcId;
	}

	/**
	 * 功能：set功能ID(funcId)
	 * @param funcId 功能ID
	 */
	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

	/**
	 * @return the checkBoxRegister
	 */
	public String getCheckBoxRegister() {
		return this.checkBoxRegister;
	}

	/**
	 * @param checkBoxRegister the checkBoxRegister to set
	 */
	public void setCheckBoxRegister(String checkBoxRegister) {
		this.checkBoxRegister = checkBoxRegister;
	}
}
