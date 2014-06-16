package com.rbt.util.formatter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import com.rbt.util.StringUtil;
import com.rbt.util.file.FileUtil;

public class MapStringFormatter {

	public String format(String str) {

		//=========================================
		// 初始化參數
		//=========================================
		//StringBuffer 增強版 工具
		FileUtil fu = new FileUtil();
		//結尾關鍵字堆疊
		Stack<String> surfixStack = new Stack<String>();
		//縮排深度
		int depth = 0;

		//標籤關鍵字
		HashMap<String, String> keyMap = new HashMap<String, String>();
		keyMap.put("(", ")");
		keyMap.put("{", "}");
		keyMap.put("<", ">");
		keyMap.put("[", "]");

		//斷行關鍵字
		HashSet<String> keySet = new HashSet<String>();
		keySet.add(",");


		//=========================================
		// format
		//=========================================
		String surfix = "";

		for (int i = 0; i < str.length(); i++) {
			String ss = str.charAt(i) + "";

			if (keyMap.containsKey(ss)) {
				if (StringUtil.notEmpty(surfix)) {
					surfixStack.push(surfix);
				}
				surfix = keyMap.get(ss);
				fu.addLine(ss);
				depth++;
				fu.addStr(fu.addIndent("", depth, false));

			} else if (ss.equals(surfix)) {
				fu.addLine();
				fu.addStr(fu.addIndent(ss, depth - 1, false));
				fu.addLine();
				if (!surfixStack.isEmpty()) {
					surfix = surfixStack.pop();
				} else {
					surfix = "";
				}
				depth--;
			} else if (keySet.contains(ss)) {
				fu.addLine(ss);
				fu.addStr(fu.addIndent("", depth, false));
			} else {
				fu.addStr(ss);
			}

		}
		return fu.getContent();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//
	}

}
