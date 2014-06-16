package com.rbt.util.excel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

import com.rbt.exception.UtilException;
import com.rbt.util.BeanUtil;
import com.rbt.util.StringUtil;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * @author Allen
 */
public class ExcelReader {

	/**
	 * LOG4j
	 */
	protected Logger LOG = Logger.getLogger(ExcelReader.class);

	/**
	 * 建構函式
	 */
	public ExcelReader() {
	}

	/**
	 *
	 */
	public static final String DEF_ROW_END_FLAG = "#END";

	/**
	 * @param is InputStream
	 * @return Parser結果
	 * @throws Exception
	 */
	public List<Object> xslParser(InputStream is) throws Exception {

		// ==============================================
		// 參數
		// ==============================================
		// 定義列
		List<ExcelDefBean> excelDefBeanList = null;
		// 資料列起始 index
		int dataRowStartIndex = -1;
		// 使用的 model bean 名稱
		String modelDefClassName = "";

		// ==============================================
		// 讀取檔案
		// ==============================================

		Sheet sheet = null;
		try {
			// 讀取 EXCEL檔案
			Workbook wb = Workbook.getWorkbook(is);
			// 讀取第一個工作表
			sheet = wb.getSheet(0);
		} catch (Exception e) {
			this.LOG.warn("上傳之EXCEL 檔案解析錯誤");
			this.LOG.warn("\n" + StringUtil.getExceptionStackTrace(e));
			// 上傳檔案損壞或格式錯誤
			throw new UtilException("上傳檔案損壞或格式錯誤");
		}

		// ==============================================
		// 取得定義列
		// ==============================================
		// 判斷工作表的大小（列數）
		int rowsCount = sheet.getRows();
		// 取得每一列的最後一個 Cell 內容
		for (int i = 0; i < rowsCount; i++) {
			// 取得列中的每一欄
			Cell[] cells = sheet.getRow(i);
			// 若數目為0時此列跳過
			if (cells.length == 0)
				continue;

			// 由第一欄開始檢查, 直到找到 DEF_ROW_END_FLAG 為止
			for (Cell cell : cells) {
				// 取得 cell 內容
				String cellContent = StringUtil.safeTrim(cell.getContents());
				// 若為 DEF_ROW_END_FLAG
				if (cellContent.startsWith(DEF_ROW_END_FLAG)) {
					// 若定義列以下無資料，則判斷此檔為空檔
					if (i == rowsCount - 1) {
						// 上傳之 EXCEL 檔案無資料
						throw new UtilException("上傳之 EXCEL 檔案無資料");
					}
					// 解析定義行 (row)
					excelDefBeanList = this.parsingDefCell(cells);
					// 定義資料開始行
					dataRowStartIndex = i + 1;
					//
					modelDefClassName = this.getModelDefName(cellContent);
					break;
				}
			}

		}

		if (excelDefBeanList == null) {
			// 上傳檔案無定義行
			//new MvcBusinessException("UPLOAD_EXCEL_NO_DEF_ERROR");
			throw new UtilException("上傳檔案無定義行");
		}

		// ==============================================
		// 讀取資料
		// ==============================================
		List<Object> beanList = new ArrayList();
		for (int i = dataRowStartIndex; i < rowsCount; i++) {
			beanList.add(this.parsingDataCellToBean(modelDefClassName, sheet.getRow(i), excelDefBeanList));
		}
		return beanList;
	}

	/**
	 * @param defStr
	 * @return
	 */
	private String getModelDefName(String defStr) {
		int sIdx = defStr.indexOf("#");
		int eIdx = defStr.lastIndexOf("#");

		defStr = defStr.substring(sIdx + 1, eIdx);

		String[] defStrs = defStr.split(",");

		if (defStrs.length != 2) {
			//上傳檔案定義錯誤(MODEL)
			//new MvcBusinessException("UPLOAD_EXCEL_DEF_MODEL_DEF_ERROR");
			throw new UtilException("上傳檔案定義錯誤");
		}
		return defStrs[1];
	}

