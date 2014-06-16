/**
 * @(#)OrderBy.java
 *
 * Copyright (c) 2007 HiTRUST Incorporated.
 * All rights reserved.
 *
 * Modify History:
 *  v1.00, 2007-4-28, Royal Shen
 *   1) First release
 */
package com.rbt.framework.criterion;

import com.rbt.framework.criterion.OrderBy;


/**
 * 此<tt>OrderBy</tt>類為<literal>HqlDetachedCriteria</literal>提供對象形式的查詢結果排序條件
 * @author Royal
 * @see com.rbt.framework.criterion.HqlDetachedCriteria
 */
public class OrderBy {
	private boolean ascending;
	private String propertyName;


	private OrderBy(String propertyName, boolean ascending) {
		this.propertyName = propertyName;
		this.ascending = ascending;
	}

	@Override
	public String toString() {
		return this.propertyName + ' ' + (this.ascending?"asc":"desc");
	}

	/**
	 * 升序
	 * @param propertyName
	 * @return OrderBy
	 */
	public static OrderBy asc(String propertyName) {
		return new OrderBy(propertyName, true);
	}

	/**
	 * 降序
	 * @param propertyName
	 * @return OrderBy
	 */
	public static OrderBy desc(String propertyName) {
		return new OrderBy(propertyName, false);
	}
}
