/**
 * @(#)Group.java
 *
 * Copyright (c) 2007 HiTRUST Incorporated.
 * All rights reserved.
 *
 * Modify History:
 *  v1.00, 2007-4-28, Royal Shen
 *   1) First release
 */
package com.rbt.framework.criterion;

/**
 * 此<tt>Group</tt>類為<literal>HqlDetachedCriteria</literal>提供對象形式的查詢結果分組條件
 * @author Royal Shen
 * @see com.rbt.framework.criterion.HqlDetachedCriteria
 */
public class Group {
	private String propertyName;


	private Group(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public String toString() {
		return this.propertyName ;
	}

	/**
	 * 返回查詢結果分組條件之對象
	 * @param propertyName
	 * @return Group
	 */
	public static Group forName(String propertyName) {
		return new Group(propertyName);
	}

}
