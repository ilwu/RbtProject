package com.rbt.util.exceloperate.bean.expt.config;

import java.util.List;

/**
 * ColumnDetail Config Info
 * @author Allen
 */
public class ColumnDetailInfo {

	// =====================================================
	// 元素屬性
	// =====================================================
	private String type;

	private String dataId;

	// =====================================================
	// 元素子項目
	// =====================================================
	private List<ColumnInfo> columnInfoList;

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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

}
