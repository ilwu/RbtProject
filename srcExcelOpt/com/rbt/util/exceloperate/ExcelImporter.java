/**
 *
 */
package com.rbt.util.exceloperate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import com.rbt.util.BeanUtil;
import com.rbt.util.StringUtil;
import com.rbt.util.exceloperate.bean.impt.config.AbstractImportCommonAttrInfo;
import com.rbt.util.exceloperate.bean.impt.config.ColumnInfo;
import com.rbt.util.exceloperate.bean.impt.config.FormatInfo;
import com.rbt.util.exceloperate.bean.impt.config.ImportConfigInfo;
import com.rbt.util.exceloperate.bean.impt.config.ParamInfo;
import com.rbt.util.exceloperate.config.ImportConfigReader;
import com.rbt.util.exceloperate.exception.ExcelOperateException;

/**
 * @author Allen
 */
public class ExcelImporter extends AbstractExcelOperater {

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.util.exceloperate.InterfaceExcelOperater#setConnection(java.sql.Connection)
	 */
	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 讀取檔案
	 * @param is
	 * @param importConfigInfo
	 * @return
	 * @throws Exception
	 */
	public List<LinkedHashMap<String, Object>> read(InputStream is, ImportConfigInfo importConfigInfo) throws Exception {
		return this.read(is, importConfigInfo, true);
	}

