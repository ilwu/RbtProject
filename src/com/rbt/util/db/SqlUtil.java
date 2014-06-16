package com.rbt.util.db;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;

import com.rbt.util.StringUtil;

/**
 * SQL 處理工具
 * @author Allen
 */
public class SqlUtil {

	/**
	 * LOG4j
	 */
	protected static Logger LOG = Logger.getLogger(SqlUtil.class);

	/**
	 * 產生Query SQL
	 * @param tablenName
	 * @param selectColumns
	 * @param whereParam
	 * @return
	 */
	/**
	 * @param tablenName table name
	 * @param selectColumns select 欄位字串
	 * @param whereParam where 參數
	 * @return
	 */
	public static String genQuerySQL(String tablenName, List<String> selectColumns, LinkedHashMap<String, Object> whereParam) {

		StringBuffer sqlSB = new StringBuffer();
		// =================================================
		// 兜組 Query SQL
		// =================================================
		sqlSB.append(SqlUtil.genSelectSQL(selectColumns));
		sqlSB.append("FROM " + tablenName + " ");
		sqlSB.append(SqlUtil.genWhereSQL(whereParam));

		return sqlSB.toString();
	}

	/**
	 * @param selectColumns
	 * @return
	 */
	public static String genSelectSQL(List<String> selectColumns) {
		if (selectColumns == null || selectColumns.size() == 0) {
			return "SELECT * ";
		}
		StringBuffer sqlSB = new StringBuffer();
		for (String column : selectColumns) {
			sqlSB.append("," + column + " ");
		}
		return "SELECT " + sqlSB.substring(1) + " ";
	}

	// ==============================================================================================
	// SQL : INSERT
	// ==============================================================================================
	/**
	 * 產生Insert SQL (參數值以 ? 形式回傳)
	 * @param tablenName
	 * @param param
	 * @return
	 */
	public static String genInsertSQL(String tablenName, LinkedHashMap<String, Object> param) {
		return genInsertSQL(tablenName, param, false);
	}

	/**
	 * 產生Insert SQL
	 * @param tablenName
	 * @param param
	 * @param isSetParam 是否將參數值兜組在SQL中
	 * @return
	 */
	public static String genInsertSQL(String tablenName, LinkedHashMap<String, Object> param, boolean isSetParam) {

		StringBuffer sqlSB = new StringBuffer();
		// =================================================
		// 兜組 INSERT SQL
		// =================================================
		sqlSB.append("INSERT INTO " + tablenName + " (");

		// 放入所有欄位名稱
		for (String columnName : param.keySet()) {
			//key 前綴帶 ## 時, 代表 value 為 function, 需將 columnName 換回正確名稱
			if (columnName.startsWith("##")){
				columnName = columnName.substring(2);
			}
			sqlSB.append(columnName + ", ");
		}
		// 最後一個逗號換成右刮號
		sqlSB.replace(sqlSB.length() - 2, sqlSB.length(), ")");

		// VALUES
		sqlSB.append(" VALUES (");

		LinkedHashMap<String, Object> newParam = new LinkedHashMap();
		for (String columnName : param.keySet()) {

			if (param.get(columnName) == null) {
				sqlSB.append("null,");
			} else if (columnName.startsWith("##")){
				//key 前綴帶 ## 時, 代表 value 為 function
				sqlSB.append(param.get(columnName) + ",");
			} else {
				if (isSetParam) {
					sqlSB.append("'" + procEscChar(param.get(columnName)) + "',");
				} else {
					sqlSB.append("?,");
				}
				newParam.put(columnName, param.get(columnName));
			}
		}

		// 清除掉為 null 的參數, 避免直接以傳入map 進行DB操作時產生錯誤
		if (!isSetParam) {
			param.clear();
			for (String columnName : newParam.keySet()) {
				param.put(columnName, newParam.get(columnName));
			}
		}

		// 最後一個逗號換成右刮號
		sqlSB.replace(sqlSB.length() - 1, sqlSB.length(), ")");

		// return
		return sqlSB.toString();
	}