	/**
	 * 解析資料行資料
	 * @param cells
	 * @param excelDefBeanList
	 * @return
	 * @throws Exception
	 */
	private Object parsingDataCellToBean(String className, Cell[] cells, List<ExcelDefBean> excelDefBeanList) throws Exception {

		Object bean = null;
		try {
			Class cls = Class.forName(className);
			bean = cls.newInstance();
		} catch (Exception e) {
			this.LOG.equals("class:[" + className + "] 初始化失敗!");
			this.LOG.error(StringUtil.getExceptionStackTrace(e));
			// UPLOAD_EXCEL_DEF_MODEL_INIT_ERROR=上傳檔案定義MODEL錯誤
			//throw new MvcBusinessException("UPLOAD_EXCEL_DEF_MODEL_INIT_ERROR");
			throw new UtilException("上傳檔案定義MODEL錯誤");
		}
		BeanUtil beanUtil = new BeanUtil();

		for (ExcelDefBean excelDefBean : excelDefBeanList) {
			// 取得 cell
			Cell cell = cells[excelDefBean.cellIndex];
			// 內容
			String content = cell.getContents();
			// 資料型態
			Class dataType = excelDefBean.dataType;
			try {

				if (dataType.getName().equals(String.class.getName())) {
					beanUtil.setObjectParamValue(excelDefBean.columnName, bean, content);
				} else if (dataType.getName().equalsIgnoreCase(Integer.class.getName())) {
					content = convertToNumberStr(content);
					beanUtil.setObjectParamValue(excelDefBean.columnName, bean, Integer.parseInt(content));
				} else if (dataType.getName().equalsIgnoreCase(Long.class.getName())) {
					content = convertToNumberStr(content);
					beanUtil.setObjectParamValue(excelDefBean.columnName, bean, Long.parseLong(content));
				} else if (dataType.getName().equalsIgnoreCase(Double.class.getName())) {
					content = convertToNumberStr(content);
					beanUtil.setObjectParamValue(excelDefBean.columnName, bean, Double.parseDouble(content));
				} else if (dataType.getName().equalsIgnoreCase(BigDecimal.class.getName())) {
					content = convertToNumberStr(content);
					beanUtil.setObjectParamValue(excelDefBean.columnName, bean, new BigDecimal(content));
				} else {
					// 定義欄位中有未定義的資料型態
					//new MvcBusinessException("UPLOAD_EXCEL_DEF_UNDEFTYPE_ERROR");
					throw new UtilException("定義欄位中有未定義的資料型態");
				}
			} catch (IllegalArgumentException e) {
				this.LOG.error("欄位型態定義錯誤！" + excelDefBean.columnName + ":[" + dataType + "]");
				throw e;
			} catch (UtilException e) {
				throw e;
			} catch (Exception e) {
				this.LOG.error(StringUtil.getExceptionStackTrace(e));
				//UPLOAD_EXCEL_DEF_NOT_MAPPING_ERROR=上傳檔案定義錯誤(MODEL與定義欄位不相符)
				//new MvcBusinessException("UPLOAD_EXCEL_DEF_NOT_MAPPING_ERROR");
				throw new UtilException("上傳檔案定義錯誤(MODEL與定義欄位不相符)");
			}
		}
		return bean;
	}

	/**
	 * @param content
	 * @return
	 */
	private String convertToNumberStr(String content){
		if (StringUtil.isEmpty(content)){
			content = "0";
		}
		return content.replaceAll(",", "");
	}

	/**
	 * 解析資料行資料
	 * @param cells
	 * @param excelDefBeanList
	 * @return HashMap<資料欄名稱, value>
	 */
	public HashMap<String, Object> parsingDataCell(Cell[] cells, List<ExcelDefBean> excelDefBeanList) {

		HashMap<String, Object> dataMap = new HashMap();

		for (ExcelDefBean excelDefBean : excelDefBeanList) {
			// 取得 cell
			Cell cell = cells[excelDefBean.cellIndex];
			// 內容
			String content = cell.getContents();
			// 資料型態
			Class dataType = excelDefBean.dataType;

			if (dataType.getName().equals(String.class.getName())) {
				dataMap.put(excelDefBean.columnName, content);
			} else if (dataType.getName().equalsIgnoreCase(Integer.class.getName())) {
				content = convertToNumberStr(content);
				dataMap.put(excelDefBean.columnName, Integer.parseInt(content));
			} else if (dataType.getName().equalsIgnoreCase(Long.class.getName())) {
				content = convertToNumberStr(content);
				dataMap.put(excelDefBean.columnName, Long.parseLong(content));
			} else if (dataType.getName().equalsIgnoreCase(Double.class.getName())) {
				content = convertToNumberStr(content);
				dataMap.put(excelDefBean.columnName, Double.parseDouble(content));
			} else if (dataType.getName().equalsIgnoreCase(BigDecimal.class.getName())) {
				content = convertToNumberStr(content);
				dataMap.put(excelDefBean.columnName, new BigDecimal(content));
			} else {
				// 定義欄位中有未定義的資料型態
				//new MvcBusinessException("UPLOAD_EXCEL_DEF_UNDEFTYPE_ERROR");
				throw new UtilException("定義欄位中有未定義的資料型態");
			}
		}
		return dataMap;
	}