	/**
	 * 讀取檔案
	 * @param is
	 * @param importConfigInfo
	 * @param isDontShowReadFileSuppressWarnings 不顯示 jxl 讀取時的警示訊息
	 * @return
	 * @throws Exception
	 */
	public List<LinkedHashMap<String, Object>> read(InputStream is, ImportConfigInfo importConfigInfo, boolean isDontShowReadFileSuppressWarnings) throws Exception {

		this.configInfo = importConfigInfo;

		// ==============================================
		// 讀取檔案內容
		// ==============================================
		List<LinkedHashMap<String, Object>> dataList = this.parseXsl(is, importConfigInfo, isDontShowReadFileSuppressWarnings);

		// ==============================================
		// 建立以 key 為引索的 設定 map, 與收集欄位list
		// ==============================================
		// Map
		LinkedHashMap<String, AbstractImportCommonAttrInfo> paramInfoMap =
				new LinkedHashMap<String, AbstractImportCommonAttrInfo>();
		// List
		List<AbstractImportCommonAttrInfo> paramInfoList = new ArrayList<AbstractImportCommonAttrInfo>();

		for (ColumnInfo columnInfo : importConfigInfo.getColumnInfoList()) {
			paramInfoMap.put(columnInfo.getKey(), columnInfo);
			paramInfoList.add(columnInfo);
		}
		for (ParamInfo paramInfo : importConfigInfo.getParamInfoList()) {
			paramInfoMap.put(paramInfo.getKey(), paramInfo);
			paramInfoList.add(paramInfo);
		}

		// ==============================================
		// param (其他額外設定欄位)
		// ==============================================
		for (LinkedHashMap<String, Object> rowDataMap : dataList) {
			for (ParamInfo paramInfo : importConfigInfo.getParamInfoList()) {
				// 取得預設值
				String defaultValue = paramInfo.getDefaultValue();
				// 存入 map
				rowDataMap.put(paramInfo.getKey(), defaultValue);
			}
		}

		// ==============================================
		// 欄位值額外處理
		// ==============================================
		String errorMessage = "";

		int rowNum = importConfigInfo.getStartRow();
		for (LinkedHashMap<String, Object> rowDataMap : dataList) {
			for (ColumnInfo columnInfo : importConfigInfo.getColumnInfoList()) {
				// 取得欄位 key
				String key = columnInfo.getKey();
				// 取得欄位說明
				String desc = columnInfo.getDesc();
				// 取得值
				String value = StringUtil.safeTrim(rowDataMap.get(key));

				// =======================================
				// 資料檢核
				// =======================================
				// 該欄位已檢核出錯誤時, 不繼續進行檢核

				// 有限定欄位不可為空時,進行資料檢查
				if ("true".equalsIgnoreCase(columnInfo.getCheckNull()) && StringUtil.isEmpty(value)) {
					errorMessage += "<br/>第(" + rowNum + ")行, 欄位：【" + desc + "." + key + "】資料內容不可為空";
					continue;
				}
				// 有設定 formatId 時,進行資料檢查
				if (StringUtil.notEmpty(columnInfo.getFormatId())) {
					errorMessage += this.validateDataByFormatId(columnInfo.getFormatId(), value, key, desc, rowNum);
					continue;
				}
				// 有設定 regex 時,進行資料檢查
				if (StringUtil.notEmpty(columnInfo.getRegex())) {
					errorMessage += this.validateData(value, columnInfo.getRegex(), key, desc, rowNum);
					continue;
				}
			}
			rowNum++;
		}

		// 有檢核到錯誤時，拋出
		if (StringUtil.notEmpty(errorMessage)) {
			throw new ExcelOperateException(errorMessage);
		}

		// ==============================================
		// 欄位值額外處理
		// ==============================================
		for (LinkedHashMap<String, Object> rowDataMap : dataList) {

			for (Entry<String, AbstractImportCommonAttrInfo> infoEntry : paramInfoMap.entrySet()) {
				// key
				String key = infoEntry.getKey();
				// 資料值
				String value = StringUtil.safeTrim(rowDataMap.get(key));
				// 設定檔
				AbstractImportCommonAttrInfo info = infoEntry.getValue();
				// 進行額外處理
				value = this.functionProcess(key, info.getFuncId(), info.getFuncParam(), value, rowDataMap, this.conn);
				// 放回資料
				rowDataMap.put(key, value);
			}
		}

		// ==============================================
		// 檢核資料重覆
		// ==============================================
		// 未設定時跳出
		if (StringUtil.notEmpty(importConfigInfo.getCheckDuplicate())) {

			// 檢核欄位陣列
			String[] duplicateColumns = importConfigInfo.getCheckDuplicate().split(",");
			// 檢核欄位值
			HashMap<String, Integer> duplicateValueStoreMap = new HashMap<String, Integer>();

			// 重置 rowNum
			rowNum = 1;
			for (LinkedHashMap<String, Object> cellMap : dataList) {

				String keyContent = "";

				// 組成 key
				for (String checkColumnKey : duplicateColumns) {
					keyContent += StringUtil.safeTrim(cellMap.get(checkColumnKey), "NULL") + "_";
				}
				// 檢核同樣的內容是否已存在
				if (duplicateValueStoreMap.containsKey(keyContent)) {
					int num = duplicateValueStoreMap.get(keyContent);
					String errormessage = "";
					for (String checkColumnKey : duplicateColumns) {
						errormessage += "【" + paramInfoMap.get(checkColumnKey).getDesc() + "." + paramInfoMap.get(checkColumnKey).getKey() + "】:[" + cellMap.get(checkColumnKey) + "]<br/>";
					}
					throw new Exception("<br/>第[" + num + "]行資料與第[" + (rowNum + importConfigInfo.getStartRow()) + "]行重複!<br/>重複鍵值：<br/>" + errormessage);
				}
				duplicateValueStoreMap.put(keyContent, rowNum + importConfigInfo.getStartRow());
				rowNum++;
			}
		}


		// ==============================================
		// 欄位List 排序
		// ==============================================
		Collections.sort(paramInfoList,
				new Comparator<AbstractImportCommonAttrInfo>() {
					public int compare(AbstractImportCommonAttrInfo info1, AbstractImportCommonAttrInfo info2) {
						String index1 = StringUtil.safeTrim(info1.getIndex());
						String index2 = StringUtil.safeTrim(info2.getIndex());
						if (StringUtil.isEmpty(index1) || !StringUtil.isNumber(index1)) {
							return 1;
						}
						if (StringUtil.isEmpty(index2) || !StringUtil.isNumber(index2)) {
							return -1;
						}
						if(StringUtil.isNumber(index1) && StringUtil.isNumber(index2)){
							return Integer.parseInt(index1) - Integer.parseInt(index2);
						}
						return 0;
					}
				});

		// ==============================================
		// 資料欄位重新排序
		// ==============================================
		List<LinkedHashMap<String, Object>> sortDataList = new ArrayList<LinkedHashMap<String, Object>>();

		for (LinkedHashMap<String, Object> rowDataMap : dataList) {
			LinkedHashMap<String, Object> sortRowDataMap = new LinkedHashMap<String, Object>();
			for (AbstractImportCommonAttrInfo commonAttrInfo : paramInfoList) {
				String key = commonAttrInfo.getKey();
				sortRowDataMap.put(key, rowDataMap.get(key));
			}
			sortDataList.add(sortRowDataMap);
		}

		dataList = sortDataList;

		// ==============================================
		// 移除不需要的欄位
		// ==============================================
		for (LinkedHashMap<String, Object> rowDataMap : dataList) {
			for (ColumnInfo columnInfo : importConfigInfo.getColumnInfoList()) {
				// 欄位設定為 pass 時，移除該欄位
				if (columnInfo.isPass()) {
					rowDataMap.remove(columnInfo.getKey());
				}
			}
		}

		return dataList;
	}

