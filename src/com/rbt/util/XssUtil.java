package com.rbt.util;

public class XssUtil {


	public static String XssEncode(String s) {
		if (StringUtil.isEmpty(s)) {
			return s;
		}
		StringBuffer stringbuffer = new StringBuffer();
		String tstr = s.trim();
		int j = tstr.length();

		for (int i = 0; i < j; i++) {
			char c = tstr.charAt(i);
			switch (c) {
			case 60:
				stringbuffer.append("&lt;"); // <
				break;
			case 62:
				stringbuffer.append("&gt;"); // >
				break;
			case 38:
				stringbuffer.append("&amp;"); // &
				break;
			case 34:
				stringbuffer.append("&quot;"); // "
				break;
			case 39:
				stringbuffer.append("&#39;"); // '
				break;
			default:
				stringbuffer.append(c);
				break;
			}
		}
		return stringbuffer.toString();
	}

	public static String XssDecode(String s) {
		if (StringUtil.isEmpty(s)) {
			return s;
		}
		s = s.replaceAll("&lt;", "<");
		s = s.replaceAll("&gt;", ">");
		s = s.replaceAll("&amp;", "&");
		s = s.replaceAll("&quot;", "\"");
		s = s.replaceAll("&#39;", "'");
		return s;
	}

}
