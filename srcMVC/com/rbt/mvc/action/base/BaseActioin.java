package com.rbt.mvc.action.base;


/**
 * BaseActioin
 * @author Allen
 */
public abstract class BaseActioin {

	/**
	 * constructor
	 */
	public BaseActioin() {
		//Auto-generated constructor stub
	}

	/**
	 * @param request
	 * @param response
	 * @param bean
	 * @param actionName
	 * @return
	 */
	abstract public void action(BaseActionBean bean, String actionName)throws Exception;
}
