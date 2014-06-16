package com.rbt.util.exceloperate.bean.expt.config;

import org.dom4j.Node;

import com.rbt.util.StringUtil;
import com.rbt.util.exceloperate.Constant;

/**
 * 和 資料欄位 有關的屬性設定資訊
 * @author Allen
 */
public abstract class AbstractExportColumnArrtInfo extends AbstractStyleInfo{

	// =====================================================
	// 元素屬性
	// =====================================================
	/**
	 * key 從 map 的key
	 */
	private String key;
	/**
	 * 要顯示的值 (若key 參數有設定時，優先使用 key)
	 */
	private String defaultValue;
	/**
	 * 要處理顯示值的 function id
	 */
	private String funcId;
	/**
	 * 功能帶入參數
	 */
	private String funcParam;
	/**
	 * 合併列數
	 */
	private int colspan;

	// =====================================================
	// 公用程式
	// =====================================================
	/**
	 * 讀取 Node 中,與 Data Column 類型元素 相關的屬性
	 * @param node
	 */
	public void readDataColumnAttr(Node node){
		if(node==null){
			return;
		}
		//key
		this.setKey(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_KEY)));
		//funcId
		this.setFuncId(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_FUNCID)));
		//FuncParam
		this.setFuncParam(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_FUNC_PARAM)));
		//colspan
		this.setColspan(Integer.valueOf(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_COLSPAN), "0")));

		// defaultValue (先讀取 node, node 無值時,  讀取 attr)
		Node defaultValueNode = node.selectSingleNode(Constant.ELEMENT_DEFAULT_VALUE);
		String defaultValue = "";
		if (defaultValueNode != null) {
			defaultValue = defaultValueNode.getText();
		}
		if (StringUtil.isEmpty(defaultValue)) {
			defaultValue = StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_DEFAULT_VALUE));
		}
		this.setDefaultValue(defaultValue);
	}

	// =====================================================
	// gatter & setter
	// =====================================================
	/**
	 * @return the key
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the colspan
	 */
	public int getColspan() {
		return this.colspan;
	}

	/**
	 * @param colspan the colspan to set
	 */
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	/**
	 * @return the funcId
	 */
	public String getFuncId() {
		return this.funcId;
	}

	/**
	 * @param funcId the funcId to set
	 */
	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

	/**
	 * @return the funcParam
	 */
	public String getFuncParam() {
		return this.funcParam;
	}

	/**
	 * @param funcParam the funcParam to set
	 */
	public void setFuncParam(String funcParam) {
		this.funcParam = funcParam;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