	/**
	 * @param formatId
	 * @param value
	 * @param key
	 * @param desc
	 * @param rowNum
	 * @return
	 * @throws Exception
	 */
	private String validateDataByFormatId(String formatId, String value, String key, String desc, int rowNum) throws Exception {

		LinkedHashMap<String, FormatInfo> formatInfoMap = ((ImportConfigInfo) this.configInfo).getFormatInfoMap();

		// 讀取設定
		FormatInfo formatInfo = formatInfoMap.get(formatId);

		// 檢核設定不存在
		if (formatInfo == null) {
			throw new ExcelOperateException("Excel 處理錯誤,format 設定不存在! formatId:[" + formatId + "]");
		}

		// 處理檢核
		return this.validateData(value, formatInfo.getRegex(), key, desc, rowNum);
	}

	/**
	 * 驗證資料
	 * @param value 欄位值
	 * @param regExp regExp
	 * @param key 設定值 key
	 * @param desc 設定值 key
	 * @param rowNum 行數
	 * @return
	 * @throws Exception
	 */
	private String validateData(String value, String regExp, String key, String desc, int rowNum) throws Exception {

		// ===========================================
		// 格式驗證
		// ===========================================
		Pattern pattern = Pattern.compile(regExp);
		Matcher matcher = pattern.matcher(value);
		if (!matcher.matches()) {
			return "<br/>第(" + rowNum + ")行, 資料內容檢核錯誤！欄位【" + desc + "." + key + "】:【" + value + "】";
		}
		return "";
	}

