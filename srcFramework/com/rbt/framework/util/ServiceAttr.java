/*
 * @(#)ServiceAttr.java
 *
 * Copyright (c) 2007 HiTRUST Incorporated. All rights reserved.
 * 
 * Description : 系統service.xml配置文件 結點 屬性定義
 * 
 * Modify History:
 *  v1.00, 2007/04/26, Jmiu Han
 *   1) First release
 */
package com.rbt.framework.util;

public interface ServiceAttr {
	String SERVICE="service";
	String STEP="step";
	String NAME="name";
	String PROCESS="class";
	String COMMAND_NAME="commandName";
	String URL="url";
	String METHOD="method";
	String FUNCTION="function";
	String RESULT="result";
	String ERROR="error";
	String BACK_URL="backUrl";
	String INIT="init";
	String METHOD_FORWARD="forward";
	String INIT_VALUE="1";
	String VALIDATOR="validator";
}
