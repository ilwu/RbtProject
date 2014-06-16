/*
 * @(#)APLogin.java
 *
 * Copyright (c) 2007 HiTRUST Incorporated. All rights reserved.
 *
 * Description : 系統login物件
 *
 * Modify History:
 *  v1.00, 2007/04/26, Jmiu Han
 *   1) First release
 */
package com.rbt.framework.model;

import java.io.Serializable;


/**
 * 登陸login物件。存在login相關信息
 * @author jmiuhan
 */
public  class APLogin implements Serializable {
	private static final long serialVersionUID = -4987677299691600624L;

	private static final ThreadLocal currentUser = new ThreadLocal();

	/**
	 * 功能：取得當前login物件
	 * @return Command login物件
	 */
	public static Command get() {
		return (Command) currentUser.get();
	}

	/**
	 * 功能：將當前login物件放入當前ThreadLocal中
	 * @param Command user login物件
	 * @see java.lang.ThreadLocal
	 */
	public static void set(Command user) {
		currentUser.set(user);
	}
}