	/**
	 * 讀取 xsl 檔案內容
	 * @param is
	 * @param importConfigInfo
	 * @return
	 * @throws BiffException
	 * @throws IOException
	 * @throws ExcelOperateException
	 */
	private List<LinkedHashMap<String, Object>> parseXsl(InputStream is, ImportConfigInfo importConfigInfo, boolean isDontShowReadFileSuppressWarnings) throws BiffException, IOException, ExcelOperateException {

		// ==============================================
		// 初始化參數
		// ==============================================
		// Workbook
		Workbook wb = null;
		// 錯誤訊息
		String blankLineMessage = "";
		// 欄位資料List
		List<LinkedHashMap<String, Object>> dataRowList = new ArrayList<LinkedHashMap<String, Object>>();

		// ==============================================
		// 設定檔資料
		// ==============================================
		// 起始行數
		int startRow = importConfigInfo.getStartRow();
		// Sheet 名稱
		int sheetNum = importConfigInfo.getSheetNum();
		if (sheetNum < 1) {
			throw new ExcelOperateException(" sheetNum 屬性設定錯誤, 不可小於 1");
		}
		// 取得欄位讀取設定
		List<ColumnInfo> columnInfoList = importConfigInfo.getColumnInfoList();

		try {
			// ==============================================
			// 讀取檔案資料
			// ==============================================
			// Workbook
			WorkbookSettings wbSetting = new WorkbookSettings();
			// 不顯示讀取時格式錯誤
			wbSetting.setSuppressWarnings(isDontShowReadFileSuppressWarnings);
			// Workbook
			wb = Workbook.getWorkbook(is, wbSetting);
			// 讀取 sheet
			Sheet sheet = wb.getSheet(sheetNum - 1);

			if (sheet == null) {
				throw new ExcelOperateException(" 檔案解析錯誤，第[" + sheetNum + "]個 sheet 不存在");
			}

			//sheet.setSuppressWarnings(true);

			// ==============================================
			// 讀取行
			// ==============================================
			// 取得行數
			int rows = sheet.getRows();
			// 檢核無內容
			if (rows - startRow < 0) {
				throw new ExcelOperateException("上傳檔案無資料內容! rows[" + rows + "], startRow:[" + startRow + "]");
			}

			// 檢核是否以下都是空行
			boolean tailBlankLine = false;

			for (int rowNum = (startRow - 1); rowNum < rows; rowNum++) {
				// 取得 Cell 陣列
				Cell[] cells = sheet.getRow(rowNum);
				// 資料MAP
				LinkedHashMap<String, Object> dataRowMap = new LinkedHashMap<String, Object>();
				//
				boolean isBlankline = true;
				for (int colNum = 0; colNum < columnInfoList.size(); colNum++) {
					// 取得欄位參數設定
					ColumnInfo columnInfo = columnInfoList.get(colNum);

					String cellContent = "";

					//長度足夠時才讀取 cell
					if((colNum + 1) <= cells.length){
						// 取得 cell 內容 (並去空白)
						cellContent = StringUtil.safeTrim(cells[colNum].getContents());
					}

					// 不為空時異動 flag
					if (StringUtil.notEmpty(cellContent)) {
						isBlankline = false;
					}

					// 為空時，放入預設值
					if (StringUtil.isEmpty(cellContent)) {
						cellContent = columnInfo.getDefaultValue();
					}
					// 依據Key放入放入 map
					dataRowMap.put(columnInfo.getKey(), cellContent);
				}

				// 本行無資料時
				if (isBlankline) {
					// 尾部空行標記
					tailBlankLine = true;
					// 空行訊息
					blankLineMessage += "第" + (rowNum + 1) + "行為空行，請重新檢查資料";
					// 空行略過 加入List
					continue;
				}

				tailBlankLine = false;
				// 加入List
				dataRowList.add(dataRowMap);
			}

			// ==============================================
			// 檢核
			// ==============================================
			// 1.排除以下都是空行
			// 2.有設定需檢核空行
			// 3.錯誤訊息不為空
			if (!tailBlankLine &&
					"true".equalsIgnoreCase(importConfigInfo.getCheckEmptyRow()) &&
					StringUtil.notEmpty(blankLineMessage)) {
				throw new ExcelOperateException("資料內容有誤!\n" + blankLineMessage);
			}

			return dataRowList;

		} finally {
			if (is != null) {
				is.close();
			}
			if (wb != null) {
				wb.close();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String configFile = "C:/workspace/RbtProject/srcExcelOpt/import_sample.xml";
		String configID = "LaborInsurance";
		String importFile = "H:/0723/活頁簿1.xls";

		ExcelImporter excelImporter = new ExcelImporter();
		FileInputStream is = null;

		try {
			//

			ImportConfigInfo importConfigInfo = new ImportConfigReader().read(configFile, configID);
			is = new FileInputStream(new File(importFile));

			System.out.println(new BeanUtil().showContent(excelImporter.read(is, importConfigInfo)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
