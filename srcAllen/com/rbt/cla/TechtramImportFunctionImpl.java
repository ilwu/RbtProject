package com.rbt.cla;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.rbt.util.StringUtil;
import com.rbt.util.db.RbtDbUtilImpl;
import com.rbt.util.exceloperate.exception.ExcelOperateException;
import com.rbt.util.exceloperate.function.AbstractExcelOperateFunction;

/**
 * @author Allen
 */
public class TechtramImportFunctionImpl extends AbstractExcelOperateFunction {

	/**
	 * LOG4j
	 */
	private static Logger LOG = Logger.getLogger(TechtramImportFunctionImpl.class);

	/* (non-Javadoc)
	 * @see com.rbt.util.exceloperate.function.AbstractExcelOperateFunction#process(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Map, java.sql.Connection)
	 */
	public String process(
			String method,
			String keyName,
			String funcParam,
			String value,
			Map<String, Object> rowDataMap,
			Connection conn) throws ExcelOperateException {

		// 依據 method 參數, 進行處理
		if ("SPLIT_SLASH".equalsIgnoreCase(method)) {
			// SQL
			return this.splitSlash(funcParam, value);

		} else if ("LEFT_PADDING".equalsIgnoreCase(method)) {
			return this.leftPadding(funcParam, value);

		} else if ("PARSER_PNO".equalsIgnoreCase(method)) {
			return this.parserPno(StringUtil.safeTrim(rowDataMap.get(funcParam)));

		} else if ("PARSER_EGR".equalsIgnoreCase(method)) {
			return this.parserEgr(StringUtil.safeTrim(rowDataMap.get(funcParam)));

		}

		throw new ExcelOperateException("Excel 處理錯誤, [" + this.getClass().getName() + "] 未設定 method :[" + method + "]");
	}

	public String splitSlash(String funcParam, String value) {
		if (StringUtil.isEmpty(value) || !StringUtil.isNumber(funcParam)) {
			return value;
		}
		String[] values = value.split("\\");
		int index = Integer.parseInt(funcParam);
		if (values.length <= index) {
			return value;
		}
		return values[index];
	}

	public String leftPadding(String funcParam, String value) {
		if (!StringUtil.isNumber(funcParam)) {
			return value;
		}
		int length = Integer.parseInt(funcParam);

		return StringUtil.padding(value, "0", length, true);
	}

	public String parserPno(String value){
		value = StringUtil.safeTrim(value);
		if(value.length()<5){
			return value;
		}
		return value.substring(0,5);
	}

	public String parserEgr(String value){
		value = StringUtil.safeTrim(value);
		if(value.indexOf("甲") >-1){
			return "1";
		}
		if(value.indexOf("乙") >-1){
			return "2";
		}
		if(value.indexOf("丙") >-1){
			return "3";
		}
		if(value.indexOf("單一") >-1){
			return "4";
		}
		return "";
	}
}
