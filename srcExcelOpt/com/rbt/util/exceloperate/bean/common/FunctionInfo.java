package com.rbt.util.exceloperate.bean.common;

import com.rbt.util.exceloperate.function.AbstractExcelOperateFunction;

/**
 * Function Config Info
 * @author Allen
 */
public class FunctionInfo {

	// =====================================================
	// 元素屬性
	// =====================================================
	/**
	 * Function ID
	 */
	private String funcId;
	/**
	 * class name
	 */
	private String className;
	/**
	 * method
	 */
	private String method;

	// =====================================================
	// 工作參數
	// =====================================================
	/**
	 * 若已實體化過，可將實體化物件存進來快取
	 */
	private AbstractExcelOperateFunction functionObject;

	// =====================================================
	// gatter & setter
	// =====================================================
	/**
	 * @return the funcId
	 */
	public String getFuncId() {
		return this.funcId;
	}

	/**
	 * @param funcId the funcId to set
	 */
	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return this.method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the functionObject
	 */
	public AbstractExcelOperateFunction getFunctionObject() {
		return this.functionObject;
	}

	/**
	 * @param functionObject the functionObject to set
	 */
	public void setFunctionObject(AbstractExcelOperateFunction functionObject) {
		this.functionObject = functionObject;
	}
}
