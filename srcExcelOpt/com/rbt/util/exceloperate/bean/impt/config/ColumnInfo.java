package com.rbt.util.exceloperate.bean.impt.config;

/**
 * column Config Info
 * @author Allen
 */
public class ColumnInfo extends AbstractImportCommonAttrInfo {

	// =====================================================
	// 元素屬性
	// =====================================================
	/**
	 * 要驗證的定義ID
	 */
	private String formatId;
	/**
	 * 單欄定義自帶驗證
	 */
	private String regex;
	/**
	 * 是否可為空值
	 */
	private String checkNull;
	/**
	 * 忽略此欄位
	 */
	private boolean pass;

	// =====================================================
	// 元素子項目
	// =====================================================

	// =====================================================
	// gatter & setter
	// =====================================================

	/**
	 * @return the formatId
	 */
	public String getFormatId() {
		return this.formatId;
	}

	/**
	 * @param formatId the formatId to set
	 */
	public void setFormatId(String formatId) {
		this.formatId = formatId;
	}

	/**
	 * @return the regex
	 */
	public String getRegex() {
		return this.regex;
	}

	/**
	 * @param regex the regex to set
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}

	/**
	 * @return the checkNull
	 */
	public String getCheckNull() {
		return this.checkNull;
	}

	/**
	 * @param checkNull the checkNull to set
	 */
	public void setCheckNull(String checkNull) {
		this.checkNull = checkNull;
	}

	/**
	 * @return the pass
	 */
	public boolean isPass() {
		return this.pass;
	}

	/**
	 * @param pass the pass to set
	 */
	public void setPass(boolean pass) {
		this.pass = pass;
	}
}
