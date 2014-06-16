/**
 * @(#)HqlRestrictions.java
 *
 * Copyright (c) 2007 HiTRUST Incorporated.
 * All rights reserved.
 *
 * Modify History:
 *  v1.00, 2007-4-28, Royal Shen
 *   1) First release
 */
package com.rbt.framework.criterion;

import com.rbt.framework.criterion.LogicalExpression;
import com.rbt.framework.criterion.NormalExpression;


/**
 * 此 <tt>HqlRestrictions</tt> 類可以為<tt>HqlDetachedCriteria</tt>創建各種内置的簡單的查詢條件
 * @author Royal
 * @see com.rbt.framework.criterion.HqlDetachedCriteria
 */
public class HqlRestrictions {

	/**
	 * 為指定的peoperty提供一個"相等"的條件對象,
	 * 並且該"相等"可以是等於一個具體的值,也可以是另一個po的peoperty(檔value以"{"開頭並以 "}"結尾的時候)
	 * @param propertyName
	 * @param value
	 * @return NormalExpression
	 */
	public static NormalExpression eq(String propertyName, Object value) {
		return new NormalExpression(propertyName, value, "=");
	}

	/**
	 * 為指定的peoperty提供一個"不相等"的條件對象,
	 * 並且該"不相等"可以是不等於一個具體的值,也可以是另一個po的peoperty(檔value以 "{" 開頭並以 "}"結尾的時候)
	 * @param propertyName
	 * @param value
	 * @return NormalExpression
	 */
	public static NormalExpression ne(String propertyName, Object value) {
		return new NormalExpression(propertyName, value, "<>");
	}

	/**
	 * 為指定的peoperty提供一個"like"的條件對象,
	 * 並且該"like"可以是不等於一個具體的值,也可以是另一個po的peoperty(檔value以 "{" 開頭並以 "}" 結尾的時候)
	 * @param propertyName
	 * @param value
	 * @return NormalExpression
	 */
	public static NormalExpression like(String propertyName, Object value) {
		return new NormalExpression(propertyName, value, " like ");
	}

	/**
	 * 為指定的peoperty提供一個"大於"的條件對象,
	 * 並且該"大於"可以是大於一個具體的值,也可以是另一個po的peoperty(檔value以 "{" 開頭並以 "}" 結尾的時候)
	 * @param propertyName
	 * @param value
	 * @return NormalExpression
	 */
	public static NormalExpression gt(String propertyName, Object value) {
		return new NormalExpression(propertyName, value, ">");
	}

	/**
	 * 為指定的peoperty提供一個"小於"的條件對象,
	 * 並且該"小於"可以是小於一個具體的值,也可以是另一個po的peoperty(檔value以 "{" 開頭並以 "}" 結尾的時候)
	 * @param propertyName
	 * @param value
	 * @return NormalExpression
	 */
	public static NormalExpression lt(String propertyName, Object value) {
		return new NormalExpression(propertyName, value, "<");
	}

	/**
	 * 為指定的peoperty提供一個"小於等於"的條件對象,
	 * 並且該"小於等於"可以是小於等於一個具體的值,也可以是另一個po的peoperty(檔value以 "{" 開頭並以 "}" 結尾的時候)
	 * @param propertyName
	 * @param value
	 * @return NormalExpression
	 */
	public static NormalExpression le(String propertyName, Object value) {
		return new NormalExpression(propertyName, value, "<=");
	}

	/**
	/**
	 * 為指定的peoperty提供一個"大於等於"的條件對象,
	 * 並且該"大於等於"可以是大於等於一個具體的值,也可以是另一個po的peoperty(檔value以 "{" 開頭並以 "}" 結尾的時候)
	 * @param propertyName
	 * @param value
	 * @return NormalExpression
	 */
	public static NormalExpression ge(String propertyName, Object value) {
		return new NormalExpression(propertyName, value, ">=");
	}
	/**
	 * 返回兩個條件的並集,相當於hql語言使用關鍵詞"or"
	 * @param lhs
	 * @param rhs
	 * @return LogicalExpression
	 */
	public static LogicalExpression or(NormalExpression lhs ,NormalExpression rhs){
		return new LogicalExpression(lhs, rhs, "or");
	}

	/**
	 * 返回先前條件與當前制定條件的並集,相當於hql語言使用關鍵詞"or"
	 * @param lhs
	 * @return LogicalExpression
	 */
	public static LogicalExpression or(NormalExpression lhs){
		return new LogicalExpression(lhs,null, "or");
	}
}
