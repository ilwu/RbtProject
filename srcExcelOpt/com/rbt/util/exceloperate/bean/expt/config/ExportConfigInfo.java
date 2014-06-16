package com.rbt.util.exceloperate.bean.expt.config;

import java.util.List;

import com.rbt.util.exceloperate.bean.common.AbstractConfigInfo;
import jxl.format.PaperSize;

/**
 * @author Allen
 */
public class ExportConfigInfo extends AbstractConfigInfo{

	// =====================================================
	// 元素屬性
	// =====================================================
	/**
	 * 輸出檔案名稱
	 */
	private String fileName;
	/**
	 * 頁面大小
	 */
	private PaperSize paperSize;

	// =====================================================
	// 元素子項目
	// =====================================================
	/**
	 * 範圍 style 設定
	 */
	private StyleInfo styleInfo;
	/**
	 * Sheet 設定 List
	 */
	private List<SheetlInfo> sheetList;

	// =====================================================
	// gatter & setter
	// =====================================================
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the sheetList
	 */
	public List<SheetlInfo> getSheetList() {
		return this.sheetList;
	}

	/**
	 * @param sheetList the sheetList to set
	 */
	public void setSheetList(List<SheetlInfo> sheetList) {
		this.sheetList = sheetList;
	}

	/**
	 * @return the paperSize
	 */
	public PaperSize getPaperSize() {
		return this.paperSize;
	}

	/**
	 * @param paperSize the paperSize to set
	 */
	public void setPaperSize(PaperSize paperSize) {
		this.paperSize = paperSize;
	}

	/**
	 * @return the styleInfo
	 */
	public StyleInfo getStyleInfo() {
		return this.styleInfo;
	}

	/**
	 * @param styleInfo the styleInfo to set
	 */
	public void setStyleInfo(StyleInfo styleInfo) {
		this.styleInfo = styleInfo;
	}

}
