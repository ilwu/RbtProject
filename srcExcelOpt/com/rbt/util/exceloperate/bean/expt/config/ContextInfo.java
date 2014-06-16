package com.rbt.util.exceloperate.bean.expt.config;

import java.util.List;

/**
 * Context Config Info
 * @author Allen
 */
public class ContextInfo {

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
	 * TR LIST
	 */
	private List<TrInfo> trInfoList;

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
	 * @return the trInfoList
	 */
	public List<TrInfo> getTrInfoList() {
		return this.trInfoList;
	}

	/**
	 * @param trInfoList the trInfoList to set
	 */
	public void setTrInfoList(List<TrInfo> trInfoList) {
		this.trInfoList = trInfoList;
	};
}
