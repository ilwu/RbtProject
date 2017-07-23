package com.rbt.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.rbt.exception.UtilException;

/**
 * 日期通用工具
 * 
 * @author Allen
 */
public class DateUtil {

	/**
	 * LOG4j
	 */
	private static Logger LOG = Logger.getLogger(DateUtil.class);

	/**
	 * DATE TYPE : 只取日期
	 */
	public static final String TYPE_DATE = "D";
	/**
	 * DATE TYPE : 只取時間
	 */
	public static final String TYPE_TIME = "T";
	/**
	 * DATE TYPE : 取得日期及時間
	 */
	public static final String TYPE_DATETIME = "DT";
	/**
	 * DATE STYLE :XML格式 CCYY-MM-DDThh:mm:ss+hh:ss
	 */
	public static final String STYLE_XML = "X";
	/**
	 * DATE STYLE :西元年 CCYYMMDDhhmmss
	 */
	public static final String STYLE_AD = "AD";
	/**
	 * DATE STYLE :民國年 YYYMMDDhhmmss
	 */
	public static final String STYLE_ROC = "R";
	/**
	 * DATE STYLE :格式化 CCYY-MM-DD hh:mm:ss (類似xml格式,但沒有zonetime)
	 */
	public static final String STYLE_FORMAT = "F";
	/**
	 * DATE STYLE :格式化 CCYY/MM/DD hh:mm:ss
	 */
	public static final String STYLE_FORMAT_FOR_USER = "FU";

	/**
	 *
	 */
	public DateUtil() {
	}

	/**************************************************
	 * Parse Date to GMT
	 * 
	 * @param String
	 *            Format:yyyymmdd
	 * @return long - Returns the number of seconds since January 1, 1970,
	 *         00:00:00 GMT
	 * @exception none
	 **************************************************/
	public static long parseDate2Long(String date) {

		// set Date
		int year, month, day;
		long gtmlong;

		year = Integer.parseInt(date.substring(0, 4));
		month = Integer.parseInt(date.substring(4, 6));
		day = Integer.parseInt(date.substring(6, 8));

		Date dt = new Date(year - 1900, month - 1, day, 0, 0, 0);
		// parsing GMT to long Integer
		gtmlong = Date.parse(dt.toGMTString());
		if (gtmlong == -28800000)
			gtmlong = 0;
		// return seconds
		return gtmlong / 1000L;
	}

	/***************************************************************************
	 * Parse GMT time to Date
	 *
	 * @param long
	 * @return String - local current DateTime Formate:yyyy/mm/dd
	 * @exception none
	 **************************************************************************/
	public static String parseLong2Date(long date) {

		String sdate, year, month, day;// , hours, mins, secs;

		// get Date instance for long Integer
		date = date * 1000L; // -- set Date with milliseconds
		Date dt = new Date(date);

		// transfer Date to String
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);

		year = String.valueOf(cal.get(Calendar.YEAR));
		month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