	// ==============================================================================================
	// SQL : UPDATE
	// ==============================================================================================
	/**
	 * 產生UPDATE SQL (參數值以 ? 形式傳回)<br>
	 * <br>
	 * 會重置 setParam & whereParam , 參數值有 null 時, 自動拿掉該參數，直接將null補在兜組的SQL中<br>
	 * ex. set aa=? ==>set aa=null, where aa=? ==> where aa is null
	 * @param tablenName table Name
	 * @param setParam 異動欄位 LinkedHashMap
	 * @param whereParam 異動條件 LinkedHashMap
	 * @return
	 */
	public static String genUpdateSQL(String tablenName, LinkedHashMap<String, Object> setParam, LinkedHashMap<String, Object> whereParam) {
		return genUpdateSQL(tablenName, setParam, whereParam, false);
	}

	/**
	 * 產生UPDATE SQL<br>
	 * <br>
	 * 會重置 setParam & whereParam , 參數值有 null 時, 自動拿掉該參數，直接將null補在兜組的SQL中<br>
	 * ex. set aa=? ==>set aa=null, where aa=? ==> where aa is null
	 * @param tablenName table Name
	 * @param setParam 異動欄位 LinkedHashMap
	 * @param whereParam 異動條件 LinkedHashMap
	 * @param isSetParam 是否將參數值兜組在SQL中
	 * @return
	 */
	public static String genUpdateSQL(String tablenName, LinkedHashMap<String, Object> setParam, LinkedHashMap<String, Object> whereParam, boolean isSetParam) {

		StringBuffer sqlSB = new StringBuffer();
		// =================================================
		// 兜組 UPDATE SQL
		// =================================================
		sqlSB.append("UPDATE " + tablenName + " ");
		sqlSB.append("SET ");

		// 放入所有SET欄位名稱
		HashMap<String, Object> newSetParam = new LinkedHashMap();
		for (String columnName : setParam.keySet()) {
			if (setParam.get(columnName) == null) {
				sqlSB.append(columnName + " = null, ");

			} else if (columnName.startsWith("##")){
				//key 前綴帶 ## 時, 代表 value 為 function
				columnName = columnName.substring(2);
				sqlSB.append(columnName + " = " + setParam.get(columnName) + ", ");

			} else {
				if (isSetParam) {
					sqlSB.append(columnName + " = '" + procEscChar(setParam.get(columnName)) + "', ");
				} else {
					sqlSB.append(columnName + " = ?, ");
				}
				newSetParam.put(columnName, setParam.get(columnName));
			}
		}
		// 最後一個逗號換成空白
		sqlSB.replace(sqlSB.length() - 2, sqlSB.length(), " ");

		// 清除掉為 null 的參數, 避免直接以傳入map 進行DB操作時產生錯誤
		if (!isSetParam) {
			setParam.clear();
			for (String columnName : newSetParam.keySet()) {
				setParam.put(columnName, newSetParam.get(columnName));
			}
		}

		// 放入 Where 條件
		sqlSB.append(genWhereSQL(whereParam));

		// return
		return sqlSB.toString();
	}

	// ==============================================================================================
	// SQL : DELETE
	// ==============================================================================================
	/**
	 * 產生Delete SQL
	 * @param tablenName
	 * @param whereParam
	 * @return
	 */
	public static String genDeleteSQL(String tablenName, LinkedHashMap<String, Object> whereParam) {

		StringBuffer sqlSB = new StringBuffer();

		// =================================================
		// 兜組 Delete SQL
		// =================================================
		// delete
		sqlSB.append("DELETE FROM " + tablenName + " ");
		// where
		sqlSB.append(genWhereSQL(whereParam));

		// return
		return sqlSB.toString();
	}

