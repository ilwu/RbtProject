package com.rbt.util.exceloperate.bean.common;

import java.util.LinkedHashMap;

/**
 * @author Allen
 */
public abstract class AbstractConfigInfo {
	// =====================================================
	// 元素屬性
	// =====================================================
	/**
	 * 設定ID
	 */
	private String id;

	// =====================================================
	// 元素子項目
	// =====================================================
	/**
	 * functionInfo 設定
	 */
	private LinkedHashMap<String, FunctionInfo> functionInfoMap;

	// =====================================================
	// gatter & setter
	// =====================================================
	/**
	 * @return the functionInfoMap
	 */
	public LinkedHashMap<String, FunctionInfo> getFunctionInfoMap() {
		return this.functionInfoMap;
	}

	/**
	 * @param functionInfoMap the functionInfoMap to set
	 */
	public void setFunctionInfoMap(LinkedHashMap<String, FunctionInfo> functionInfoMap) {
		this.functionInfoMap = functionInfoMap;
	}

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

}
