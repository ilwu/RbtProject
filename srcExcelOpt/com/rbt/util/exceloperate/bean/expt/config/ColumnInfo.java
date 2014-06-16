package com.rbt.util.exceloperate.bean.expt.config;

import java.util.List;

/**
 * column Config Info
 * @author Allen
 */
public class ColumnInfo extends AbstractExportColumnArrtInfo{

	// =====================================================
	// 元素子項目
	// =====================================================
	/**
	 * ColumnDetailList LIST
	 */
	private List<ColumnDetailInfo> columnDetailInfoList;

	// =====================================================
	// gatter & setter
	// =====================================================
	/**
	 * @return the columnDetailInfoList
	 */
	public List<ColumnDetailInfo> getColumnDetailInfoList() {
		return this.columnDetailInfoList;
	}

	/**
	 * @param columnDetailInfoList the columnDetailInfoList to set
	 */
	public void setColumnDetailInfoList(List<ColumnDetailInfo> columnDetailInfoList) {
		this.columnDetailInfoList = columnDetailInfoList;
	}
}
