package com.rbt.util.exceloperate.bean.impt.config;

import org.dom4j.Node;

import com.rbt.util.StringUtil;
import com.rbt.util.exceloperate.Constant;

/**
 * import 共通屬性設定資訊
 * @author Allen
 */
public abstract class AbstractImportCommonAttrInfo {

	// =====================================================
	// 元素屬性
	// =====================================================
	/**
	 * key 回傳map 物件的 map key
	 */
	private String key;
	/**
	 * 預設值
	 */
	private String defaultValue;
	/**
	 * 欄位說明，非必要，錯誤時顯示名稱，及備註用
	 */
	private String desc;
	/**
	 * 要處理輸入值的 function id
	 */
	private String funcId;
	/**
	 * 功能帶入參數
	 */
	private String funcParam;
	/**
	 * 參數排列順序
	 */
	private String index;

	// =====================================================
	// 公用程式
	// =====================================================
	/**
	 * 讀取 Node 中,共通屬性
	 * @param node
	 */
	public void readCommonAttr(Node node) {
		if (node == null) {
			return;
		}
		// key
		this.setKey(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_KEY)));
		// desc
		this.setDesc(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_DESC)));
		// funcId
		this.setFuncId(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_FUNCID)));
		// index
		this.setIndex(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_INDEX)));

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


		// funcParam (先讀取 node, node 無值時,  讀取 attr)
		Node funcParamNode = node.selectSingleNode(Constant.ELEMENT_FUNC_PARAM);
		String funcParam = "";
		if (funcParamNode != null) {
			funcParam = funcParamNode.getText();
		}
		if (StringUtil.isEmpty(funcParam)) {
			funcParam = StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_FUNC_PARAM));
		}
		this.setFuncParam(funcParam);
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

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return this.desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
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
	 * @return the index
	 */
	public String getIndex() {
		return this.index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(String index) {
		this.index = index;
	}
}
