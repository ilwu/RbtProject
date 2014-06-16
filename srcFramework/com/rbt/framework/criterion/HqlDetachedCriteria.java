/**
 * @(#)HqlDetachedCriteria.java
 *
 * Copyright (c) 2007 HiTRUST Incorporated.
 * All rights reserved.
 *
 * Modify History:
 *  v1.00, 2007-4-28, Royal Shen
 *   1) First release
 */
package com.rbt.framework.criterion;

import com.rbt.framework.criterion.BaseCriteria;
import com.rbt.framework.criterion.HqlDetachedCriteria;

/**
 * 此<tt>HqlDetachedCriteria</tt>類作爲hql查詢語句的對象封裝可以在任何地方實例化,
 * 最後以參數形式傳入<literal>BaseDAO</literal>做查詢動作。
 * 該類的所有方法與其父類<literal>BaseCriteria</literal>的相應方法有相同的語義
 * @see com.rbt.framework.criterion.BaseCriteria
 * @see com.rbt.framework.dao.BaseDAO
 * @author Royal
 *
 */
public class HqlDetachedCriteria extends BaseCriteria{

	private static final long serialVersionUID = 8923386108672022481L;

	private HqlDetachedCriteria(String[] entityNames){
		this.entityNames = entityNames;
	}

	public static HqlDetachedCriteria forEntityNames(String[] entityNames) {
		return new HqlDetachedCriteria(entityNames);
	}

}
