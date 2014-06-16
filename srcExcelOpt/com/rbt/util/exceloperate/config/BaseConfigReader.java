/**
 *
 */
package com.rbt.util.exceloperate.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.rbt.util.StringUtil;
import com.rbt.util.exceloperate.Constant;
import com.rbt.util.exceloperate.bean.common.FunctionInfo;
import com.rbt.util.exceloperate.exception.ExcelOperateException;

/**
 * @author Allen
 */
public class BaseConfigReader {


	/**
	 * 讀取設定檔案
	 * @param configFilePath
	 * @return
	 * @throws DocumentException
	 */
	protected Document readConfigFile(String configFilePath) throws DocumentException {

		// =========================================================
		// 讀取設定檔案
		// =========================================================
		File configFile = new File(configFilePath);
		if (!configFile.isFile()) {
			throw new ExcelOperateException("檔案:[" + configFilePath + "] 不存在!");
		}

		// =========================================================
		// 讀取 Document
		// =========================================================
		return new SAXReader().read(configFile);
	}

	/**
	 * 讀取 function 標籤設定
	 * @param document
	 * @return
	 */
	protected LinkedHashMap<String, FunctionInfo> readFunctionInfo(Document document) {

		// =========================================================
		// 讀取 function 設定
		// =========================================================
		List<Node> functionNodeList = document.selectNodes("//" + Constant.ELEMENT_FUNCTION);

		// =========================================================
		// 解析 NODE 設定
		// =========================================================
		LinkedHashMap<String, FunctionInfo> functionInfoMap = new LinkedHashMap<String, FunctionInfo>();
		for (Node funcNode : functionNodeList) {
			FunctionInfo functionInfo = new FunctionInfo();
			functionInfo.setFuncId(StringUtil.safeTrim(funcNode.valueOf(Constant.ATTRIBUTE_FUNCID)));
			functionInfo.setClassName(StringUtil.safeTrim(funcNode.valueOf(Constant.ATTRIBUTE_CLASSNAME)));
			functionInfo.setMethod(StringUtil.safeTrim(funcNode.valueOf(Constant.ATTRIBUTE_METHOD)));
			functionInfoMap.put(functionInfo.getFuncId(), functionInfo);
		}

		return functionInfoMap;
	}

}
