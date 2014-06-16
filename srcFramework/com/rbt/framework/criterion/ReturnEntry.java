/**
 * @(#)ReturnEntry.java
 *
 * Copyright (c) 2007 HiTRUST Incorporated.
 * All rights reserved.
 *
 * Modify History:
 *  v1.00, 2007-4-28, Royal Shen
 *   1) First release
 */
package com.rbt.framework.criterion;

import com.rbt.framework.criterion.ReturnEntry;

/**
 * 此<tt>ReturnEntry</tt>類為<literal>HqlDetachedCriteria</literal>提供面向對象的查詢結果返回類型
 * @author Royal Shen
 * @see com.rbt.framework.criterion.HqlDetachedCriteria
 */
public class ReturnEntry {

	private String returnEntry;

	private ReturnEntry(String entryName){
		this.returnEntry = entryName;
	}

	/**
	 * 返回查詢結果返回類型之對象
	 * @param entryName
	 * @return ReturnEntry
	 */
	public static ReturnEntry forName(String entryName){
		return new ReturnEntry(entryName);
	}

	@Override
	public String toString() {
		return this.returnEntry;
	}

}
