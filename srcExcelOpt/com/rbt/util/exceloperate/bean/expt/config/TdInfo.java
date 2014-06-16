package com.rbt.util.exceloperate.bean.expt.config;

/**
 * td Config Info
 * @author Allen
 */
public class TdInfo extends AbstractExportColumnArrtInfo{

	// =====================================================
	// 元素屬性
	// =====================================================
	/**
	 * 合併行數
	 */
	private int rowspan;

	// =====================================================
	// gatter & setter
	// =====================================================
	/**
	 * @return the rowspan
	 */
	public int getRowspan() {
		return this.rowspan;
	}

	/**
	 * @param rowspan the rowspan to set
	 */
	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}
}