	/**
	 * 解析定義行
	 * @param cells
	 * @return
	 */
	private List<ExcelDefBean> parsingDefCell(Cell[] cells) {

		List<ExcelDefBean> excelDefBeanList = new ArrayList();
		HashMap cloumnNameMap = new HashMap();

		for (int i = 0; i < cells.length; i++) {
			// 取得內容
			String countent = cells[i].getContents();
			// 遇到結束符號則跳出
			if (countent != null && countent.startsWith(DEF_ROW_END_FLAG)) {
				break;
			}

			// ============================
			// 解析 & 檢核
			// ============================
			int sIdx = countent.indexOf("[");
			int eIdx = countent.indexOf("]");

			if (sIdx == -1 || eIdx < 1 || eIdx < sIdx) {
				// 上傳檔案定義欄位錯誤
				//new MvcBusinessException("第" + i + "攔定義行解析失敗:[" + countent + "],sIdx:[" + sIdx + "],eIdx:[" + eIdx + "] 請重新檢查上傳檔案","UPLOAD_EXCEL_DEF_ERROR");
				throw new UtilException("第" + i + "攔定義行解析失敗:[" + countent + "],sIdx:[" + sIdx + "],eIdx:[" + eIdx + "] 請重新檢查上傳檔案");
			}
			countent = countent.substring(sIdx + 1, eIdx);

			String[] defStr = countent.split(",");
			if (defStr.length != 2) {
				// 上傳檔案定義欄位錯誤
				//new MvcBusinessException("第" + i + "攔定義行解析失敗:[" + countent + "] 請重新檢查上傳檔案", "UPLOAD_EXCEL_DEF_ERROR");
				throw new UtilException("第" + i + "攔定義行解析失敗:[" + countent + "] 請重新檢查上傳檔案");
			}

			String columnName = StringUtil.safeTrim(defStr[0]);
			String className = StringUtil.safeTrim(defStr[1]);
			if (columnName.length() == 0 || className.length() == 0) {
				// 上傳檔案定義欄位錯誤
				//new MvcBusinessException("第" + i + "攔定義行解析失敗:[" + countent + "] 請重新檢查上傳檔案", "UPLOAD_EXCEL_DEF_ERROR");
				throw new UtilException("第" + i + "攔定義行解析失敗:[" + countent + "] 請重新檢查上傳檔案");
			}

			// 檢核定義欄位重覆
			if (cloumnNameMap.containsKey(columnName)) {
				// 上傳檔案定義欄位重覆
				//new MvcBusinessException("UPLOAD_EXCEL_DEF_DEDUPLICATE_ERROR");
				throw new UtilException("上傳檔案定義欄位重覆");
			}
			cloumnNameMap.put(columnName, "Y");

			// ============================
			// 建立定義 bean
			// ============================
			ExcelDefBean bean = new ExcelDefBean();
			bean.cellIndex = i;
			bean.columnName = columnName;
			try {
				bean.dataType = Class.forName(className);
				if (bean.dataType == null)
					throw new Exception();
			} catch (Exception e) {
				// 上傳檔案定義欄位類型錯誤(無法解析定義Class)
				//new MvcBusinessException("UPLOAD_EXCEL_DEF_TYPE_ERROR");
				throw new UtilException("上傳檔案定義欄位類型錯誤(無法解析定義Class)");
			}
			excelDefBeanList.add(bean);
		}
		return excelDefBeanList;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		InputStream is = new FileInputStream("H:/0222/1020205-2011全年.xls");
		System.out.println(new BeanUtil().showContent((new ExcelReader().xslParser(is))));
	}
}