	// ==============================================================================================
	// SQL : WHERE
	// ==============================================================================================
	/**
	 * @param whereParam
	 * @return
	 */
	public static String genWhereSQL(LinkedHashMap<String, Object> whereParam) {

		StringBuffer sqlSB = new StringBuffer();

		// 無where 條件時直接回傳
		if (whereParam == null || whereParam.size() == 0) {
			return "";
		}

		sqlSB.append("WHERE ");
		LinkedHashMap<String, Object> newWhereParam = new LinkedHashMap();
		for (String columnName : whereParam.keySet()) {
			Object value = whereParam.get(columnName);
			if (value == null) {
				// 傳入值為 null
				sqlSB.append(columnName + " is null and ");

			} else if (List.class.isAssignableFrom(value.getClass())) {
				// 傳入值為 List
				List list = (List) value;
				if (list.size() == 0) {
					continue;
				}
				sqlSB.append(columnName + " in (" + SqlUtil.genInValue(list) + ") and ");

			} else {
				//
				sqlSB.append(columnName + " = ? and ");
				newWhereParam.put(columnName, value);
			}
		}

		// 最後一個 " and " 換成空白
		sqlSB.replace(sqlSB.length() - 5, sqlSB.length(), " ");

		whereParam.clear();
		for (String columnName : newWhereParam.keySet()) {
			whereParam.put(columnName, newWhereParam.get(columnName));
		}

		return sqlSB.toString();
	}

	// ==============================================================================================
	// SQL : IN
	// ==============================================================================================
	/**
	 * 分隔符號字串 => SQL IN 字串
	 * @param str
	 * @param spliteStr
	 * @return
	 */
	public static String genInValue(String str, String spliteStr) {
		// 兜組 msgid IN 參數
		String[] ary = str.split(spliteStr);
		String result = "";
		for (int i = 0; i < ary.length; i++) {
			result += "'" + ary[i] + "',";
		}
		return result.substring(0, result.length() - 1);
	}

	/**
	 * @param params
	 * @return
	 */
	public static String genInValue(List params) {
		if (params == null || params.size() == 0) {
			return "";
		}
		String result = "";
		for (Object param : params) {
			result += ",'" + param + "'";
		}

		return result.substring(1);
	}

	/**
	 * 處理 IN SQL
	 * @param paramStr
	 * @param params
	 * @return SQL String
	 */
	public static String genInSQL(String paramStr, List<Object> params) {

		if (StringUtil.isEmpty(paramStr)) {
			return "";
		}

		String qmMarkStr = "";
		String paramAry[] = paramStr.split(",");

		for (String param : paramAry) {
			qmMarkStr += ",?";
			params.add(param);
		}
		return qmMarkStr.substring(1);
	}

	/**
	 * 處理 IN SQL （依據傳入的參數個數,產生對應的?號字串 ex:size=3 -> ?,?,?）
	 * @param params
	 * @return SQL String
	 */
	public static String genInSQL(List<Object> params) {

		if (params == null || params.size() == 0) {
			return "";
		}

		String qmMarkStr = "";
		for (int i = 0; i < params.size(); i++) {
			qmMarkStr += ",?";
		}

		return qmMarkStr.substring(1);
	}

	/**
	 * 處理 IN SQL （依據傳入的參數個數,產生對應的?號字串 ex:size=3 -> ?,?,?）
	 * @param params
	 * @return SQL String
	 */
	public static String genInSQLs(List<String> params) {

		if (params == null || params.size() == 0) {
			return "";
		}

		String qmMarkStr = "";
		for (int i = 0; i < params.size(); i++) {
			qmMarkStr += ",?";
		}

		return qmMarkStr.substring(1);
	}

	// ==============================================================================================
	// OTHER
	// ==============================================================================================
	private static String procEscChar(Object obj) {
		String value = StringUtil.safeTrim(obj);
		value = value.replaceAll("'", "''");
		value = value.replaceAll("\"", "\\\"");
		return value;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//
	}
}
