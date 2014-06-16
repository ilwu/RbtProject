package com.rbt.util.exceloperate.bean.expt.config;

import java.util.LinkedHashMap;

public class SheetlInfo {

	// =====================================================
	// 元素屬性
	// =====================================================
	/**
	 * 設定ID
	 */
	private String id;
	/**
	 * 顯示 Sheet 名稱
	 */
	private String sheetName;

	// =====================================================
	// 元素子項目
	// =====================================================
	/**
	 * (context | detail)*
	 */
	private LinkedHashMap<String, Object> partInfoMap;

	// =====================================================
	// gatter & setter
	// =====================================================

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the sheetName
	 */
	public String getSheetName() {
		return this.sheetName;
	}
	/**
	 * @param sheetName the sheetName to set
	 */
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	/**
	 * @return the partInfoMap
	 */
	public LinkedHashMap<String, Object> getPartInfoMap() {
		return this.partInfoMap;
	}
	/**
	 * @param partInfoMap the partInfoMap to set
	 */
	public void setPartInfoMap(LinkedHashMap<String, Object> partInfoMap) {
		this.partInfoMap = partInfoMap;
	}

}
