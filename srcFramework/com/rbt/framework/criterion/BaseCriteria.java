package com.rbt.framework.criterion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.rbt.framework.criterion.BaseCriteria;
import com.rbt.framework.criterion.Group;
import com.rbt.framework.criterion.HqlRestrictions;
import com.rbt.framework.criterion.LogicalExpression;
import com.rbt.framework.criterion.NormalExpression;
import com.rbt.framework.criterion.OrderBy;
import com.rbt.framework.criterion.ReturnEntry;


/**
 * <tt>BaseCriteria</tt> 是一個用於以對象形式構建hql簡單查詢的API.
 * 能方便的增加查詢過濾條件、排序、分組及結果返回類型等。並且諸如此類的操作都是語義化的。
 * 最常用的是通過<tt>HqlRestrictions</tt>提供的各種語義化的methods構建它的子類<tt>HqlDetachedCriteria</tt>。
 * <br>
 * <pre>
 *  eg.
 *  	HqlDetachedCriteria criteria = HqlDetachedCriteria.forEntityNames( new String[] { "Cat c", "Dog d" });
 * 		criteria.add(HqlRestrictions.eq("c.weight", "{d.weight}"))
 *      criteria.add(HqlRestrictions.gt( "c.age", new Integer(2) ))
 *      criteria.addOrder(OrderBy.asc("c.id"))
 *      criteria.setReturnEntry(ReturnEntry.forName("new Map(c.name,d.name)"));
 *      BaseDAO baseDAO = new BaseDAOImpl();
 *		List result = baseDAO.query(criteria);
 * </pre>
 * @see com.rbt.framework.criterion.BaseExpression
 * @see com.rbt.framework.criterion.OrderBy
 * @see com.rbt.framework.criterion.ReturnEntry
 * @see com.rbt.framework.criterion.Group
 * @see com.rbt.framework.criterion.HqlRestrictions
 * @see com.rbt.framework.dao.BaseDAO
 * @see com.rbt.framework.dao.impl.BaseDAOImpl
 * @author Royal
 *
 */

