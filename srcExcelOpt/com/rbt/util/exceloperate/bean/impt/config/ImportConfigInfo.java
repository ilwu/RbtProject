package com.rbt.util.exceloperate.bean.impt.config;

import java.util.LinkedHashMap;
import java.util.List;

import com.rbt.util.exceloperate.bean.common.AbstractConfigInfo;

/**
 * @author Allen
 */
public class ImportConfigInfo extends AbstractConfigInfo{

	// =====================================================
	// 元素屬性
	// =====================================================
	/**
	 * 讀取的 sheet index
	 */
	private int sheetNum;
	/**
	 * 讀取起始行數
	 */
	private int startRow;
	/**
	 * 檢核空行
	 */
	private String checkEmptyRow;
	/**
	 * 重複資料檢核欄位 (key, 以逗點分隔)
	 */
	private String checkDuplicate;
	/**
	 * 欄位說明，非必要
	 */
	private String desc;

	// =====================================================
	// 元素子項目
	// =====================================================
	/**
	 * functionInfo 設定
	 */
	private LinkedHashMap<String, FormatInfo> formatInfoMap;
	/**
	 * column 設定 List
	 */
	private List<ColumnInfo> columnInfoList;
	/**
	 * column 設定 List
	 */
	private List<ParamInfo> paramInfoList;

	// =====================================================
	// gatter & setter
	// =====================================================
	/**
	 * @return the sheetNum
	 */
	public int getSheetNum() {
		return this.sheetNum;
	}

	/**
	 * @param sheetNum the sheetNum to set
	 */
	public void setSheetNum(int sheetNum) {
		this.sheetNum = sheetNum;
	}

	/**
	 * @return the startRow
	 */
	public int getStartRow() {
		return this.startRow;
	}

	/**
	 * @param startRow the startRow to set
	 */
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return this.desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the formatInfoMap
	 */
	public LinkedHashMap<String, FormatInfo> getFormatInfoMap() {
		return this.formatInfoMap;
	}

	/**
	 * @param formatInfoMap the formatInfoMap to set
	 */
	public void setFormatInfoMap(LinkedHashMap<String, FormatInfo> formatInfoMap) {
		this.formatInfoMap = formatInfoMap;
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
	 * @return the paramInfoList
	 */
	public List<ParamInfo> getParamInfoList() {
		return this.paramInfoList;
	}

	/**
	 * @param paramInfoList the paramInfoList to set
	 */
	public void setParamInfoList(List<ParamInfo> paramInfoList) {
		this.paramInfoList = paramInfoList;
	}

	/**
	 * @return the checkDuplicate
	 */
	public String getCheckDuplicate() {
		return this.checkDuplicate;
	}

	/**
	 * @param checkDuplicate the checkDuplicate to set
	 */
	public void setCheckDuplicate(String checkDuplicate) {
		this.checkDuplicate = checkDuplicate;
	}

	/**
	 * @return the checkEmptyRow
	 */
	public String getCheckEmptyRow() {
		return this.checkEmptyRow;
	}

	/**
	 * @param checkEmptyRow the checkEmptyRow to set
	 */
	public void setCheckEmptyRow(String checkEmptyRow) {
		this.checkEmptyRow = checkEmptyRow;
	}

}
