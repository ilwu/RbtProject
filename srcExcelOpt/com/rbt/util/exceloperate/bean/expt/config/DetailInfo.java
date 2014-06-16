package com.rbt.util.exceloperate.bean.expt.config;

import java.util.List;

/**
 * Detail Config Info
 * @author Allen
 */
public class DetailInfo {

	// =====================================================
	// 元素屬性
	// =====================================================
	/**
	 * 設定ID
	 */
	private String dataId;

	// =====================================================
	// 元素子項目
	// =====================================================
	/**
	 * Column LIST
	 */
	private List<ColumnInfo> columnInfoList;

	// =====================================================
	// gatter & setter
	// =====================================================

	/**
	 * @return the dataId
	 */
	public String getDataId() {
		return this.dataId;
	}

	/**
	 * @param dataId the dataId to set
	 */
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	/**
	 * @return the columnInfoList
	 */
	public List<ColumnInfo> getColumnInfoList() {
		return this.columnInfoList;
	}

	/**
	 * @param columnInfoList the columnInfoList to set
	 */
	public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
		this.columnInfoList = columnInfoList;
	}
}
