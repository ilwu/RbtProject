package com.rbt.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rbt.exception.UtilException;

/**
 * 字串處理工具
 * @author Allen Wu
 */
public class StringUtil {

	/**
	 * 計算關鍵字在字串中出現的次數
	 * @param str 比對字串
	 * @param keyWord 關鍵字元
	 * @return 次數
	 */
	public int countString(String str, String keyWord) {
		int count = 0;

		if (str == null || str.length() < 1 || keyWord == null || keyWord.length() < 1) {
			return 0;
		}

		for (int i = 0; i < str.length(); i++) {
			int endindex = i + keyWord.length();
			String word = "";

			// 剩餘未比對的長度已經不足，直接return
			if (endindex > str.length()) {
				return count;
			}

			word = str.substring(i, endindex);

			if (word.endsWith(keyWord)) {
				count++;
				i += keyWord.length() - 1;
			}
		}
		return count;
	}

	/**
	 * 將String 依斷行符號轉成 String[]
	 * @param str
	 * @return result String[]
	 * @throws IOException
	 */
	public String[] splitLine2Ary(String str) throws IOException {

		if (str == null || "".equals(str))
			return new String[0];

		StringReader stringReader = new StringReader(str);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		List<String> list = new ArrayList<String>();
		// 逐行讀取
		String lineStr = "";
		while ((lineStr = bufferedReader.readLine()) != null) {
			list.add(lineStr);
		}

		return list.toArray(new String[0]);
	}

	// =============================================================================
	// Get some thing
	// =============================================================================
	/**
	 * 取得字符串指定位置的字符
	 * @param str
	 * @param num
	 * @return
	 */
	public static String getStrChar(String str, int num) {
		if (str == null || num > str.length())
			return "";
		return str.substring(num - 1, num);
	}

	/**
	 * 取得 字符串 中 除去preffix開始 和 suffix結尾的部分 。
	 * for example: preffix ="/" ;suffix="*"; str="/test*";
	 * result is : test
	 * @param preffix 前綴
	 * @param str 需要處理字符串
	 * @param suffix 后綴
	 * @return String
	 */
	public static String getMiddleStr(String preffix, String str, String suffix) {
		String result = null;
		if (str != null) {
			if (str.startsWith(preffix)) {
				str = str.replaceFirst(preffix, "");
			}
			if (str.endsWith(suffix)) {
				str = str.substring(0, str.lastIndexOf(suffix));
			}
			result = str;
		}
		return result;
	}

	// =============================================================================
	// Replace
	// =============================================================================
	/**
	 * 將byte中的 null 轉成 space
	 * @param data 資料
	 * @param encodeing 資料編碼
	 * @return result byte[]
	 * @throws UnsupportedEncodingException 資料編碼錯誤時拋出 UnsupportedEncodingException
	 */
	public byte[] replaceNullToSpace(byte[] data, String encodeing) throws UnsupportedEncodingException {

		// 空白 to byte
		byte space = " ".getBytes(encodeing)[0];

		for (int i = 0; i < data.length; i++) {
			if (data[i] == (byte) 0x00) {
				data[i] = space;
			}
		}
		return data;
	}

