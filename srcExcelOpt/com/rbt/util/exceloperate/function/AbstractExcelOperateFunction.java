/**
 *
 */
package com.rbt.util.exceloperate.function;

import java.sql.Connection;
import java.util.Map;

import com.rbt.util.exceloperate.exception.ExcelOperateException;

/**
 * @author Allen
 */
public abstract class AbstractExcelOperateFunction {

	/**
	 * 處理方法
	 * @param method 設定的方法參數
	 * @param funcParam function 使用的參數
	 * @param value 要處理的值
	 * @return 返回處理後的值
	 * @throws ExcelOperateException
	 */
	abstract public String process(
			String method,
			String keyName,
			String funcParam,
			String value,
			Map<String, Object>	rowDataMap,
			Connection conn) throws ExcelOperateException;

}