public abstract class BaseCriteria implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -4442257922538994778L;
	public final String DEFAULT_RETURN_ENTRIES="";
	public final String DEFAULT_ORDER_BY_CONDITION="";
	public final String DEFAULT_GROUP_BY_CONDITION="";
	public final String ORDER_BY =" order by ";
	public final String GROUP_BY =" group by ";
	public final String SELECT="select ";
	public final String WHERE=" where ";
	public final String AND = " and ";
	public final String FROM=" from ";
	public final String COUNT="select count(*) ";
	public final  static String COMMA=",";

	private List criterionEntries = new ArrayList();
	private String[] criterionString;
	private Object[] values;
	protected  String[] entityNames;

	private String hql;
	private String pageHql;
	private String order = this.DEFAULT_ORDER_BY_CONDITION;
	private String groupBy = this.DEFAULT_GROUP_BY_CONDITION;
	private String returnEntries =this.DEFAULT_RETURN_ENTRIES;
	private String queryEntry =this.FROM;
	private boolean alreadyBuild = false;


	/**
	 * 增加查詢過濾條件
	 * eg: 1.add(HqlDetachedCriteria.ge("a.name","cat")), the hql semantics is: where a.name>="cat"
	 *     2.from A a, B b where a.name=b.name ,the hql semantics is add(HqlDetachedCriteria.eq("a.name","{b.name}"))
	 * @param expression
	 * @return BaseCriteria
	 * @see HqlRestrictions
	 */
	public BaseCriteria add(BaseExpression expression) {
		this.criterionEntries.add(expression);
		return this;
	}

	/**
	 * 設定查詢結果排序
	 * eg: addOrder(OrderBy.asc("a.id")),the hql semantics is: order by a.id asc
	 * @param orderBy
	 * @return BaseCriteria
	 */
	public BaseCriteria addOrder(OrderBy orderBy){
		if(isNullOrder())
			setOrder();
		this.order =this.order.concat(orderBy.toString()).concat(COMMA);
		return this;
	}

	/**
	 * 設定查詢結果返回方式(可以是Bean,Map etc..等hql所支持之返回結果)
	 * eg: setReturnEntry(ReturnEntry.forName("a.name")),the hql semantics is: select a.name from ...
	 * @param returnEntry
	 * @return BaseCriteria
	 */
	public BaseCriteria setReturnEntry(ReturnEntry returnEntry){
		if(isNullReturnEntries())
			setReturnEntries();
		this.returnEntries = this.returnEntries.concat(returnEntry.toString()).concat(COMMA);
		return this;
	}


	/**
	 * 設定查詢結果分組
	 * eg: groupBy(Group.forName("a.name")),the hql semantics is: ．．.group by a.name
	 * @param group
	 * @return BaseCriteria
	 */
	public BaseCriteria groupBy(Group group){
		if(isNullGroup())
			setGroup();
		this.groupBy = this.groupBy.concat(group.toString()).concat(COMMA);
		return this;
	}

	private void buildHql(){
		this.criterionString = new String[this.criterionEntries.size()];
		List tempValues =new ArrayList();
		for(int i= 0 ; i < this.criterionEntries.size();i++){
			BaseExpression expression = (BaseExpression)this.criterionEntries.get(i);
			if (expression instanceof NormalExpression) {
				NormalExpression normalExpression = (NormalExpression) expression;
				this.criterionString[i] = normalExpression.toHql();
				if(normalExpression.getRecordValue()!=null){
					tempValues.add(normalExpression.getRecordValue());
				}
			}
			if (expression instanceof LogicalExpression) {
				LogicalExpression logicalExpression = (LogicalExpression)expression;
				this.criterionString[i] = logicalExpression.toHql();
				tempValues.add(logicalExpression.getRecordValue());
			}
		}
		this.values = getTypedValue(tempValues);
		parseHql();
		System.out.println("HQL is [ "+this.hql +" ]");
	}

	/**
	 *
	 * @return
	 */
	private String composeCriterion(){
		StringBuffer conditions = new StringBuffer();
		for(int i= 0 ; i <this.criterionString.length-1 ;i++){
			conditions.append(this.criterionString[i]).append(this.AND);
		}
		return this.WHERE.concat(conditions.append(this.criterionString[this.criterionString.length-1]).toString());
	}

	/**
	 *
	 * @param composeValue
	 * @return
	 */
	private Object[] getTypedValue(List composeValue){
		List objects = new ArrayList();
		for(int v= 0; v<composeValue.size() ;v++){
			if (composeValue.get(v) instanceof Object[]) {
				Object[] doubleValue = (Object[]) composeValue.get(v);
				if(doubleValue[0]!=null){
					objects.add(doubleValue[0]);
				}
				if(doubleValue[1]!=null){
					objects.add(doubleValue[1]);
				}
			}else{
				objects.add(composeValue.get(v));
			}
		}
		Object[] results = new Object[objects.size()];
		objects.toArray(results);
		return results;

	}


	/**
	 * set return Entries
	 *
	 */
	private void formatReturnEntries(){
		this.returnEntries=FormatHelper.format(this.returnEntries);
	}

	private void formatOrder(){
		this.order = FormatHelper.format(this.order);
	}

	private void formatGroup(){
		this.groupBy =FormatHelper.format(this.groupBy);
	}

	/**
	 *  set query Entries
	 *
	 */
	private void composeQueryEntries(){
		for(int i=0; i< this.entityNames.length;i++){
			this.queryEntry = this.queryEntry.concat(this.entityNames[i]).concat(COMMA);
		}
		this.queryEntry =FormatHelper.format(this.queryEntry);
	}

	private void parseHql(){
		this.formatReturnEntries();
		this.formatOrder();
		this.formatGroup();
		this.composeQueryEntries();
		this.pageHql=new StringBuffer().append(this.COUNT)
									.append(this.queryEntry)
									.append(composeCriterion())
									.append(this.groupBy).toString();
		this.hql=new StringBuffer().append(this.returnEntries)//select a,b
								.append(this.queryEntry)//from A a ,B b
								.append(composeCriterion())//where
								.append(this.groupBy) //group by
								.append(this.order).toString();//order by
		this.alreadyBuild = true;

	}

	public String getHql() {
		if(!this.alreadyBuild)
			this.buildHql();
		return this.hql;
	}

	public String getPageHql(){
		if(!this.alreadyBuild)
			this.buildHql();
		return this.pageHql;
	}

	public Object[] getValues() {
		return this.values;
	}

	private boolean isNullOrder(){
		return FormatHelper.isBlank(this.order);
	}

	private void setOrder(){
		this.order=this.ORDER_BY;
	}

	private boolean isNullReturnEntries(){
		return FormatHelper.isBlank(this.returnEntries);
	}

	private void setReturnEntries(){
		this.returnEntries=this.SELECT;
	}

	private boolean isNullGroup(){
		return FormatHelper.isBlank(this.groupBy);
	}

	private void setGroup(){
		this.groupBy=this.GROUP_BY;
	}

	//
	public static class FormatHelper  {

		public static boolean isBlank(String str){
			return str==null||str.trim().equals("");
		}

		public static String format(String str){
			if(!isBlank(str)){
				return str.substring(0,str.lastIndexOf(COMMA));
			}
			return "";
		}
	}
}
