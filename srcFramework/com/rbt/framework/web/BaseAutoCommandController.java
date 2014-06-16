package com.rbt.framework.web;

import com.rbt.framework.model.BaseCommand;

/**
 * <tt>BaseAutoCommandController</tt>類為抽象類AbstractMethodWizardFormController類的實現；<br>
 * 作爲自定義Controller類的基類。
 * @see com.rbt.framework.web.AbstractMethodWizardFormController
 */
public class BaseAutoCommandController extends AbstractMethodWizardFormController{

	/**
	 * 功能：初始化command對象。
	 * @param Command command
	 */
	public void init(BaseCommand command){
		initCommand(command);
	}
}
