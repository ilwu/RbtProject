package com.rbt.util.exceloperate.bean.impt.config;

/**
 * format Config Info
 * @author Allen
 */
public class FormatInfo {

	// =====================================================
	// 元素屬性
	// =====================================================
	/**
	 * format 設定 ID
	 */
	private String formatId;
	/**
	 * 檢核之正規表示式
	 */
	private String regex;

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
}
