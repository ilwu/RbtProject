package com.rbt.evaimport;

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
public class EvaImportFunctionImpl extends AbstractExcelOperateFunction {

	/**
	 * LOG4j
	 */
	private static Logger LOG = Logger.getLogger(EvaImportFunctionImpl.class);

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
		if ("SELECT".equalsIgnoreCase(method)) {
			// SQL
			return this.select(keyName, funcParam, rowDataMap, conn);

		} else if ("GEN_UID".equalsIgnoreCase(method)) {
			// 產生序號
			return this.genUID(funcParam);

		} else if ("LENGTH_LIMIT".equalsIgnoreCase(method)) {
			// 長度限制
			return this.lengthLimit(value, funcParam);

		} else if ("CURR_DATE_TIME".equalsIgnoreCase(method)) {
			// 取得系統時間
			return this.getCurrDateTime(funcParam);

		} else if ("COPY_COLUMN".equalsIgnoreCase(method)) {
			// 複製別的欄位
			return StringUtil.safeTrim(rowDataMap.get(funcParam));

		} else if ("DATE_FORMAT".equalsIgnoreCase(method)) {
			// 格式化日期
			return this.dateFormat(value);

		}else if ("EMPTY_TO_FULL_SPACE".equalsIgnoreCase(method)) {
			// 值為空時，放入全形空白
			return this.empty2FullSpace(value);

		}else if ("LEFT_PAD_ZERO".equalsIgnoreCase(method)) {
			// 值為空時，放入全形空白
			if(!StringUtil.isNumber(funcParam)){
				throw new ExcelOperateException("LEFT_PAD_ZERO 處理錯誤, 傳入參數非數字[" + funcParam + "]");
			}
			return StringUtil.padding(value, "0", Integer.parseInt(funcParam), true);

		}else if ("REPLACE_TO_PARAM".equalsIgnoreCase(method)) {
			// 將傳入字串的關鍵字,轉為
			return StringUtil.replace2Param(funcParam, rowDataMap);

		}else if ("CONVERT_AD_YEAR".equalsIgnoreCase(method)) {
			// 傳入值轉換為西元年
			return this.convertAdYear(value);
		}
		throw new ExcelOperateException("Excel 處理錯誤, [" + this.getClass().getName() + "] 未設定 method :[" + method + "]");
	}

	// ====================================================================================================
	// DATE_FORMAT
	// ====================================================================================================
	/**
	 * @param value
	 * @return
	 */
	public String dateFormat(String value) {

		value = StringUtil.safeTrim(value);
		value = value.replace(".", "-");
		value = value.replace("/", "-");
		value = value.replace("\\", "-");

		String[] dateAry = value.split("-");

		if (dateAry.length != 3) {
			throw new ExcelOperateException("傳入日期格式錯誤:[" + value + "]");
		}

		int year = Integer.parseInt(StringUtil.safeTrim(dateAry[0], "0"));
		int month = Integer.parseInt(StringUtil.safeTrim(dateAry[1], "0"));
		int date = Integer.parseInt(StringUtil.safeTrim(dateAry[2], "0"));

		if (month > 12 || month < 1 || date > 31 || date < 1) {
			throw new ExcelOperateException("傳入日期格式錯誤:[" + value + "]");
		}

		if (year < 200) {
			year += 1911;
		}

		return StringUtil.padding(year, "0", 4, true) + "-" +
				StringUtil.padding(month, "0", 2, true) + "-" +
				StringUtil.padding(date, "0", 2, true);
	}

	// ====================================================================================================
	// SELECT
	// ====================================================================================================
	/**
	 * @param querySQL
	 * @param conn
	 * @return
	 */
	public String select(String keyName, String funcParam, Map<String, Object> rowDataMap, Connection conn) {

		//兜組查詢字串
		String querySQL = StringUtil.replace2Param(funcParam, rowDataMap);

		try {
			// 查詢
			List<LinkedHashMap<String, Object>> list = new RbtDbUtilImpl().query(conn, querySQL, null);

			if (StringUtil.isEmpty(list)) {
				LOG.info("[" + keyName + "] - 查詢無資料:[" + querySQL + "]");
				return "";
			}

			LinkedHashMap<String, Object> dataMap = list.get(0);
			for (Object value : dataMap.values()) {
				return StringUtil.safeTrim(value);
			}
			return "";
		} catch (Exception e) {
			LOG.error(StringUtil.getExceptionStackTrace(e));
			throw new ExcelOperateException("Function select 處理錯誤!", e);
		}

	}

	// ====================================================================================================
	// genUID
	// ====================================================================================================
	/**
	 * @param parentstr
	 * @return
	 */
	public String genUID(String parentstr) {

		String date = "";
		String num = "";
		String temp = "";

		// 2008-09-26
		/**
		 * =============== 解析日期時間格式 ===============
		 */
		int startDATE = parentstr.indexOf("[DATE");
		int endDATE = parentstr.indexOf("]", startDATE);

		if (startDATE > -1 && endDATE > -1) {
			temp = parentstr.substring(startDATE + 1, endDATE);
			date = temp.replaceAll("DATE", "");
			String[] dates = date.split("-");
			if (dates.length != 2) {
				// 日期時間格式不對
				throw new ExcelOperateException("Function [GEN_UID] 日期時間按格式不對!");
			}
			// 格式化時間
			date = this.formatDateTime(dates[0], dates[1]);
			// 替換日期時間部分
			parentstr = parentstr.replaceAll("\\[" + temp + "\\]", date);
		}

		/**
		 * =============== 解析隨機字符 ===============
		 */
		int randomNum = (new Random()).nextInt(9);
		parentstr = parentstr.replaceAll("\\[R]", String.valueOf(randomNum));

		/**
		 * =============== 解析數字格式 ===============
		 */
		int startNUM = parentstr.indexOf("[NUM");
		int endNUM = parentstr.indexOf("]", startNUM);
		if (startNUM > -1 && endNUM > -1) {
			temp = parentstr.substring(startNUM + 1, endNUM);
			num = temp.replaceAll("NUM", "");
			num = this.getIndex(date, num);
			// 替換數字序號部分
			parentstr = parentstr.replaceAll("\\[" + temp + "\\]", num);
		}
		parentstr = parentstr.trim();
		return parentstr;
	}

	/**
	 * 格式日期時間
	 *
	 * @return
	 */
	public String formatDateTime(String start, String end) {
		if (null == start || null == end) {
			return "";
		}
		int beginIndex = Integer.parseInt(start);
		int endIndex = Integer.parseInt(end);

		if (beginIndex > 18 || endIndex > 18) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
		Date currentDate = new Date();
		String date1 = sdf.format(currentDate);

		return date1.substring(beginIndex, endIndex);
	}

	// 序列號
	private int index = 1;

	/**
	 * 獲取ID序號
	 *
	 * @param date
	 * @param time
	 * @param length
	 * @return
	 */
	public String getIndex(String nowTime, String num) {
		String result = null;
		int length = Integer.parseInt(num);
		result = String.valueOf(this.index++);
		if (result.length() < length) {
			for (int i = 0, j = length - result.length(); i < j; i++)
				result = "0" + result;
		}
		return result;
	}

	// ====================================================================================================
	// LENGTH_LIMIT
	// ====================================================================================================
	/**
	 * 截除長度限制以後的字元
	 * @param value
	 * @param limit
	 * @return
	 */
	public String lengthLimit(String value, String limit) {
		limit = StringUtil.safeTrim(limit);
		if (!StringUtil.isNumber(limit)) {
			throw new ExcelOperateException("Function [LENGTH_LIMIT] 長度設定錯誤! [" + limit + "]");
		}

		int maxLength = Integer.valueOf(limit);
		if (value.length() > maxLength) {
			return value.substring(0, maxLength);
		}

		return value;
	}

	// ====================================================================================================
	// CURR_DATE_TIME
	// ====================================================================================================
	/**
	 * 取得目前系統時間
	 * @param format
	 * @return
	 */
	public String getCurrDateTime(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	// ====================================================================================================
	// CONVERT_AD_YEAR
	// ====================================================================================================
	/**
	 * 轉換為西元年
	 * @param format
	 * @return
	 */
	public String convertAdYear(String value) {
		if(StringUtil.isEmpty(value) || !StringUtil.isNumber(value)){
			return value;
		}

		return (Integer.parseInt(value) + 1911) + "";
	}

	// ====================================================================================================
	//
	// ====================================================================================================
	/**
	 * 值為空白時轉全形
	 * @param value
	 * @return
	 */
	protected String empty2FullSpace(Object value) {
		if (StringUtil.isEmpty(value)) {
			return "　";
		}
		return StringUtil.safeTrim(value);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EvaImportFunctionImpl function = new EvaImportFunctionImpl();
		String result = "";

		// ===============================
		// select 測試
		// ===============================
		// Map<String, Object> rowDataMap = new HashMap();
		// rowDataMap.put("CITY", "台中市");
		// rowDataMap.put("TOWN", "北區");
		// String sql = "SELECT TOWN_UID FROM TB_TOWN A LEFT JOIN TB_CITY B ON A.CITY_UID = B.CITY_UID WHERE CITY_NAME ='#CITY#' AND TOWN_NAME ='#TOWN#'";
		// result = function.select(sql, rowDataMap, null);
		// System.out.println("[" + result + "]");

		// ===============================
		// UID 測試
		// ===============================
		// result = function.genUID("SB[DATE2-14][R][NUM5]");
		// System.out.println("[" + result + "]");
		// result = function.genUID("SB[DATE2-14][R][NUM5]");
		// System.out.println("[" + result + "]");
		// result = function.genUID("SB[DATE2-14][R][NUM5]");
		// System.out.println("[" + result + "]");
		// result = function.genUID("SB[DATE2-14][R][NUM5]");
		// System.out.println("[" + result + "]");

		// ===============================
		// LENGTH_LIMIT 測試
		// ===============================
		//result = function.lengthLimit("12345", "3");

		// ===============================
		// DATE_FORMAT 測試
		// ===============================
		//result = function.dateFormat("2013/2/4");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
		Date currentDate = new Date();
		String date1 = sdf.format(currentDate);
		System.out.println(date1);

	}
}
