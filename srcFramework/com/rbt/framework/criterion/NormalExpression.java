/**
 * @(#)NormalExpression.java
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
 * 此<tt>NormalExpression</tt>類將簡單的面向對象的查詢條件轉化為相應的hql string
 * 該過程通過調用<tt>toHql()</tt>方法來實現
 * @author Royal Shen
 * @see com.rbt.framework.criterion.BaseExpression
 */
public class NormalExpression implements BaseExpression{

	public final String SPACE=" ";
	private final String propertyName;
	private final Object value;
	//private boolean ignoreCase;
	private final String op;
	private Object recordValue;

	protected NormalExpression(String propertyName, Object value, String op) {
		this.propertyName = propertyName;
		this.value = value;
		this.op = op;
	}

	/*
	public Expression ignoreCase() {
		ignoreCase = true;
		return this;
	}
	*/

	@Override
	public String toString() {
		return this.propertyName + getOp() + this.value;
	}

	public String toHql(){
		String symbol = "?";
		this.recordValue = this.value;
		if (this.value instanceof String) {
			symbol = (String) this.value;
			if(symbol.indexOf("{")<0){
				symbol = "?";
			}else{
				symbol = symbol.substring(1,symbol.length()-1);
				this.recordValue =null;
			}
		}
		return new StringBuffer(this.SPACE).append(this.propertyName.concat(this.op).concat(symbol))
									.append(this.SPACE).toString();
	}

	protected final String getOp() {
		return this.op;
	}

	public  Object getRecordValue() {
		return this.recordValue;
	}
}
