package com.rbt.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;

public class FmtUtil {

	/**
	 * Log4j
	 */
	static Logger LOG = Logger.getLogger(StringUtil.class);

	/**
	 * 數字格式化函數
	 * @param  number: 格式化前的數字;
	 * @param  decimalDigits: 小數位數;
	 * @return: 三位一組以逗號分割的字符串;
	 */
	public static String format(double number, int decimalDigits) {
		if (number == 0d) number = 0d;

		boolean flag=false;
		if(decimalDigits < 0) {
			LOG.debug("小數位數不能小於0.");
			return "";
		}

		String pattern = "###,###,###,###,###,###";
		if(decimalDigits > 0) {
			flag=true;
			pattern += ".";
			for(int i=0;i<decimalDigits;i++) {
				pattern += "0";
			}
		}

		DecimalFormat df = new DecimalFormat(pattern);
		if (number <= -1d){
			return df.format(number);
		}else if (number > -1d && number < 0d){
			return "-0"+df.format(number).substring(1);
		}else if (number >= 0d && number < 1d){
			if(flag==true){
				return "0"+df.format(number);
			}
			return df.format(number);
		}else{
			return df.format(number);
		}
	}

	/**
	 * 數字格式化函數
	 * @param  number: 格式化前的字符串(是一個數字);
	 * @param  decimalDigits: 小數位數;
	 * @return: 三位一組以逗號分割的字符串,如果為null,或空串或只有空格的字符串,返回空串;
	 */
	public static String format(String s, int decimalDigits) {
		if(s == null) return "";
		s = s.trim();
		if(s.equals("&nbsp;")) return "";
		if(s.length() == 0) return "";
		double number = Double.parseDouble(s);
		return format(number,decimalDigits);
	}

	/**
	 * 四捨五入 格式化數字 eg.20000.335--->20000.34
	 * @param s
	 * @param decimalDigits
	 * @return
	 */
	public static String formatNumber(String s ,int decimalDigits){
		if(s == null) return "";
		s = s.trim();
		if(s.equals("&nbsp;")) return "";
		if(s.length() == 0) return "";
		BigDecimal bd = new BigDecimal(s);
		BigDecimal db1 = bd.setScale(decimalDigits,BigDecimal.ROUND_HALF_UP);
		return db1.toString();
	}


	/**
	 * 四捨五入 格式化數字 eg.20000.335--->20,000.34(帶逗號分割)
	 * @param s
	 * @param decimalDigits
	 * @return
	 */
	public static String formatNumberWithD(String  s ,int decimalDigits){
		return format(formatNumber(s,decimalDigits),decimalDigits);
	}

	/**
	 * @param bd
	 * @param decimalDigits
	 * @return
	 */
	public static String formatBigNumber(BigDecimal bd, int decimalDigits) {
		if(bd.intValue()<=0){
			return "&nbsp;";
		}
		double number=0;
		number=bd.doubleValue();
		if (number == 0d) number = 0d;

		boolean flag=false;
		if(decimalDigits < 0) {
			LOG.debug("小數位數不能小於0.");
			return "";
		}

		String pattern = "###,###,###,###,###,###";
		if(decimalDigits > 0) {
			flag=true;
			pattern += ".";
			for(int i=0;i<decimalDigits;i++) {
				pattern += "0";
			}
		}

		DecimalFormat df = new DecimalFormat(pattern);
		if (number <= -1d){
			return df.format(number);
		}else if (number > -1d && number < 0d){
			return "-0"+df.format(number).substring(1);
		}else if (number >= 0d && number < 1d){
			if(flag==true){
				return "0"+df.format(number);
			}
			return df.format(number);
		}else{
			return df.format(number);
		}
	}

	//=============================================================================
	// formatEDouble
	//=============================================================================
	/**
	 * @method name: formatEDouble()
	 * @author added by  Fong Yu 2005-12-19
	 * @param number
	 * @param decimalDigits
	 * @return
	 */
	public static String formatEDouble(double number, int decimalDigits) {
		if (number == 0d) number = 0d;

		boolean flag=false;
		if(decimalDigits < 0) {
			LOG.debug("小數位數不能小於0.");
			return "";
		}

		String pattern = "##################";
		if(decimalDigits > 0) {
			flag=true;
			pattern += ".";
			for(int i=0;i<decimalDigits;i++) {
				pattern += "0";
			}
		}

		DecimalFormat df = new DecimalFormat(pattern);
		if (number <= -1d){
			return df.format(number);
		}else if (number > -1d && number < 0d){
			return "-0"+df.format(number).substring(1);
		}else if (number >= 0d && number < 1d){
			if(flag==true){
				return "0"+df.format(number);
			}
			return df.format(number);
		}else{
			return df.format(number);
		}
	}

	/**
	 * @method name: formatEDouble()
	 * @author added by  Fong Yu 2005-12-19
	 * @param number
	 * @param decimalDigits
	 * @return
	 */
	public static String formatEDouble(String snumber, int decimalDigits) {
		double number = 0d;
		if (snumber == null)
			return "";
		boolean flag=false;
		if(decimalDigits < 0) {
			LOG.debug("小數位數不能小於0.");
			return "";
		}

		String pattern = "##################";
		if(decimalDigits > 0) {
			flag=true;
			pattern += ".";
			for(int i=0;i<decimalDigits;i++) {
				pattern += "0";
			}
		}

		DecimalFormat df = new DecimalFormat(pattern);
		if (number <= -1d){
			return df.format(number);
		}else if (number > -1d && number < 0d){
			return "-0"+df.format(number).substring(1);
		}else if (number >= 0d && number < 1d){
			if(flag==true){
				return "0"+df.format(number);
			}
			return df.format(number);
		}else{
			return df.format(number);
		}
	}

	/**
	 * @method name: formatEDoubleWithDot()
	 * @author added by  Fong Yu 2006-2-7
	 * @param number
	 * @param decimalDigits
	 * @return
	 */
	public static String formatEDoubleWithDot(double number, int decimalDigits) {
		if (number == 0d) number = 0d;

		boolean flag=false;
		if(decimalDigits < 0) {
			LOG.debug("小數位數不能小於0.");
			return "";
		}

		String pattern = "###,###,###,###,###,###";
		if(decimalDigits > 0) {
			flag=true;
			pattern += ".";
			for(int i=0;i<decimalDigits;i++) {
				pattern += "0";
			}
		}

		DecimalFormat df = new DecimalFormat(pattern);
		if (number <= -1d){
			return df.format(number);
		}else if (number > -1d && number < 0d){
			return "-0"+df.format(number).substring(1);
		}else if (number >= 0d && number < 1d){
			if(flag==true){
				return "0"+df.format(number);
			}
			return df.format(number);
		}else{
			return df.format(number);
		}
	}

	//
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//
	}
}
