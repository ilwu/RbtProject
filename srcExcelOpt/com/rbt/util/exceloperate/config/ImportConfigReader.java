package com.rbt.util.exceloperate.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import com.rbt.util.BeanUtil;
import com.rbt.util.StringUtil;
import com.rbt.util.exceloperate.Constant;
import com.rbt.util.exceloperate.bean.impt.config.ColumnInfo;
import com.rbt.util.exceloperate.bean.impt.config.ImportConfigInfo;
import com.rbt.util.exceloperate.bean.impt.config.ParamInfo;
import com.rbt.util.exceloperate.exception.ExcelOperateException;

/**
 * 讀取設定資訊
 * @author Allen
 */
public class ImportConfigReader extends BaseConfigReader{

	/**
	 * 依據ID讀取設定檔案
	 * @param configFilePath 設定檔案完整路徑
	 * @param id 設定資訊 ID
	 * @return
	 * @throws ExcelOperateException
	 * @throws DocumentException
	 */
	public ImportConfigInfo read(String configFilePath, String id) throws ExcelOperateException, DocumentException {

		// =========================================================
		// 讀取設定檔案
		// =========================================================
		Document document = this.readConfigFile(configFilePath);

		// =========================================================
		// 讀取 ImportConfigInfo
		// =========================================================
		ImportConfigInfo importConfigInfo = this.readExcelInfo(document, id);

		// =========================================================
		// 讀取 FormatInfo
		// =========================================================
		importConfigInfo.setFunctionInfoMap(this.readFunctionInfo(document));

		return importConfigInfo;
	}




	/**
	 * ExportConfigInfo
	 * @param document
	 * @param id
	 * @return
	 * @throws ExcelOperateException
	 */
	private ImportConfigInfo readExcelInfo(Document document, String id) throws ExcelOperateException {

		// =========================================================
		// 讀取設定資訊
		// =========================================================
		Node excelNode = document.selectSingleNode("//" + Constant.ELEMENT_EXCEL + "[" + Constant.ATTRIBUTE_ID + "=\"" + id + "\"]");
		if (excelNode == null) {
			throw new ExcelOperateException("設定資訊:[" + id + "] 不存在!");
		}

		// =========================================================
		// 讀取 excel 標籤屬性
		// =========================================================
		ImportConfigInfo importConfigInfo = new ImportConfigInfo();

		//id
		importConfigInfo.setDesc(StringUtil.safeTrim(excelNode.valueOf(Constant.ATTRIBUTE_ID)));

		//sheetNum
		String sheetNum = StringUtil.safeTrim(excelNode.valueOf(Constant.ATTRIBUTE_SHEETNUM),"1");
		if(!StringUtil.isNumber(sheetNum)){
			throw new ExcelOperateException("屬性 sheetNum 設定錯誤! sheetNum:[" + sheetNum + "]");
		}
		importConfigInfo.setSheetNum(Integer.parseInt(sheetNum));

		//startRow
		String startRow = StringUtil.safeTrim(excelNode.valueOf(Constant.ATTRIBUTE_STARTROW));
		if(!StringUtil.isNumber(startRow)){
			throw new ExcelOperateException("屬性 startRow 設定錯誤! startRow:[" + startRow + "]");
		}
		importConfigInfo.setStartRow(Integer.parseInt(startRow));
		//CheckEmptyRow
		importConfigInfo.setCheckEmptyRow(StringUtil.safeTrim(excelNode.valueOf(Constant.ATTRIBUTE_CHECK_EMPTY_ROW)));
		//check duplicate
		importConfigInfo.setCheckDuplicate(StringUtil.safeTrim(excelNode.valueOf(Constant.ATTRIBUTE_CHECK_DUPLICATE)));
		//desc
		importConfigInfo.setDesc(StringUtil.safeTrim(excelNode.valueOf(Constant.ATTRIBUTE_DESC)));

		// =========================================================
		// 讀取 excel/read 標籤 下的 column
		// =========================================================
		//讀取 node list
		List<Node> columnNodeList = excelNode.selectNodes(Constant.ELEMENT_READ + "/" + Constant.ELEMENT_COLUMN);
		//檢核
		if(StringUtil.isEmpty(columnNodeList)){
			throw new ExcelOperateException("未找到任何 <column> (config/excel/read/column)");
		}

		//收集屬性設定list
		List<ColumnInfo> columnInfoList = new ArrayList<ColumnInfo>();
		importConfigInfo.setColumnInfoList(columnInfoList);
		//紀錄KEY避免重複
		HashSet<String> keySet = new HashSet<String>();

		//逐筆讀取
		for (Node columnNode : columnNodeList) {
			//初始化物件
			ColumnInfo columnInfo = new ColumnInfo();
			//讀取共通屬性
			columnInfo.readCommonAttr(columnNode);
			//regexp
			columnInfo.setRegex(StringUtil.safeTrim(columnNode.valueOf(Constant.ATTRIBUTE_REGEX)));
			//isNull
			columnInfo.setCheckNull(StringUtil.safeTrim(columnNode.valueOf(Constant.ATTRIBUTE_CHECK_NULL)));
			//pass
			columnInfo.setPass("true".equalsIgnoreCase(StringUtil.safeTrim(columnNode.valueOf(Constant.ATTRIBUTE_PASS))));
			//add to list
			columnInfoList.add(columnInfo);
			//檢核Key 是否重複
			this.checkKey(keySet, columnInfo.getKey());
		}

		// =========================================================
		// 讀取 excel/params 標籤 下的 param
		// =========================================================
		//讀取 node list
		List<Node> paramNodeList = excelNode.selectNodes(Constant.ELEMENT_PARAMS + "/" + Constant.ELEMENT_PARAM);

		//收集屬性設定list
		List<ParamInfo> paramInfoList = new ArrayList<ParamInfo>();
		importConfigInfo.setParamInfoList(paramInfoList);

		//逐筆讀取
		for (Node paramNode : paramNodeList) {
			//初始化物件
			ParamInfo paramInfo = new ParamInfo();
			//讀取共通屬性
			paramInfo.readCommonAttr(paramNode);
			//add to list
			paramInfoList.add(paramInfo);
			//檢核Key 是否重複
			this.checkKey(keySet, paramInfo.getKey());
		}

		return importConfigInfo;
	}

	/**
	 * 檢核Key 是否重複
	 * @param keySet
	 * @param key
	 */
	private void checkKey(HashSet<String> keySet, String key){
		if(keySet.contains(key)){
			throw new ExcelOperateException("key:[" + key + "] 重複設定<column> 和 <param> 兩標籤共用檢核)  ");
		}
		keySet.add(key);
	}

	public static void main(String[] args) throws ExcelOperateException, DocumentException {
		ImportConfigInfo importConfigInfo =
				new ImportConfigReader().read("C:/workspace/RbtProject/srcExcelOpt/import_sample.xml", "LaborInsurance");

		System.out.println(new BeanUtil().showContent(importConfigInfo));
	}
}
