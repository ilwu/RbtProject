package com.rbt.util.exceloperate.bean.expt.config;

import java.util.List;

/**
 * "tr" tag Config Info
 * @author Allen
 */
public class TrInfo extends AbstractStyleInfo{

	// =====================================================
	// 元素子項目
	// =====================================================
	/**
	 * Sheet 設定 List
	 */
	private List<TdInfo> tdInfoList;

	// =====================================================
	// gatter & setter
	// =====================================================
	/**
	 * @return the tdInfoList
	 */
	public List<TdInfo> getTdInfoList() {
		return this.tdInfoList;
	}
	/**
	 * @param tdInfoList the tdInfoList to set
	 */
	public void setTdInfoList(List<TdInfo> tdInfoList) {
		this.tdInfoList = tdInfoList;
	}
}
