package com.rbt.framework.criterion;

/**
 * 此<tt>LogicalExpression</tt>類將稍複雜(通常帶邏輯性的)的面向對象的查詢條件轉化為相應的hql string
 * 該過程通過調用<tt>toHql()</tt>方法來實現
 * @author Royal
 * @see com.rbt.framework.criterion.BaseExpression
 */
public class LogicalExpression implements BaseExpression{
	private final NormalExpression lhs;
	private final NormalExpression rhs;
	private final String op;
	private static final String IDENTICAL = "1=1 ";
	protected LogicalExpression(NormalExpression lhs, NormalExpression rhs, String op) {
		if(lhs==null) throw new IllegalArgumentException("the first Expression cant be Null");
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
	}

	/**
	 * toHql
	 * @return String
	 */
	public String toHql(){
		if(this.rhs!=null)
		return new StringBuffer().append("(")
								.append(this.lhs.toHql().concat(this.op).concat(this.rhs.toHql()))
								.append(") ").toString();
		return new StringBuffer().append(IDENTICAL)
								.append(this.op.concat(this.lhs.toHql())).toString();

	}

	public Object[] getRecordValue(){
		Object[] values = new Object[2];
		values[0] = this.lhs.getRecordValue();
		if(this.rhs!=null){
			values[1] = this.rhs.getRecordValue();
		}

		return values;
	}
}
