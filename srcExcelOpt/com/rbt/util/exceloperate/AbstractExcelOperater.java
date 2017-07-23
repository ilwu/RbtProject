package com.rbt.util.exceloperate;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.rbt.util.StringUtil;
import com.rbt.util.exceloperate.bean.common.AbstractConfigInfo;
import com.rbt.util.exceloperate.bean.common.FunctionInfo;
import com.rbt.util.exceloperate.exception.ExcelOperateException;
import com.rbt.util.exceloperate.function.AbstractExcelOperateFunction;

/**
 * @author Allen
 */
public abstract class AbstractExcelOperater implements InterfaceExcelOperater {

	// ===========================================================================
	// 全域變數
	// ===========================================================================
	/**
	 * LOG4J
	 */
	protected Logger LOG = Logger.getLogger(this.getClass());
	/**
	 * config
	 */
	protected AbstractConfigInfo configInfo;
	/**
	 *
	 */
	protected Connection conn;


	/**
	 * 執行設定的外掛方法
	 * @param columnKey
	 * @param funcId
	 * @param funcParam
	 * @param value
	 * @param rowDataMap
	 * @param conn
	 * @return
	 */
	protected String functionProcess(
			String columnKey,
			String funcId,
			String funcParam,
			String value,
			Map<String, Object> rowDataMap,
			Connection conn
			) {

		// 未設定 FuncId 時跳過
		if (StringUtil.isEmpty(funcId)) {
			return value;
		}

		// 讀取設定
		FunctionInfo functionInfo = this.configInfo.getFunctionInfoMap().get(funcId);

		// 檢核
		if (functionInfo == null) {
			throw new ExcelOperateException("Excel 處理錯誤,function 設定不存在! funcId:[" + funcId + "]");
		}

		// 取得 Class
		Class cls = null;
		AbstractExcelOperateFunction function = functionInfo.getFunctionObject();
		try {
			if (function == null) {

				// 取得Class
				cls = Class.forName(functionInfo.getClassName());
				// 物件實體化
				Object funcObj = cls.newInstance();
				// 檢核
				if (!AbstractExcelOperateFunction.class.isAssignableFrom(funcObj.getClass())) {
					throw new ExcelOperateException("Excel 處理錯誤, function class 需繼承 AbstractExcelOperateFunction! className:[" + functionInfo.getClassName() + "]");
				}
				// 轉型
				function = (AbstractExcelOperateFunction) funcObj;
				// 快取
				functionInfo.setFunctionObject(function);
			}
			// 執行
			return function.process(functionInfo.getMethod(), columnKey, funcParam, value, rowDataMap, conn);

		} catch (ExcelOperateException e) {
			this.LOG.error(StringUtil.getExceptionStackTrace(e.getCause()));
			throw e;
		} catch (ClassNotFoundException e) {
			this.LOG.error(StringUtil.getExceptionStackTrace(e));
			throw new ExcelOperateException("Excel 處理錯誤, function class 不存在! className:[" + functionInfo.getClassName() + "]");
		} catch (InstantiationException e) {
			this.LOG.error(StringUtil.getExceptionStackTrace(e));
			throw new ExcelOperateException("Excel 處理錯誤, function class 實體化時發生錯誤! className:[" + functionInfo.getClassName() + "]");
		} catch (IllegalAccessException e) {
			this.LOG.error(StringUtil.getExceptionStackTrace(e));
			throw new ExcelOperateException("Excel 處理錯誤, function class 實體化時發生錯誤! className:[" + functionInfo.getClassName() + "]");
		} catch (Exception e) {
			this.LOG.error(StringUtil.getExceptionStackTrace(e));
			throw new ExcelOperateException("Excel 處理錯誤, " +
					"\r\ncolumnKey:[" + columnKey + "], " +
					"\r\nfuncId:[" + funcId + "], " +
					"\r\nfuncParam[" + funcParam + "]," +
					"\r\nvalue:[" + value + "]");
		}
	}

}