		if (day.length() == 1)
			day = "0" + day;
		if (month.length() == 1)
			month = "0" + month;
		sdate = year + "/" + month + "/" + day;
		// return Localtime
		return sdate;
	}

	// =============================================================================
	// 取得當下日期時間
	// =============================================================================
	/**
	 * 取得目前系統時間 預設格式(yyyy-MM-dd hh:mm:ss)
	 * 
	 * @return
	 */
	public static String getCurrentDateTime() {
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
	}

	/**
	 * 取得目前系統時間
	 * 
	 * @param format
	 *            SimpleDateFormat 格式設定
	 * @return
	 */
	public static String getCurrentDateTime(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	/**
	 * 取得目前日期時間
	 * 
	 * @param type
	 *            取得日期或時間 參考指定 DateUtil.TYPE_XXXX
	 * @param style
	 *            產生字串的格式 參考指定 DateUtil.STYLE_XXXX
	 * @return
	 */
	public synchronized static String getCurrentDateTime(String type, String style) {
		Calendar calendar = Calendar.getInstance();
		return formatDateTime(calendar, type, style);
	}

	/**
	 * 取得今日
	 *
	 * @param none
	 * @return String 今日日期 (格式: yyyyMMdd)
	 * @exception none
	 */
	public static String getToday() {
		Calendar calendar = Calendar.getInstance();
		int tYear = calendar.get(Calendar.YEAR);
		int tMonth = calendar.get(Calendar.MONTH) + 1;
		int tDate = calendar.get(Calendar.DATE);
		return String.valueOf(tYear * 10000 + tMonth * 100 + tDate);
	}

	// =============================================================================
	// Get
	// =============================================================================
	/**
	 * 返回指定日期的月份;date Formate yyyymmdd
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonthFromStr(String date) {
		if (StringUtil.isEmpty(date) || date.length() != 8) {
			throw new UtilException("getMonthOfDay : 傳入日期不合法:[" + date + "]");
		}
		String month = date.substring(4, 6);
		return Integer.parseInt(month);
	}

	/**
	 * 取得當日星期
	 * 
	 * @param today
	 *            日期 yyyyMMdd
	 * @return int 星期日 :1 星期一 :2 星期二 :3 星期三 :4 星期四 :5 星期五 :6 星期六 :7
	 */
	static public int getDayOfWeekFromStr(String today) {
		int Now_yyyy = Integer.parseInt(today.substring(0, 4));
		int Now_mm = Integer.parseInt(today.substring(4, 6)) - 1;
		int date = Integer.parseInt(today.substring(6, 8));

		Calendar dd = Calendar.getInstance();
		dd.clear();
		dd.set(Calendar.YEAR, Now_yyyy);
		dd.set(Calendar.MONTH, Now_mm);
		dd.set(Calendar.DATE, date);

		return dd.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 取得當日星期
	 * 
	 * @param today
	 *            日期 yyyyMMdd
	 * @return String 星期日 :日 星期一 :一 星期二 :二 星期三 :三 星期四 :四 星期五 :五 星期六 :六
	 */
	static public String getChtDayOfWeekFormStr(String today) {
		int week = getDayOfWeekFromStr(today);
		if (week == 1)
			return "日";
		if (week == 2)
			return "一";
		if (week == 3)
			return "二";
		if (week == 4)
			return "三";
		if (week == 5)
			return "四";
		if (week == 6)
			return "五";
		if (week == 7)
			return "六";
		return week + "";
	}

	/**
	 * 取得當月的最大天數
	 * 
	 * @param dateStr
	 *            日期
	 * @return int 該日期所在月份的最大天數
	 * @exception none
	 */
	public static int getMaxMonthDayFromStr(String dateStr) {
		if (dateStr == null || dateStr.length() < 6) {
			throw new UtilException("DateUtil.getMaxMonthDay : 傳入日期不合法:[" + dateStr + "]");
		}
		int Now_yyyy = Integer.parseInt(dateStr.substring(0, 4));
		int Now_mm = Integer.parseInt(dateStr.substring(4, 6)) - 1;

		Calendar dd = Calendar.getInstance();
		dd.clear();
		dd.set(Calendar.YEAR, Now_yyyy);
		dd.set(Calendar.MONTH, Now_mm);

		return dd.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	// =============================================================================
	// is 判斷
	// =============================================================================
	/**
	 * 是否當月的最後一天
	 * 
	 * @param currDate
	 *            檢查日期 格式 yyyyMMdd
	 * @return boolean true: 當月的最後一天, false: 非當月的最後一天
	 */
	public static boolean isMaxMonthDay(String currDate) {
		int day = 0;
		// Get day
		day = Integer.parseInt(currDate.substring(6, 8));

		// Processing
		if (day == getMaxMonthDayFromStr(currDate)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否為上班日 (排除週六、週日)
	 * 
	 * @return
	 */
	public static boolean isBusinessDay() {
		return DateUtil.isBusinessDay(Calendar.getInstance());
	}

	/**
	 * 是否為上班日 (排除週六、週日)
	 * 
	 * @param calendar
	 * @return
	 */
	public static boolean isBusinessDay(Calendar calendar) {
		boolean isBusinessDay = true;
		int iToday = 0;
		iToday = calendar.get(Calendar.DAY_OF_WEEK);
		if (iToday == Calendar.SUNDAY || iToday == Calendar.SATURDAY) {
			isBusinessDay = false;
		}
		return isBusinessDay;
	}

	/**
	 * 檢查傳入時間是否合法(可傳入xmlTime及strTime) ex. 2001-11-12 or 15:16:17 or
	 * 2001-12-17T15:16:17+08:00 ex. 20011112 or 151617 or 20011112151617 ex.
	 * 2001-11-12 15:16:17
	 * 
	 * @param dateString
	 * @return
	 */
	public synchronized static boolean isDateTime(String dateString) {
		boolean isValid = false;
		try {
			if (dateString.length() == 25 || dateString.length() == 10
					|| (dateString.length() == 8 && dateString.indexOf(":") == 2)) {
				dateString = DateUtil.convertXMLTime2Str(dateString);
			} else if (dateString.length() == 19) {
				dateString = DateUtil.convertDateTime2Str(dateString);
			}
			DateUtil.convertStr2Calendar(dateString);
			isValid = true;
		} catch (Exception e) {
			LOG.debug(" checkDateIsValid Exception :" + e.toString());
		}
		return isValid;
	}

	// =============================================================================
	// Convert
	// =============================================================================
	/**
	 * 將傳入之字串日期格式轉為calendar,若不合法則throw Exception ex. 20011112 or 20011112151617
	 * or 151617
	 * 
	 * @param dateString
	 * @return
	 * @throws Exception
	 */
	public synchronized static Calendar convertStr2Calendar(String dateString) {
		int year = 0, month = 0, date = 0, hour = 0, min = 0, sec = 0, myLen = 0;
		if (dateString == null) {
			throw new UtilException("DateUtil.convertStr2Calendar: 傳入日期時間為null!");
		}
		myLen = dateString.length();
		if (myLen == 8 || myLen == 14) {
			year = Integer.parseInt(dateString.substring(0, 4));
			month = Integer.parseInt(dateString.substring(4, 6)) - 1;
			date = Integer.parseInt(dateString.substring(6, 8));
			if (myLen == 14) {
				dateString = dateString.substring(8);
			}
		}

		if (dateString.length() == 6) {
			hour = Integer.parseInt(dateString.substring(0, 2));
			min = Integer.parseInt(dateString.substring(2, 4));
			sec = Integer.parseInt(dateString.substring(4, 6));
		}

		Calendar calendarObj = Calendar.getInstance();
		try {
			if (myLen == 8) {
				calendarObj.set(year, month, date);
				if (year != calendarObj.get(Calendar.YEAR) || month != (calendarObj.get(Calendar.MONTH))
						|| date != calendarObj.get(Calendar.DATE)) {
					throw new UtilException("DateUtil.convertStr2Calendar: 傳入日期錯誤!");
				}
			} else if (myLen == 6) {
				if (hour < 0 || hour >= 24 || min < 0 || min >= 60 || sec < 0 || sec >= 60) {
					throw new UtilException("DateUtil.convertStr2Calendar: 傳入時間錯誤!");
				}
			} else if (myLen == 14) {
				calendarObj.set(year, month, date, hour, min, sec);
				if (year != calendarObj.get(Calendar.YEAR) || month != (calendarObj.get(Calendar.MONTH))
						|| date != calendarObj.get(Calendar.DATE) || hour != calendarObj.get(Calendar.HOUR_OF_DAY)
						|| min != calendarObj.get(Calendar.MINUTE) || sec != calendarObj.get(Calendar.SECOND)) {
					throw new UtilException("DateUtil.convertStr2Calendar: 傳入日期或時間錯誤!");
				}
			} else {
				throw new UtilException("DateUtil.convertStr2Calendar: 傳入長度錯誤!");
			}
		} catch (Exception e) {
			LOG.error("DateUtil.convertStr2Calendar: 傳入日期時間不合法,無法轉換Calendar!");
			LOG.error(StringUtil.getExceptionStackTrace(e));
			throw new UtilException("DateUtil.convertStr2Calendar: 傳入日期時間不合法,無法轉換Calendar!" + e.getMessage());
		}
		return calendarObj;
	}

	/**
	 * 轉民國年
	 *
	 * @param today
	 *            日期 YYYYMMDD
	 * @return int \uFFFD
	 * @exception none
	 */
	public static String getDateOfROC(String date) {
		int year = Integer.parseInt(date.substring(0, 4));
		year = year - 1911;
		String tmp = ("0" + Integer.toString(year));
		date = tmp.substring(tmp.length() - 3, tmp.length()) + date.substring(4);
		return date;
	}

	/**
	 * 民國年轉西元年
	 *
	 * @param today
	 *            日期 YYYYMMDD
	 * @return int \uFFFD
	 * @exception none
	 */
	public static String ROC2Date(String date) {
		// 民國年轉西元年
		int year = Integer.parseInt(date.substring(0, 3));
		year = year + 1911;
		String tmp = Integer.toString(year);
		String rt = tmp + date.substring(3);
		return rt;
	}

	/**
	 * 日期格式化函數 YYYY/MM/DD --> YYYYMMDD
	 * 
	 * @param date:
	 *            格式化前的字符串,長度必須為10,且是YYYY/MM/DD格式; ?? 如果為null,或空串或只有空格的字符串,返回空串;
	 *            ?? 如果長度是不為8的字符串,返回空串;
	 * @return 格式為: YYYYMMDD 的字符串;
	 */
	public static String convertDate2Str(String date) {
		if (date == null)
			return "";
		date = date.trim();
		if (date.equals("&nbsp;"))
			return "";
		if (date.length() == 0 || date.length() != 10)
			return "";
		date = date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10);
		return date;
	}

	/**
	 * 還原格式化之DateTime(適用西元str) ex. 2001/12/17 ==> 20011217 ex. 2001-12-17 ==>
	 * 20011217 ex. 16:15:17 ==> 161517 ex. 2001-11-12T15:16:17+08:00 ==>
	 * 20011112151617
	 * 
	 * @param myDateTime
	 * @return
	 */
	public synchronized static String convertDateTime2Str(String myDateTime) {
		String rtnDateTime = "";
		if (myDateTime == null)
			return "";
		if (myDateTime.length() == 10 || myDateTime.length() == 19) {
			rtnDateTime = myDateTime.substring(0, 4) + myDateTime.substring(5, 7) + myDateTime.substring(8, 10);
			if (myDateTime.length() == 19) {
				myDateTime = myDateTime.substring(11);
			}
		}
		if (myDateTime.length() == 8) {
			rtnDateTime = rtnDateTime + myDateTime.substring(0, 2) + myDateTime.substring(3, 5)
					+ myDateTime.substring(6, 8);
		}
		return rtnDateTime;
	}

	/**
	 * 還原格式化之Time(適用西元str) ex. 16:15 ==> 1615 ex. 16:15:17 ==> 161517
	 * 
	 * @param myDateTime
	 * @return
	 */
	public synchronized static String convertTime2Str(String myDateTime) {
		String rtnDateTime = "";
		if (myDateTime == null)
			return "";
		if (myDateTime.length() == 5) {
			rtnDateTime = myDateTime.substring(0, 2) + myDateTime.substring(3, 5);
		}
		if (myDateTime.length() == 8) {
			rtnDateTime = myDateTime.substring(0, 2) + myDateTime.substring(3, 5) + myDateTime.substring(6, 8);
		}
		return rtnDateTime;
	}

	/**
	 * 轉換字串格式日期時間為XML格式日期時間(timezone=+08:00) ex. 20011112 ==> 2001-11-12 ex.
	 * 20011112151617 ==> 2001-11-12T15:16:17+08:00
	 * 
	 * @param xmlTime
	 * @return
	 * @throws Exception
	 */
	public synchronized static String convertStr2XMLTime(String xmlTime) {
		String rtnDateTime = "";
		String timezone = "+08:00";
		if (xmlTime == null || (xmlTime.length() != 14 && xmlTime.length() != 6 && xmlTime.length() != 8)) {
			throw new UtilException("strTime2XMLTime():傳入日期時間不合法,無法轉換!");
		}
		if (xmlTime.length() == 6 || xmlTime.length() == 8) {
			rtnDateTime = formatDateTime(xmlTime);
		} else if (xmlTime.length() == 14) {
			rtnDateTime = formatDateTime(xmlTime.substring(0, 8)) + "T" + formatDateTime(xmlTime.substring(8, 14))
					+ timezone;
		}
		return rtnDateTime;
	}

	/**
	 * 轉換XML格式日期時間為字串格式日期時間 ex. 2001-12-21 or 16:35:45 or 25碼xml格式日期
	 * 
	 * @param strTime
	 * @return
	 * @throws Exception
	 */
	public synchronized static String convertXMLTime2Str(String strTime) throws Exception {
		String rtnDateTime = "";
		if (strTime == null || (strTime.length() != 25 && strTime.length() != 10 && strTime.length() != 8)) {
			throw new Exception("xmlTime2StrTime():傳入日期時間不合法,無法轉換!");
		}
		if (strTime.length() == 10 || strTime.length() == 8) {
			rtnDateTime = convertDateTime2Str(strTime);
		} else if (strTime.length() == 25) {
			rtnDateTime = convertDateTime2Str(strTime.substring(0, 19));
		}
		return rtnDateTime;
	}

	/**
	 * 西元年轉Unix時間格式
	 * 
	 * @param today
	 *            日期 yyyyMMddhhmmss
	 * @return int \uFFFD
	 * @exception none
	 */
	public static long convertStr2UnixDate(String s) throws Exception {
		if (s == null || "".equals(s) || s.length() != 14) {
			return 0;
		}
		Calendar cl = Calendar.getInstance();
		int year = Integer.parseInt(s.substring(0, 4));
		int month = Integer.parseInt(s.substring(4, 6));
		int date = Integer.parseInt(s.substring(6, 8));
		int hh = Integer.parseInt(s.substring(8, 10));
		int mm = Integer.parseInt(s.substring(10, 12));
		int ss = Integer.parseInt(s.substring(12, 14));
		cl.set(year, month - 1, date, hh, mm, ss);
		long end = cl.getTimeInMillis();
		cl.set(1970, 0, 1, 0, 0, 0);
		long start = cl.getTimeInMillis();
		long seconds = (end - start) / 1000;
		return seconds;
	}

	/**
	 * Unix時間格式轉西元年時分秒
	 *
	 * @param today
	 *            日期 YYYYMMDD
	 * @return int \uFFFD
	 * @exception none
	 */
	public static String convertLong2UnixDate(long second) throws Exception {
		Calendar c = DateUtil.convertStr2Calendar("19700101000000");
		c.add(Calendar.SECOND, (int) second);
		return new SimpleDateFormat("yyyyMMddHHmmss").format(c.getTime());
	}

	// =============================================================================
	// 日期移動
	// =============================================================================
	/**
	 * 以年為單位
	 */
	public static final int ADD_DATE_TYPE_YEAR = Calendar.YEAR;
	/**
	 * 以月為單位
	 */
	public static final int ADD_DATE_TYPE_MONTH = Calendar.MONTH;
	/**
	 * 以週為單位
	 */
	public static final int ADD_DATE_TYPE_WEEK = Calendar.WEEK_OF_YEAR;
	/**
	 * 以日為單位
	 */
	public static final int ADD_DATE_TYPE_DAY = Calendar.DAY_OF_YEAR;

	/**
	 * 加減日期
	 * 
	 * @param date
	 *            Date 物件
	 * @param ROLL_DATE_TYPE
	 *            DateUtil.ROLL_DATE_TYPE_XXX 或 Calendar.XXXX
	 * @param value
	 *            值
	 * @param format
	 *            輸出字串格式化參數
	 * @return String
	 */
	public static String addDate(Date date, int ROLL_DATE_TYPE, int value, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(ROLL_DATE_TYPE, value);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(calendar.getTime());
	}

	/**
	 * 取得某日期幾天前或幾天後之日期(須為標準字串格式) ex. 20011112 ==> 20011115(加3天) ex. 20011112 ==>
	 * 20011109(減3天)
	 * 
	 * @param orgDate
	 * @param dayCnt
	 * @return
	 * @throws Exception
	 */
	public synchronized static String countDate(String orgDate, int dayCnt) throws Exception {
		String myDateLine = null;
		String myType = null;
		Calendar calendar = convertStr2Calendar(orgDate);
		calendar.add(Calendar.DATE, dayCnt);
		if (orgDate.length() == 8) {
			myType = TYPE_DATE;
		} else if (orgDate.length() == 14) {
			myType = TYPE_DATETIME;
		}
		myDateLine = formatDateTime(calendar, myType, STYLE_AD);
		return myDateLine;
	}

	/**
	 * 取得某日期幾月前或幾月後之日期(須為標準字串格式) ex. 20040112 ==> 20040412(加3月) ex. 20040112 ==>
	 * 20040412(減3月)
	 * 
	 * @param orgDate
	 * @param monthCnt
	 * @return
	 */
	public synchronized static String countMonth(String orgDate, int monthCnt) {
		String myDateLine = null;
		String myType = null;
		try {

			Calendar calendar = convertStr2Calendar(orgDate);
			calendar.add(Calendar.MONTH, monthCnt);
			if (orgDate.length() == 8) {
				myType = TYPE_DATE;
			} else if (orgDate.length() == 14) {
				myType = TYPE_DATETIME;
			}
			myDateLine = formatDateTime(calendar, myType, STYLE_AD);
		} catch (Exception e) {
			LOG.debug("countMonth error:" + e.toString());
		}
		return myDateLine;
	}

	/**
	 * 取得某日期幾年前或幾年後之日期(須為標準字串格式) ex. 20041112 ==> 20071112(加3年) ex. 20041112 ==>
	 * 20011112(減3年)
	 * 
	 * @param orgDate
	 * @param yearCnt
	 * @return
	 * @throws Exception
	 */
	public synchronized static String countYear(String orgDate, int yearCnt) throws Exception {
		String myDateLine = null;
		String myType = null;
		Calendar calendar = convertStr2Calendar(orgDate);
		calendar.add(Calendar.YEAR, yearCnt);
		if (orgDate.length() == 8) {
			myType = TYPE_DATE;
		} else if (orgDate.length() == 14) {
			myType = TYPE_DATETIME;
		}
		myDateLine = formatDateTime(calendar, myType, STYLE_AD);
		return myDateLine;
	}

	/**
	 * 取得某日期幾秒前或幾秒後之日期時間(須為標準字串格式) ex. 20011112121231 ==> 20011115121232(加1秒)
	 * ex. 20011112121231 ==> 20011112121228(減3秒)
	 * 
	 * @param orgDttm
	 * @param secondCnt
	 * @return
	 * @throws Exception
	 */
	public synchronized static String countSecond(String orgDttm, int secondCnt) throws Exception {
		String myDateLine = null;
		String myType = null;
		Calendar calendar = convertStr2Calendar(orgDttm);
		calendar.add(Calendar.SECOND, secondCnt);
		myType = TYPE_DATETIME;
		myDateLine = formatDateTime(calendar, myType, STYLE_AD);
		return myDateLine;
	}

	// =============================================================================
	// Date String Formatter
	// =============================================================================

	/**
	 * 取得某個calendar之時間日期 依據type及style取得字串
	 * 
	 * @param calendar
	 * @param type
	 *            取得日期或時間 參考指定 DateUtil.TYPE_XXXX
	 * @param style
	 *            產生字串的格式 參考指定 DateUtil.STYLE_XXXX
	 * @return
	 * @throws Exception
	 */
	public synchronized static String formatDateTime(Calendar calendar, String type, String style) {
		String myDateTime = "";

		// =========================================================
		// 預設格式，未指定時 預設為 西元 日期 + 時間
		// =========================================================
		if (type == null || type.equals("")) {
			type = TYPE_DATETIME;
		}
		if (style == null || style.equals("")) {
			style = STYLE_AD;
		}

		// =========================================================
		// 取得日期、時間字串
		// =========================================================
		String year, month, day, hour, min, sec;

		// 年
		if (style.equals(STYLE_ROC)) {
			// 民國年(西元-1911)，左補3個0
			year = StringUtil.paddingAndSplit((calendar.get(Calendar.YEAR) - 1911) + "", "0", 3, true);
		} else {
			// 西元年，左補4個0
			year = StringUtil.paddingAndSplit(calendar.get(Calendar.YEAR) + "", "0", 4, true);
		}
		// 月
		month = StringUtil.paddingAndSplit((calendar.get(Calendar.MONTH) + 1) + "", "0", 2, true);
		// 日
		day = StringUtil.paddingAndSplit(calendar.get(Calendar.DATE) + "", "0", 2, true);
		// 時
		hour = StringUtil.paddingAndSplit(calendar.get(Calendar.HOUR_OF_DAY) + "", "0", 2, true);
		// 分
		min = StringUtil.paddingAndSplit(calendar.get(Calendar.MINUTE) + "", "0", 2, true);
		// 秒
		sec = StringUtil.paddingAndSplit(calendar.get(Calendar.SECOND) + "", "0", 2, true);

		if (type.equals(TYPE_DATE) || type.equals(TYPE_DATETIME)) {
			myDateTime = year + month + day;
		}
		if (type.equals(TYPE_TIME) || type.equals(TYPE_DATETIME)) {
			myDateTime = myDateTime + hour + min + sec;
		}

		if (style.equals(STYLE_FORMAT)) {
			// 格式化 CCYY-MM-DD hh:mm:ss
			myDateTime = formatDateTime(myDateTime);
		} else if (style.equals(STYLE_XML)) {
			// XML格式 CCYY-MM-DDThh:mm:ss+hh:ss
			myDateTime = convertStr2XMLTime(myDateTime);
		} else if (style.equals(STYLE_FORMAT_FOR_USER)) {
			// 格式化 CCYY/MM/DD hh:mm:ss
			myDateTime = formateDateTimeForUser(myDateTime);
		}

		return myDateTime;
	}

	/**
	 * 日期格式化函數
	 * 
	 * @param date:
	 *            格式化前的字符串,長度必須為8,且是YYYYMMDD格式; ?? 如果為null,或空串或只有空格的字符串,返回空串; ??
	 *            如果長度是不為8的字符串,返回空串;
	 * @return 格式為: YYYY/MM/DD 的字符串;
	 */
	public static String formatDate(String date) {
		return DateUtil.formatDate(date, "/");
	}

	/**
	 * 日期格式化函數
	 * 
	 * @param date:
	 *            格式化前的字符串,長度必須為8,且是YYYYMMDD格式; ?? 如果為null,或空串或只有空格的字符串,返回空串; ??
	 *            如果長度是不為8的字符串,返回空串;
	 * @param splitChar
	 *            分割符號
	 * @return
	 */
	public static String formatDate(String date, String splitChar) {
		if (date == null)
			return "";
		date = date.trim();
		if (date.equals("&nbsp;"))
			return "";
		if (date.length() == 0 || date.length() != 8)
			return "";
		date = date.substring(0, 4) + splitChar + date.substring(4, 6) + splitChar + date.substring(6, 8);
		return date;
	}

	/**
	 * 將字串時間做格式化給USER看的格式 ex. 1516 ==> 15:16
	 * 
	 * @param myDateTime
	 * @return
	 */
	public synchronized static String formatTime(String myDateTime) {
		String rtnDateTime = "";
		if (myDateTime == null)
			return "";
		if (myDateTime.length() >= 4) {
			rtnDateTime = rtnDateTime + myDateTime.substring(0, 2) + ":" + myDateTime.substring(2, 4);
		}
		return rtnDateTime;
	}

	/**
	 * 將字串時間做格式化 ex. 20011217 ==> 2001-12-17 ex. 151617 ==> 15:16:17 ex.
	 * 20011217151617 ==> 2001-12-17 15:16:17
	 * 
	 * @param myDateTime
	 * @return
	 */
	public synchronized static String formatDateTime(String myDateTime) {
		String rtnDateTime = "";
		if (myDateTime == null)
			return "";
		if (myDateTime.length() == 8 || myDateTime.length() == 14) {
			rtnDateTime = myDateTime.substring(0, 4) + "-" + myDateTime.substring(4, 6) + "-"
					+ myDateTime.substring(6, 8);
			if (myDateTime.length() == 14) {
				rtnDateTime = rtnDateTime + " ";
				myDateTime = myDateTime.substring(8);
			}
		}
		if (myDateTime.length() == 6) {
			rtnDateTime = rtnDateTime + myDateTime.substring(0, 2) + ":" + myDateTime.substring(2, 4) + ":"
					+ myDateTime.substring(4, 6);
		}
		return rtnDateTime;
	}

	/**
	 * 將字串時間做格式化給USER看的格式 ex. 151617 ==> 15:16:17
	 * 
	 * @param myDateTime
	 * @return
	 */
	public synchronized static String formatTimeForUser(String myDateTime) {
		String rtnDateTime = "";
		if (myDateTime == null)
			return "";
		if (myDateTime.length() >= 6) {
			rtnDateTime = rtnDateTime + myDateTime.substring(0, 2) + ":" + myDateTime.substring(2, 4) + ":"
					+ myDateTime.substring(4, 6);
		}
		return rtnDateTime;
	}

	/**
	 * 將字串時間做格式化給USER看的格式 ex. 20011217 ==> 2001/12/17 ex. 151617 ==> 15:16:17
	 * ex. 20011217151617 ==> 2001/12/17 15:16:17
	 * 
	 * @param myDateTime
	 * @return
	 */
	public synchronized static String formateDateTimeForUser(String myDateTime) {
		String rtnDateTime = "";
		if (myDateTime == null) {
			return "";
		}
		if (myDateTime.length() == 17) {
			myDateTime = myDateTime.substring(0, 14);
		}
		if (myDateTime.length() == 8 || myDateTime.length() == 14 || myDateTime.length() == 10) {
			rtnDateTime = myDateTime.substring(0, 4) + "/" + myDateTime.substring(4, 6) + "/"
					+ myDateTime.substring(6, 8);
			if (myDateTime.length() == 14) {
				rtnDateTime = rtnDateTime + " ";
				myDateTime = myDateTime.substring(8);
			}
			// added by Glenn Tung for some date formate length is 10
			if (myDateTime.length() == 10) {
				rtnDateTime = rtnDateTime + " ";
				myDateTime = myDateTime.substring(8);
			}
		}
		if (myDateTime.length() == 6) {
			rtnDateTime = rtnDateTime + myDateTime.substring(0, 2) + ":" + myDateTime.substring(2, 4) + ":"
					+ myDateTime.substring(4, 6);
		}
		if (myDateTime.length() == 2) {
			rtnDateTime = rtnDateTime + myDateTime.substring(0, 2);
		}

		return rtnDateTime;
	}

	/**
	 * 秒數換算:時/分/秒
	 * @param 秒數
	 * @return hh:mm:ss
	 */
	public static String timeFormat(int t)
	{
		long hour = t / 3600; // 小時
		long minute = t % 3600 / 60; // 分鐘
		long second = t % 60; // 秒
		return StringUtil.padding(hour, "0", 2, true) + ":" + 
				StringUtil.padding(minute, "0", 2, true) + ":" + 
		StringUtil.padding(second, "0", 2, true);
	}
}