	/**
	 * 把右斜線『\』替換城左斜線『/』
	 * @param myStr
	 * @return 替換後的字串
	 */
	public static String replaceBacklash(String myStr) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(myStr);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {

			if (character == '\\') {
				result.append("/");
			} else {
				result.append(character);
			}

			character = iterator.next();
		}
		return result.toString();
	}

	/**
	 * 去除字符串中的空格、回車、換行符、製表符
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 解決 replacement 中, 含有 『$』、『\』時造成 exception 的錯誤
	 * @param regex
	 * @param replacement
	 * @return
	 */
	public static String safeReplaceAll(String content, String regex, String replacement) {
		if (StringUtil.isEmpty(content)) {
			return "";
		}
		if (StringUtil.isEmpty(regex)) {
			return content;
		}
		StringBuffer sb = new StringBuffer();
		for (char chr : replacement.toCharArray()) {

			if (chr == 36) {
				// 『$』
				sb.append("\\$");
			} else if (chr == 92) {
				// 『\』
				sb.append("\\\\");
			} else {
				sb.append(chr);
			}
		}

		return content.replaceAll(regex, sb.toString());
	}

	// =============================================================================
	// convert
	// =============================================================================
	/**
	 * 將物件 toString 並轉為整數
	 * @param obj
	 * @return
	 */
	public static int convertStr2Integer(Object obj) {
		if (StringUtil.isEmpty(obj)) {
			return 0;
		}
		return Integer.parseInt(obj.toString());
	}

	/**
	 * input 1---52 ,return 'A'----'Z'
	 * @param i
	 * @return
	 */
	public static String convertInt2Char(int i) {
		if (i <= 26) {
			return ((char) (64 + i)) + "";
		} else if (i <= 52) {
			return "A" + ((char) (64 + i - 26));
		} else if (i > 52) {
			return "B" + ((char) (64 + i - 52));
		} else {
			return "";
		}
	}

	/**
	 *
	 * @param i
	 * @return
	 */
	public static String convertNumner2Chn(String str) {

		String[] chnAry = new String[] { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };

		if (StringUtil.isEmpty(str)) {
			return "";
		}

		StringBuffer sb = new StringBuffer();

		for (char chr : str.toCharArray()) {
			if (StringUtil.isNumber(chr + "")) {
				sb.append(chnAry[Integer.parseInt(chr + "")]);
			} else {
				sb.append(chr);
			}
		}

		return sb.toString();
	}

	/**
	 * htm Encode
	 * @param s HTML
	 * @return result String
	 */
	public static String htmEncode(String s) {
		StringBuffer stringbuffer = new StringBuffer();
		String tstr = s.trim();
		int j = tstr.length();

		for (int i = 0; i < j; i++) {
			char c = tstr.charAt(i);
			switch (c) {

			case 60:
				stringbuffer.append("&lt;");
				break;
			case 62:
				stringbuffer.append("&gt;");
				break;
			case 38:
				stringbuffer.append("&amp;");
				break;
			case 34:
				stringbuffer.append("&quot;");
				break;
			case 169:
				stringbuffer.append("&copy;");
				break;
			case 174:
				stringbuffer.append("&reg;");
				break;
			case 165:
				stringbuffer.append("&yen;");
				break;
			case 8364:
				stringbuffer.append("&euro;");
				break;
			case 8482:
				stringbuffer.append("&#153;");
				break;
			default:
				stringbuffer.append(c);
				break;
			}
		}
		return new String(stringbuffer.toString());
	}

	/**
	 * 半形字元轉全形字元
	 * @param char halfwidthchar
	 * @return char fullwidthchar
	 */
	public static char convertHalf2FullWidth(char halfwidthchar) {
		// 全形文字直接return
		if (String.valueOf(halfwidthchar).getBytes().length != 1)
			return halfwidthchar;
		// 半形空白無法mapping
		if (halfwidthchar == ' ') {
			return '　';
		}
		return (char) (halfwidthchar + 65248);
	}

	/**
	 * 半形字元轉全形字元
	 * @param str
	 * @return String
	 */
	public static String convertHalf2FullWidth(String str) {
		char[] charArray = str.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			charArray[i] = StringUtil.convertHalf2FullWidth(charArray[i]);
		}
		return new String(charArray);
	}

	// =============================================================================
	// Trim
	// =============================================================================
	/**
	 * trim 字串 (避免傳入值為null)
	 * @param s
	 * @return
	 */
	public static String safeTrim(Object s) {
		return safeTrim(s, "");
	}

	/**
	 * trim 字串 (避免傳入值為null)
	 * @param s 傳入字
	 * @param defaultStr 預設字串
	 * @return
	 */
	public static String safeTrim(Object s, String defaultStr) {
		if (s == null || isEmpty(s)) {
			return defaultStr;
		}
		return s.toString().trim();
	}

	/**
	 * 將左邊的某一字元去除
	 * @param str 原始字串
	 * @param ch 要去除的字元
	 * @return result string
	 */
	public String trimLeftChar(String str, char ch) {

		if (str == null)
			return "";

		String sch = String.valueOf(ch);
		for (int i = 0; i < str.length(); i++) {
			if (!sch.equals(String.valueOf(str.charAt(i)))) {
				return str.substring(i);
			}
		}
		return str;
	}

	/**
	 * Trim 全形空白 (包含半形)
	 * @param temp
	 * @return
	 */
	public static String trimFullWidthSpace(String temp) {
		String result = "";
		if (temp != null && !"".equalsIgnoreCase(temp)) {
			int length = temp.length();
			int len = length;
			int st = 0;
			char[] val = temp.toCharArray();
			while ((st < len) && ((val[st] == ' ') || (val[st] == '　'))) {// 前面的半型空白和全型空白
				st++;
			}
			while ((st < len) && ((val[len - 1] == ' ') || (val[len - 1] == '　'))) {// 後面的半型空白和全型空白
				len--;
			}
			result = ((st > 0) || (len < length)) ? temp.substring(st, len) : temp;
		}
		return result;
	}

	// =============================================================================
	// Padding
	// =============================================================================
	/**
	 * Padding Char
	 * @param obj 原始字串(為直接被 toString)
	 * @param paddingChar paddingChar
	 * @param length padding length
	 * @param isLeft 是否補在字串左邊
	 * @throws Exception
	 */
	public static String padding(Object obj, String paddingChar, int length, boolean isLeft) {
		String str = "";
		if (StringUtil.notEmpty(obj)) {
			str += obj;
		}
		return StringUtil.padding(str, paddingChar, length, isLeft);
	}

	/**
	 * Padding Char
	 * @param str 原始字串
	 * @param paddingChar paddingChar
	 * @param length padding length
	 * @param isLeft 是否補在字串左邊
	 * @throws Exception
	 */
	public static String padding(String str, String paddingChar, int length, boolean isLeft) {

		if (str == null)
			str = "";

		if (paddingChar.length() != 1) {
			throw new UtilException("傳入之 paddingChar 長度需為 1!");
		}

		String paddingCharStr = "";
		for (int z = 0; z < length * 2; z++) {
			paddingCharStr += paddingChar;
		}

		// 多字元判斷
		for (int i = 0; i < str.length(); i++) {
			String c = str.substring(i, i + 1);
			if (c.getBytes().length > 1) {
				length = length - 2;
			} else {
				length = length - 1;
			}
		}

		if (length < 1)
			return str;

		if (isLeft) {
			return paddingCharStr.substring(0, length) + str;
		}
		return str + paddingCharStr.substring(0, length);
	}

	/**
	 * 將字串補足或切割到指定長度
	 * @param str 原始字串
	 * @param paddingChar 要補的字元
	 * @param length 指定長度
	 * @param isLeft 處理原字串左邊
	 * @return
	 */
	public static String paddingAndSplit(String str, String paddingChar, int length, boolean isLeft) {
		try {
			return StringUtil.paddingAndSplit(str, paddingChar, length, isLeft, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new UtilException("傳入字串無法正確編碼![" + str + "]");
		}
	}

	/**
	 * 將字串補足或切割到指定長度
	 * @param str 原始字串
	 * @param paddingChar 要補的字元
	 * @param length 指定長度
	 * @param isLeft 處理原字串左邊
	 * @param encodeing 計算長度的指定編碼
	 * @throws UnsupportedEncodingException
	 */
	public static String paddingAndSplit(String str, String paddingChar, int length, boolean isLeft, String encodeing) throws UnsupportedEncodingException {

		// 初始化傳入字串，避免錯誤
		if (str == null)
			str = "";

		// 檢核paddingChar
		if (paddingChar.length() < 1) {
			throw new UtilException("傳入之 paddingChar 長度需為 1!");
		}

		str = StringUtil.padding(str, paddingChar, length, isLeft);

		if (str.length() > length) {
			return str.substring(0, length);
		}

		return str;

		//		// 傳入字串的指標
		//		int strCursor = 0;
		//		// 結果字串
		//		String result = "";
		//
		//		for (int tempResultLength = 0; tempResultLength < length;) {
		//
		//			// 將append到結果字串的字元
		//			byte[] appendChar = null;
		//			// 是否為原始資料(用以識別在padding字元為左方時，原始資料排列不應排在左邊)
		//			boolean isRawData = true;
		//
		//			if (strCursor < str.length()) {
		//				// 從原字串取得將append到結果字串的字元
		//				appendChar = str.substring(strCursor, strCursor + 1).getBytes(encodeing);
		//				// 原字串指標 + 1
		//				strCursor++;
		//			} else {
		//				// 以paddingChar為將append到結果字串的字元()
		//				appendChar = paddingChar.getBytes(encodeing);
		//			}
		//			// 加上appendChar長度
		//			tempResultLength += appendChar.length;
		//
		//			// 檢核加上結果字元後長度是否超過長度，是則用空白補足短缺的長度
		//			if (tempResultLength > length) {
		//				// 計算短缺的長度
		//				int shortLength = length - result.getBytes(encodeing).length;
		//				// 用空白補足短缺長度
		//				for (int i = 0; i < shortLength; i++) {
		//					if (isLeft) {
		//						result = " " + result;
		//					} else {
		//						result = result + " ";
		//					}
		//				}
		//				// 跳出
		//				return result;
		//			}
		//			// 將結果字串加上appendChar
		//			if (isLeft && !isRawData) {
		//				result = new String(appendChar, encodeing) + result;
		//			} else {
		//				result = result + new String(appendChar, encodeing);
		//			}
		//		}
		//		return result;
	}

	// =============================================================================
	// HEX 處理
	// =============================================================================
	/**
	 * byte --> HEX String
	 * @param b byte
	 * @return HEX String
	 */
	public static String toHex(byte b) {
		return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF".charAt(b & 0xf));
	}

	public static String toHexByChar(String str) {
		return toHexByChar(str, null);
	}

	/**
	 * @param str
	 * @param encode
	 * @return
	 */
	public static String toHexByChar(String str, String encode) {
		if (isEmpty(str)) {
			return "";
		}
		String result = " ";

		for (int i = 0; i < str.length(); i++) {
			String s = str.substring(i, i + 1);
			try {
				if (encode != null) {
					result += " " + StringUtil.toHex(s.getBytes(encode));
				} else {
					result += " " + StringUtil.toHex(s.getBytes());
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return result.substring(1);
	}

	/**
	 * byte[] --> HEX String
	 * @param bAry toHex
	 * @return HEX String
	 */
	public static String toHex(byte[] bAry) {
		String result = "";
		for (int i = 0; i < bAry.length; i++) {
			result += StringUtil.toHex(bAry[i]);
		}
		return result;
	}

	/**
	 * 將 byte 資料整理成 HEX 型式的文字
	 * @param dataByteAry 資料
	 * @param encodeing 編碼
	 * @return HEX String
	 */
	public String byteToHexFormat(byte[] dataByteAry, String encodeing) {

		// byte index 字串最大的長度
		int byteIndexMaxLength = String.valueOf(dataByteAry.length).length() + 1;

		// ==============================================
		// 變數區
		// ==============================================
		// 結果文字 StringBuffer
		StringBuffer sb = new StringBuffer();
		// 處理起始 index 旗標
		int processStartIndex = 0;
		// 分行後的List (16byte 為一行)
		List<byte[]> rowList = new ArrayList<byte[]>();

		// ==============================================
		// 分行(16byte 為一行)
		// ==============================================
		while (processStartIndex < dataByteAry.length) {
			// 取得此次行 byte 數
			int length = 16;
			if (dataByteAry.length - processStartIndex < 16) {
				length = dataByteAry.length - processStartIndex;
			}
			// copy 此行資料後放到 list
			byte[] rowDataByteAry = new byte[length];
			System.arraycopy(dataByteAry, processStartIndex, rowDataByteAry, 0, length);
			rowList.add(rowDataByteAry);
			// index 位置 +16 byte
			processStartIndex += 16;
		}

		// ==============================================
		// 兜組輸出字串(16byte 為一行)
		// ==============================================
		// 標題
		for (int i = 0; i < byteIndexMaxLength; i++)
			sb.append(" ");
		sb.append("                      HEX");
		sb.append("                                 ");
		sb.append(encodeing.toUpperCase());
		sb.append("\r\n");

		// 分隔線
		for (int i = 0; i < byteIndexMaxLength; i++)
			sb.append("-");
		sb.append("   ");
		sb.append("-----------------------------------------------");
		sb.append("    ");
		sb.append("----------------");
		sb.append("\r\n");

		// 內容
		int index = 0;
		for (int i = 0; i < rowList.size(); i++) {
			// 取得行 byte
			byte[] rowDataByteAry = rowList.get(i);

			// 資料 index
			try {
				sb.append(StringUtil.padding(String.valueOf(index), "0", byteIndexMaxLength, true));
			} catch (Exception e1) {
				// 不可能
			}
			index += rowDataByteAry.length;

			// 分隔符號
			sb.append(" | ");

			// HEX
			for (int j = 0; j < rowDataByteAry.length; j++) {
				sb.append(StringUtil.toHex(rowDataByteAry[j]));
				sb.append(" ");
			}

			// 最後一筆補空白
			int length = rowDataByteAry.length;
			if (length < 16) {
				for (int k = 0; k < 16 - length; k++)
					sb.append("   ");
			}

			// 分隔符號
			sb.append(" | ");

			// String
			try {
				sb.append(new String(rowDataByteAry, encodeing));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			//
			sb.append("\r\n");
		}
		return sb.toString();
	}

	// =============================================================================
	// is 判斷
	// =============================================================================
	/**
	 * 檢核字串是否為空
	 * @param object 傳入物件
	 * @return true or false
	 */
	public static boolean isEmpty(Object object) {
		if (object == null)
			return true;
		if (!"".equals(object.toString().trim()))
			return false;
		return true;
	}

	public static boolean isEmpty(String str) {
		if (str == null)
			return true;
		if (!"".equals(str.trim()))
			return false;
		return true;
	}

	/**
	 * 檢核List是否為空
	 * @param object 傳入物件
	 * @return true or false
	 */
	public static boolean isEmpty(List list) {
		if (list == null || list.size() == 0)
			return true;
		return false;
	}

	/**
	 * 檢核字串是否不為空
	 * @param str
	 * @return true or false
	 */
	public static boolean notEmpty(String str) {
		if (str == null)
			return false;
		if (!"".equals(str.trim()))
			return true;
		return false;
	}

	/**
	 * 檢核物件是否不為空
	 * @param obj
	 * @return
	 */
	public static boolean notEmpty(Object obj) {
		if (obj == null)
			return false;
		if (!"".equals(obj.toString().trim()))
			return true;
		return false;
	}

	/**
	 * 檢核List是否為不為空
	 * @param object 傳入物件
	 * @return true or false
	 */
	public static boolean notEmpty(List list) {
		return !isEmpty(list);
	}

	/**
	 * 判斷英數字
	 * @param val
	 * @return
	 */
	public static boolean isAlpha(String val) {
		if (val == null)
			return false;
		for (int i = 0; i < val.length(); i++) {
			char ch = val.charAt(i);
			if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && (ch < '0' || ch > '9'))
				return false;
		}
		return true;
	}

	/**
	 * 檢核字串是否為數字
	 * @param str
	 * @return true or false
	 */
	// public static boolean isNumeric(String str) {
	// Pattern pattern = Pattern.compile("[0-9]+");
	// return pattern.matcher(str).matches();
	// }

	/**
	 *
	 * @param val
	 * @return
	 */
	public static boolean isNumber(String val) {
		if (val == null)
			return false;
		for (int i = 0; i < val.length(); i++) {
			char ch = val.charAt(i);
			if (ch < '0' || ch > '9') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判斷是否為大寫英文字母
	 * @param val
	 * @return
	 */
	public static boolean isCapsEnglish(String val) {
		if (val == null)
			return false;
		for (int i = 0; i < val.length(); i++) {
			char ch = val.charAt(i);

			if (ch < 'A' || ch > 'Z')
				return false;
		}
		return true;
	}

	/**
	 *
	 * @param val
	 * @return
	 */
	public static boolean isNumberOrCapsEnglish(String val) {
		if (val == null)
			return false;
		for (int i = 0; i < val.length(); i++) {
			char ch = val.charAt(i);

			if ((ch < 'A' || ch > 'Z') && (ch < '0' || ch > '9'))
				return false;
		}
		return true;
	}

	/**
	 * E-mail格式檢查
	 * @param data
	 * @return
	 */
	public static boolean isEMail(String data, String ENCODING) throws UnsupportedEncodingException {
		if (data != null && !data.trim().equalsIgnoreCase("")) {
			data = data.trim();
			char[] charArray = data.toCharArray();
			for (int i = 0; i < charArray.length; i++) {
				if (String.valueOf(charArray[i]).getBytes(ENCODING).length != 1) {
					return false;// Email格式錯誤;
				}
			}
			if (data.indexOf("@") < 0) {
				return false;// Email格式錯誤;
			}
			String[] email = data.split("@");
			if (email.length != 2) {
				return false;// Email格式錯誤;
			}
			if ((email[0].indexOf(" ") != -1) || (email[1].indexOf(" ") != -1)) {
				return false;// Email格式錯誤;
			}
			if (data.indexOf(".") < 0) {
				return false;// Email格式錯誤;
			}
		} else {
			return false;
		}
		return true;
	}

	// =============================================================================
	// Exception 轉字串
	// =============================================================================
	/**
	 * 將ExceptionStackTrace轉為字串
	 * @param e Throwable
	 * @return ExceptionStackTrace
	 */
	public static String getExceptionStackTrace(Throwable e) {
		if (e == null) {
			return "e is null";
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
		e.printStackTrace(new PrintStream(byteArrayOutputStream));
		return new String(byteArrayOutputStream.toByteArray());
	}

	// =============================================================================
	// 剪貼簿
	// =============================================================================
	/**
	 * 取得剪貼簿內容
	 * @return 剪貼簿內容文字
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	public String getClipboardContent() throws UnsupportedFlavorException, IOException {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable content = clipboard.getContents(this);
		return (String) content.getTransferData(DataFlavor.stringFlavor);
	}

	// =============================================================================
	// cropping
	// =============================================================================
	/**
	 * 從srcString截取至最大maxLen(中文需一次取2 bytes)
	 * @param srcString
	 * @param maxLen
	 * @return
	 * @throws Exception
	 */
	public synchronized static String cropping(String srcString, int maxLen) throws Exception {
		if (srcString == null) {
			throw new Exception("cropping():傳入null!");
		}
		String desString = null;
		// ??String?byte?料
		byte[] desBytes = srcString.getBytes();
		// 比?desBytes之?度
		if (desBytes.length > maxLen) {
			// 呼叫cropping函式,??果assign回去
			byte[] tmpBytes = cropping(desBytes, maxLen);
			desBytes = tmpBytes;
		}
		// ??srcBytes?Sting,?成回?之desString
		desString = new String(desBytes);
		return desString;
	}

	/**
	 * 從srcByte截取至最大maxLen(中文需一次取2 bytes)
	 * @param srcBytes
	 * @param maxLen
	 * @return
	 * @throws Exception
	 */
	public synchronized static byte[] cropping(byte[] srcBytes, int maxLen) throws Exception {
		if (srcBytes == null) {
			throw new Exception("cropping():傳入null!");
		}
		byte[] desBytes = srcBytes;
		// 比?srcBytes 之?度
		if (srcBytes.length > maxLen) {
			// ?理中文字, 重新?算maxLen
			for (int i = 0; i < maxLen; i++) {
				// 若?中文?一次跳2 byte
				if (srcBytes[i] < 0) {
					i++;
				}
				if (i == maxLen) {
					maxLen = maxLen - 1;
				}
			}
			// 以maxLen??,?srcBytes中超出maxLen之部份移除
			byte[] tmpBytes = new byte[maxLen];
			System.arraycopy(srcBytes, 0, tmpBytes, 0, maxLen);
			desBytes = tmpBytes;
		}
		return desBytes;
	}

	// =============================================================================
	// param
	// =============================================================================
	public static String replace2Param(String str, Map<String, Object> rowDataMap) {

		StringBuffer result = new StringBuffer();

		boolean paramPrefix = false;
		String paramKey = "";
		for (int i = 0; i < str.length(); i++) {
			String chr = str.charAt(i) + "";
			// 傳入字元為 #
			if ("#".equals(chr)) {
				if (!paramPrefix) {
					paramPrefix = true;
				} else {
					result.append(rowDataMap.get(paramKey));
					paramKey = "";
					paramPrefix = false;
				}
				continue;
			}
			// 已遇到開始的 # 字號
			if (paramPrefix) {
				paramKey += chr;
				continue;
			}
			result.append(chr);
		}

		return result.toString();

	}

	// =============================================================================
	//
	// =============================================================================
	// =============================================================================
	// 測試區
	// =============================================================================
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		StringUtil a = new StringUtil();
		String encodeing = "cp937";
		String str = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";

		try {
			System.out.println(a.byteToHexFormat(str.getBytes(encodeing), encodeing));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main1(String[] args) {

		String encodeing = "big5";
		String str = "宏x碁";
		try {
			str = StringUtil.paddingAndSplit(str, "卡", 13, true, encodeing);
			System.out.println("『" + str + "』:" + str.getBytes(encodeing).length);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
