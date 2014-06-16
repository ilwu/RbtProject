/*
 * @(#)Constants.java
 *
 * Copyright (c) 2007 HiTRUST Incorporated.All rights reserved.
 *
 * Description : 系統常量定義
 *
 * Modify History:
 *  v1.00, 2007/04/26, Jmiu Han
 *   1) First release
 */
package com.rbt.framework.util;

public interface Constants {
	String MESSAGE_REDIRECT="messageRedirect";
	String LOGIN_USER = "loginUser";
	String EXCEPTIONS = "exceptions";

	//Application properitiy file name
	String PROPERTY_FILE = "lib";

	//全局 URL 定義
	String ERROR_BACK_URL = "/index.html";
	String EXCEPTION_BACK_URL ="/main.html";
	String MSG_URL = "/message/msg";

	//登陸密碼變更url
	String CHANGE_PSWD_URL = "/changePassword";

	String POP_ERR_URL = "/message/popupError";
}
