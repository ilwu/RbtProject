package com.rbt.util.exceloperate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.PageOrientation;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WritableFont.FontName;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.rbt.util.StringUtil;
import com.rbt.util.exceloperate.bean.expt.ColumnDataSet;
import com.rbt.util.exceloperate.bean.expt.ExportDataSet;
import com.rbt.util.exceloperate.bean.expt.config.AbstractStyleInfo;
import com.rbt.util.exceloperate.bean.expt.config.ColumnDetailInfo;
import com.rbt.util.exceloperate.bean.expt.config.ColumnInfo;
import com.rbt.util.exceloperate.bean.expt.config.ContextInfo;
import com.rbt.util.exceloperate.bean.expt.config.DetailInfo;
import com.rbt.util.exceloperate.bean.expt.config.ExportConfigInfo;
import com.rbt.util.exceloperate.bean.expt.config.SheetlInfo;
import com.rbt.util.exceloperate.bean.expt.config.StyleInfo;
import com.rbt.util.exceloperate.bean.expt.config.TdInfo;
import com.rbt.util.exceloperate.bean.expt.config.TrInfo;
import com.rbt.util.exceloperate.config.ExportConfigReader;
import com.rbt.util.exceloperate.exception.ExcelOperateException;

/**
 * 匯出Excel 檔案
 * @author Allen
 */
public class ExcelExporter extends AbstractExcelOperater{


	// ===========================================================================
	// 名稱
	// ===========================================================================
	/**
	 * 最大 column index
	 */
	private static final String KEY_MAX_COL = "KEY_MAX_COL";
	/**
	 *
	 */
	private static final String KEY_COLUMN_COLSPAN_PERFIX = "#COLUMN_COLSPAN#";

	// ===========================================================================
	// 功能區
	// ===========================================================================
	/* (non-Javadoc)
	 * @see com.rbt.util.exceloperate.InterfaceExcelOperater#setConnection(java.sql.Connection)
	 */
	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 產生Excel
	 * @param configInfo
	 * @param out
	 * @throws IOException
	 * @throws WriteException
	 */
	public void export(ExportConfigInfo exportConfigInfo, ExportDataSet exportDataSet, OutputStream out) throws ExcelOperateException {

		this.configInfo = exportConfigInfo;

		try {
			// =========================================================
			// 建立 Workbook
			// =========================================================
			WritableWorkbook writableWorkbook = Workbook.createWorkbook(out);

			for (int sheetlIndex = 0; sheetlIndex < exportConfigInfo.getSheetList().size(); sheetlIndex++) {
				// =====================================================
				// 建立 sheet
				// =====================================================
				// 取得 sheetlInfo 設定
				SheetlInfo sheetlInfo = exportConfigInfo.getSheetList().get(sheetlIndex);

				// 取得 sheetName
				String sheetName = (StringUtil.isEmpty(sheetlInfo.getSheetName())) ? "Sheet" + (sheetlIndex + 1) : sheetlInfo.getSheetName();

				// 建立 sheet
				WritableSheet writableSheet = writableWorkbook.createSheet(sheetName, sheetlIndex);

				// 版面設定
				// setPageSetup Parameters:
				// p - the page orientation
				// ps - the paper size
				// hm - the header margin, in inches
				// fm - the footer margin, in inches
				writableSheet.setPageSetup(PageOrientation.LANDSCAPE, exportConfigInfo.getPaperSize(), 0, 0);
				writableSheet.getSettings().setLeftMargin(0);
				writableSheet.getSettings().setRightMargin(0);

				// =====================================================
				// 處理前準備
				// =====================================================
				// 列指標
				int targetRowIndex = 0;
				// 紀錄已使用的儲存格 (cell)
				HashMap<Integer, HashSet<Integer>> usedCells = new HashMap<Integer, HashSet<Integer>>();
				// 紀錄欄(column)的最大欄寬
				HashMap<String, Integer> maxWidthMap = new HashMap<String, Integer>();

				// =====================================================
				// 資訊
				// =====================================================
				for (Entry<String, Object> entry : sheetlInfo.getPartInfoMap().entrySet()) {

					if (entry.getValue() == null) {
						return;
					}

					// 內容為 context
					if (entry.getKey().startsWith(Constant.ELEMENT_CONTEXT)) {
						ContextInfo contextInfo = (ContextInfo) entry.getValue();
						targetRowIndex = this.writeContext(
								writableSheet,
								contextInfo,
								targetRowIndex,
								exportDataSet.getContext(contextInfo.getDataId()),
								usedCells,
								maxWidthMap
								);
					} else if (entry.getKey().startsWith(Constant.ELEMENT_DETAIL)) {
						DetailInfo detailInfo = (DetailInfo) entry.getValue();
						targetRowIndex = this.enterWriteDetail(
								writableSheet,
								detailInfo,
								targetRowIndex,
								exportDataSet.getDetail(detailInfo.getDataId()),
								usedCells,
								maxWidthMap);
					}
				}

				// =====================================================
				// 設定欄寬
				// =====================================================
				// 取得最大欄位 index
				int maxColIndex = maxWidthMap.get(KEY_MAX_COL);
				for (int colIndex = 0; colIndex <= maxColIndex; colIndex++) {
					// 取得欄寬
					int colWidth = 0;
					//取得 MAP 中的值 (tr、td 設定)
					if(StringUtil.isNumber(StringUtil.safeTrim(maxWidthMap.get(colIndex + "")))){
						colWidth = Integer.parseInt(StringUtil.safeTrim(maxWidthMap.get(colIndex + ""), "0"));
					}
					//若 trtd 未設定時，取 style 設定
					if (colWidth == 0) {
						colWidth = Integer.parseInt(StringUtil.safeTrim(exportConfigInfo.getStyleInfo().getWidth(), "0"));
					}

					// 以上都未設定時使用預設值
					if (colWidth == 0) {
						colWidth = Integer.parseInt(Constant.DEFAULT_WIDTH);
					}
					writableSheet.setColumnView(colIndex, colWidth);
				}
			}

			writableWorkbook.write();
			writableWorkbook.close();

		} catch (Exception e) {
			this.LOG.error(StringUtil.getExceptionStackTrace(e));
			throw new ExcelOperateException("EXCEL 檔案匯出處理錯誤! " + e.getMessage());
		}
	}

	/**
	 * @param writableSheet
	 * @param trInfoList
	 * @param targetRowIndex
	 * @param usedCells
	 * @return
	 * @throws WriteException
	 */
	private int enterWriteDetail(
			WritableSheet writableSheet,
			DetailInfo detailInfo,
			int targetRowIndex,
			List<ColumnDataSet> columnDataSetList,
			HashMap<Integer, HashSet<Integer>> usedCells,
			HashMap<String, Integer> maxWidthMap
			) throws WriteException {

		// 取得欄位設定欄位
		List<ColumnInfo> columnInfoList = detailInfo.getColumnInfoList();

		// 無資料時跳出
		if (StringUtil.isEmpty(detailInfo.getColumnInfoList()) || StringUtil.isEmpty(columnDataSetList)) {
			return targetRowIndex;
		}

		// 計算 rowspan
		for (ColumnDataSet columnDataSet : columnDataSetList) {
			this.countRowspan(columnInfoList, columnDataSet);
		}

		// excel 欄位輸出
		for (ColumnDataSet columnDataSet : columnDataSetList) {
			targetRowIndex = this.writeDetail(
					writableSheet, columnInfoList, columnDataSet,
					targetRowIndex, usedCells, maxWidthMap);
		}

		return targetRowIndex;
	}

	private int writeDetail(
			WritableSheet writableSheet,
			List<ColumnInfo> columnInfoList,
			ColumnDataSet columnDataSet,
			int targetRowIndex,
			HashMap<Integer, HashSet<Integer>> usedCells,
			HashMap<String, Integer> maxWidthMap
			) throws WriteException {

		int targetColIndex = 0;
		int newTargetRowIndex = targetRowIndex;

		for (ColumnInfo columnInfo : columnInfoList) {

			// 取得子欄位
			List<ColumnDetailInfo> columnDetailInfoList = columnInfo.getColumnDetailInfoList();

			// 為子欄位陣列時，進行遞迴處理
			if (StringUtil.notEmpty(columnDetailInfoList)) {

				for (ColumnDetailInfo columnDetailInfo : columnDetailInfoList) {

					// 設定元素類別
					String type = columnDetailInfo.getType();
					// dataId
					String dataId = columnDetailInfo.getDataId();
					// 欄位下的欄位
					List<ColumnInfo> childColumnInfoList = columnDetailInfo.getColumnInfoList();

					// ELEMENT_SINGLE
					if (Constant.ELEMENT_SINGLE.equalsIgnoreCase(type)) {
						// 遞迴處理
						newTargetRowIndex = this.writeDetail(
								writableSheet,
								childColumnInfoList,
								columnDataSet.getSingle(dataId),
								newTargetRowIndex,
								usedCells,
								maxWidthMap);
					}

					if (Constant.ELEMENT_ARRAY.equalsIgnoreCase(type)) {
						// 取得 array 元素的資料集
						List<ColumnDataSet> arrayDataList = columnDataSet.getArray(dataId);
						// 逐筆處理
						for (ColumnDataSet arrayColumnDataSet : arrayDataList) {
							// 遞迴處理
							newTargetRowIndex = this.writeDetail(
									writableSheet,
									childColumnInfoList,
									arrayColumnDataSet,
									newTargetRowIndex,
									usedCells,
									maxWidthMap);
						}
					}
				}
				continue;
			}

			// 取得 key
			String key = columnInfo.getKey();
			// 取得欄位設定
			WritableCellFormat cellFormat = this.getCellFormat(columnInfo, columnInfo);
			// 取得要放入 cell 的值
			String content = this.perpareContent(key, columnInfo.getDefaultValue(), columnInfo.getFuncId(), columnInfo.getFuncParam(), columnDataSet.getColumnDataMap());
			// 取得寬度設定
			int width = Integer.parseInt(StringUtil.safeTrim(columnInfo.getWidth(), "0"));
			// 取得還未使用的 column
			targetColIndex = this.getUnUsedCol(usedCells, targetRowIndex, targetColIndex);

			// 取得 rowspan (之前已計算好)
			int rowspan = (Integer) columnDataSet.getColumnDataMap().get(KEY_COLUMN_COLSPAN_PERFIX + key);
			// colspan
			int colspan = columnInfo.getColspan();

			if (colspan > 1 || rowspan > 1) {
				// 合併儲存格
				this.merageCell(writableSheet, usedCells, targetColIndex, targetRowIndex, colspan, rowspan, maxWidthMap, width);
				// addCell
				this.addCell(writableSheet, usedCells, targetColIndex, targetRowIndex, content, cellFormat, maxWidthMap, width, key);

				// 移動 col 指標
				if (colspan > 0) {
					targetColIndex += colspan;
				} else {
					targetColIndex++;
				}
			} else {
				// addCell
				this.addCell(writableSheet, usedCells, targetColIndex, targetRowIndex, content, cellFormat, maxWidthMap, width, key);
				// 移動 col 指標
				targetColIndex++;
			}
		}
		targetRowIndex++;
		// newTargetRowIndex++;
		return (targetRowIndex > newTargetRowIndex) ? targetRowIndex : newTargetRowIndex;
	}

	/**
	 * 計算 rowspan
	 * @param columnInfoList
	 * @param detailDataSet
	 * @return
	 * @throws WriteException
	 */
	private int countRowspan(List<ColumnInfo> columnInfoList, ColumnDataSet columnDataSet) throws WriteException {

		int myRowspan = 0;
		for (ColumnInfo columnInfo : columnInfoList) {

			// 取得子欄位
			List<ColumnDetailInfo> columnDetailInfoList = columnInfo.getColumnDetailInfoList();

			// 無子欄位設定時略過
			if (StringUtil.isEmpty(columnDetailInfoList)) {
				continue;
			}

			int currRowAdd = 0;
			for (ColumnDetailInfo columnDetailInfo : columnDetailInfoList) {

				// 設定元素類別
				String type = columnDetailInfo.getType();
				// dataId
				String dataId = columnDetailInfo.getDataId();
				// 欄位下的欄位
				List<ColumnInfo> childColumnInfoList = columnDetailInfo.getColumnInfoList();

				// ELEMENT_SINGLE
				if (Constant.ELEMENT_SINGLE.equalsIgnoreCase(type)) {
					// 遞迴處理
					currRowAdd += this.countRowspan(
							childColumnInfoList,
							columnDataSet.getSingle(dataId));
				}

				if (Constant.ELEMENT_ARRAY.equalsIgnoreCase(type)) {
					// 取得 array 元素的資料集
					List<ColumnDataSet> arrayDataList = columnDataSet.getArray(dataId);
					// 逐筆處理
					for (ColumnDataSet arrayColumnDataSet : arrayDataList) {
						// 遞迴處理
						currRowAdd += this.countRowspan(
								childColumnInfoList,
								arrayColumnDataSet);
					}
				}
				// 取得最大 rowspan
				if (currRowAdd > myRowspan) {
					myRowspan = currRowAdd;
				}
			}
		}

		// 加上基礎的1列
		if (myRowspan == 0) {
			myRowspan = 1;
		}

		for (ColumnInfo columnInfo : columnInfoList) {

			// 取得子欄位
			List<ColumnDetailInfo> columnDetailInfoList = columnInfo.getColumnDetailInfoList();

			// 有子欄位設定時略過
			if (StringUtil.notEmpty(columnDetailInfoList)) {
				continue;
			}

			// 取得 key
			String key = columnInfo.getKey();
			// 把 rowspan值 以 key 加上固定前綴之後, 放入 資料MAP
			columnDataSet.getColumnDataMap().put(KEY_COLUMN_COLSPAN_PERFIX + key, myRowspan);
		}
		return myRowspan;
	}

	/**
	 * @param writableSheet
	 * @param trInfoList
	 * @param targetRowIndex
	 * @param dataMap
	 * @param usedCells
	 * @param maxWidthMap
	 * @return
	 * @throws WriteException
	 */
	private int writeContext(
			WritableSheet writableSheet,
			ContextInfo contextInfo,
			int targetRowIndex,
			Map<String, Object> dataMap,
			HashMap<Integer, HashSet<Integer>> usedCells,
			HashMap<String, Integer> maxWidthMap
			) throws WriteException {

		// 無資料時跳出
		if (contextInfo.getTrInfoList() == null) {
			return targetRowIndex;
		}

		// 逐列處理
		for (int row = 0; row < contextInfo.getTrInfoList().size(); row++) {

			TrInfo trInfo = contextInfo.getTrInfoList().get(row);

			// col index 指標
			int targetColIndex = 0;

			for (int col = 0; col < trInfo.getTdInfoList().size(); col++) {
				// 取得 TdInfo
				TdInfo tdInfo = trInfo.getTdInfoList().get(col);
				// 取得欄位設定
				WritableCellFormat cellFormat = this.getCellFormat(trInfo, tdInfo);
				// 取得要放入 cell 的值
				String content = this.perpareContent(tdInfo.getKey(), tdInfo.getDefaultValue(), tdInfo.getFuncId(), tdInfo.getFuncParam(), dataMap);
				// 取得寬度設定
				int width = Integer.parseInt(StringUtil.safeTrim(tdInfo.getWidth(), "0"));
				// 取得還未使用的 column
				targetColIndex = this.getUnUsedCol(usedCells, targetRowIndex, targetColIndex);

				if (tdInfo.getColspan() > 1 || tdInfo.getRowspan() > 1) {

					// 合併儲存格
					this.merageCell(writableSheet, usedCells, targetColIndex, targetRowIndex, tdInfo.getColspan(), tdInfo.getRowspan(), maxWidthMap, width);
					// addCell
					this.addCell(writableSheet, usedCells, targetColIndex, targetRowIndex, content, cellFormat, maxWidthMap, width, tdInfo.getKey());

					// 移動 col 指標
					if (tdInfo.getColspan() > 0) {
						targetColIndex += tdInfo.getColspan();
					} else {
						targetColIndex++;
					}
				} else {
					// addCell
					this.addCell(writableSheet, usedCells, targetColIndex, targetRowIndex, content, cellFormat, maxWidthMap, width, tdInfo.getKey());
					// 移動 col 指標
					targetColIndex++;
				}
			}
			targetRowIndex++;
		}
		return targetRowIndex;
	}

	/**
	 * 設定已使用欄位
	 * @param usedCells
	 * @param rowNum
	 * @param colNum
	 */
	private void setUsed(HashMap<Integer, HashSet<Integer>> usedCells, int rowNum, int colNum) {
		HashSet<Integer> colNumSet = usedCells.get(rowNum);
		if (colNumSet == null) {
			colNumSet = new HashSet<Integer>();
			usedCells.put(rowNum, colNumSet);
		}
		colNumSet.add(colNum);
	}

	/**
	 * 設定已使用欄位 (範圍)
	 * @param usedCells
	 * @param startCol
	 * @param startRow
	 * @param endCol
	 * @param endRow
	 */
	private void setUsed(HashMap<Integer, HashSet<Integer>> usedCells, int startCol, int startRow, int endCol, int endRow) {
		for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
			for (int colNum = startCol; colNum <= endCol; colNum++) {
				this.setUsed(usedCells, rowNum, colNum);
			}
		}
	}

	/**
	 * 取得還未使用的 column index
	 * @param usedCells 已使用區域紀錄物件
	 * @param rowNum row index
	 * @param startColNum 開始尋找的 column index
	 * @return
	 */
	private int getUnUsedCol(HashMap<Integer, HashSet<Integer>> usedCells, int rowNum, int startColNum) {
		HashSet<Integer> colNumSet = usedCells.get(rowNum);
		if (colNumSet == null) {
			return 0;
		}
		int newColNum = startColNum;
		while (colNumSet.contains(newColNum)) {
			newColNum++;
		}
		return newColNum;
	}

	/**
	 * 合併儲存格
	 * @param writableSheet WritableSheet
	 * @param usedCells 已使用區域紀錄物件
	 * @param startCol 開始的 column index (行)
	 * @param startRow 開始的 row index (列)
	 * @param colspan colspan
	 * @param rowspan rowspan
	 * @param maxWidthMap 最大欄寬紀錄物件
	 * @param width 欄寬
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private void merageCell(WritableSheet writableSheet,
			HashMap<Integer, HashSet<Integer>> usedCells,
			int startCol, int startRow, int colspan, int rowspan,
			HashMap<String, Integer> maxWidthMap,
			int width
			) throws RowsExceededException, WriteException {

		int endCol = startCol;
		if (colspan > 1) {
			endCol += colspan - 1;
		}
		int endRow = startRow;
		if (rowspan > 1) {
			endRow += rowspan - 1;
		}
		// 合併儲存格
		writableSheet.mergeCells(startCol, startRow, endCol, endRow);
		// 記錄使用區域
		this.setUsed(usedCells, startCol, startRow, endCol, endRow);
		// 記錄欄寬
		for (int colNum = startCol; colNum <= endCol; colNum++) {
			this.setMaxWidth(maxWidthMap, width, colNum);
		}
	}

	/**
	 * add Cell
	 * @param writableSheet WritableSheet
	 * @param usedCells 已使用區域紀錄物件
	 * @param colIndex column index
	 * @param rowIndex row index
	 * @param content 文字內容
	 * @param cellFormat cell 格式設定物件
	 * @param maxWidthMap 最大欄寬紀錄物件
	 * @param width 欄寬
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private void addCell(
			WritableSheet writableSheet,
			HashMap<Integer, HashSet<Integer>> usedCells,
			int colIndex, int rowIndex,
			String content, WritableCellFormat cellFormat,
			HashMap<String, Integer> maxWidthMap,
			int width,
			String key
			) throws RowsExceededException, WriteException {
		// System.out.println(key + ": [" + rowIndex + "," + colIndex + "],[" + content + "]");
		// 新增 cell
		writableSheet.addCell(new Label(colIndex, rowIndex, content, cellFormat));
		// 記錄使用區域
		this.setUsed(usedCells, rowIndex, colIndex);
		// 記錄欄寬
		this.setMaxWidth(maxWidthMap, width, colIndex);
	}

	/**
	 * 記錄欄寬
	 * @param maxWidthMap
	 * @param width
	 * @param colIndex
	 */
	private void setMaxWidth(HashMap<String, Integer> maxWidthMap, int width, int colIndex) {
		// ================================================
		// 紀錄最大 INDEX
		// ================================================
		// 取得目前最大的 index
		int maxColIndex = Integer.parseInt(StringUtil.safeTrim(maxWidthMap.get(KEY_MAX_COL), "0"));
		// 傳入值大於現值時，更新
		if (colIndex > maxColIndex) {
			maxWidthMap.put(KEY_MAX_COL, colIndex);
		}

		// ================================================
		// 紀錄最大欄寬
		// ================================================
		String key = "" + colIndex;
		// 取得該index目前設定值
		int maxWidth = Integer.parseInt(StringUtil.safeTrim(maxWidthMap.get(key), "0"));
		// 傳入值大於現值時，更新
		if (width > maxWidth) {
			maxWidthMap.put(key, width);
		}
	}

	/**
	 * 準備要放入 cell 的資料
	 * @param key
	 * @param value
	 * @param funcId
	 * @param funcParam
	 * @param dataMap
	 * @return
	 */
	private String perpareContent(
			String key, String value,
			String funcId, String funcParam,
			Map<String, Object> dataMap
			) {

		String content = "";

		// KEY 有設定時優先處理
		if (dataMap != null && StringUtil.notEmpty(key) && dataMap.containsKey(key)) {
			content = StringUtil.safeTrim(dataMap.get(key));
		}
		// 取不到值時,
		if (StringUtil.isEmpty(content)) {
			content = value;
		}
		// 呼叫處理的 function
		if (StringUtil.notEmpty(funcId)) {
			content = this.functionProcess(key, funcId, funcParam, value, dataMap, this.conn);
		}
		return content;
	}

	/**
	 * 設定 Cell 格式 (Detal時, 沒有 tr 和 td, 此時兩個參數傳入同一個物件, 不影響判斷)
	 * @param trInfo
	 * @param tdInfo
	 * @return
	 * @throws WriteException
	 */
	private WritableCellFormat getCellFormat(AbstractStyleInfo trInfo, AbstractStyleInfo tdInfo) throws WriteException {

		//style 設定
		StyleInfo styleInfo = ((ExportConfigInfo)this.configInfo).getStyleInfo();

		// 字體名稱
		FontName font = styleInfo.getFont();
		if (tdInfo.getFont() != null) {
			font = tdInfo.getFont();
		} else if (trInfo.getFont() != null) {
			font = trInfo.getFont();
		}

		// 字體大小
		int size = 0;
		if (StringUtil.notEmpty(tdInfo.getSize())) {
			size = Integer.valueOf(StringUtil.safeTrim(tdInfo.getSize(), "0"));
		} else if (StringUtil.notEmpty(trInfo.getSize())) {
			size = Integer.valueOf(StringUtil.safeTrim(trInfo.getSize(), "0"));
		}
		if (size == 0) {
			size = Integer.valueOf(styleInfo.getSize());
		}

		// 粗體
		boolean isBold = ("true".equalsIgnoreCase(styleInfo.getBold()));
		if (StringUtil.notEmpty(tdInfo.getBold())) {
			isBold = ("true".equalsIgnoreCase(tdInfo.getBold()));
		} else if (StringUtil.notEmpty(trInfo.getBold())) {
			isBold = ("true".equalsIgnoreCase(trInfo.getBold()));
		}

		// 斜體
		boolean isItalic = ("true".equalsIgnoreCase(styleInfo.getItalic()));
		if (StringUtil.notEmpty(tdInfo.getItalic())) {
			isItalic = ("true".equalsIgnoreCase(tdInfo.getItalic()));
		} else if (StringUtil.notEmpty(trInfo.getBold())) {
			isItalic = ("true".equalsIgnoreCase(trInfo.getItalic()));
		}

		// 底線
		UnderlineStyle underlineStyle = styleInfo.getUnderline();
		if (tdInfo.getUnderline() != null) {
			underlineStyle = tdInfo.getUnderline();
		} else if (trInfo.getUnderline() != null) {
			underlineStyle = trInfo.getUnderline();
		}

		// 字體顏色
		Colour color = styleInfo.getColor();
		if (tdInfo.getColor() != null) {
			color = tdInfo.getColor();
		} else if (trInfo.getColor() != null) {
			color = trInfo.getColor();
		}

		// 水平位置
		Alignment align = styleInfo.getAlign();
		if (tdInfo.getAlign() != null) {
			align = tdInfo.getAlign();
		} else if (trInfo.getAlign() != null) {
			align = trInfo.getAlign();
		}

		// 垂直位置
		VerticalAlignment valign = styleInfo.getValign();
		if (tdInfo.getValign() != null) {
			valign = tdInfo.getValign();
		} else if (trInfo.getValign() != null) {
			valign = trInfo.getValign();
		}

		// 文字換行
		boolean isTextWrap = ("true".equalsIgnoreCase(styleInfo.getWrap()));
		if (StringUtil.notEmpty(tdInfo.getWrap())) {
			isTextWrap = ("true".equalsIgnoreCase(tdInfo.getWrap()));
		} else if (StringUtil.notEmpty(trInfo.getWrap())) {
			isTextWrap = ("true".equalsIgnoreCase(trInfo.getWrap()));
		}

		// 邊線位置
		Border borderSide = styleInfo.getBorderSide();
		if (tdInfo.getBorderSide() != null) {
			borderSide = tdInfo.getBorderSide();
		} else if (trInfo.getBorderSide() != null) {
			borderSide = trInfo.getBorderSide();
		}

		// 邊線樣式
		BorderLineStyle borderStyle = styleInfo.getBorderStyle();
		if (tdInfo.getBorderStyle() != null) {
			borderStyle = tdInfo.getBorderStyle();
		} else if (trInfo.getValign() != null) {
			borderStyle = trInfo.getBorderStyle();
		}

		// 背景顏色
		Colour background = styleInfo.getBackground();
		if (tdInfo.getBackground() != null) {
			background = tdInfo.getBackground();
		} else if (trInfo.getBackground() != null) {
			background = trInfo.getBackground();
		}

		// 產生字型設定
		WritableFont writableFont = new WritableFont(
				font,
				size,
				(isBold) ? WritableFont.BOLD : WritableFont.NO_BOLD,
				isItalic,
				underlineStyle,
				color);

		// 資料列cell格式
		WritableCellFormat writableCellFormat = new WritableCellFormat(writableFont);
		// 水平置中
		writableCellFormat.setAlignment(align);
		// 垂直置中
		writableCellFormat.setVerticalAlignment(valign);
		// 換行
		writableCellFormat.setWrap(isTextWrap);
		// 背景顏色
		writableCellFormat.setBackground(background);
		// 邊線
		writableCellFormat.setBorder(borderSide, borderStyle);

		return writableCellFormat;
	}

	// ===========================================================================
	// 參數預設值區
	// ===========================================================================
	/**
	 * 產生測試用的資料
	 * @return
	 */
	public ExportDataSet perpareTestData() {

		// ExportDataSet
		ExportDataSet exportDataSet = new ExportDataSet();
		// Detail DataSet List
		List<ColumnDataSet> detailDataSetList = new ArrayList<ColumnDataSet>();
		exportDataSet.setDetailDataSet("detail", detailDataSetList);

		// ==============================================
		// 外框資料
		// ==============================================
		String[] schoolNames = new String[]{"莊敬高職","亞東","東南","城市科大","致理","耕莘","康寧"};
		for (String schoolName : schoolNames) {
			Map<String, Object> columnData = new HashMap<String, Object>();
			columnData.put("SCHOOL_CHN_NAME", schoolName);
			columnData.put("EMPLOYMENT_RATE", "100.00%");
			detailDataSetList.add(this.perpareTesDataDetail(columnData));
		}
		return exportDataSet;
	}

	/**
	 * @param dataMap
	 */
	private ColumnDataSet perpareTesDataDetail(Map<String, Object> columnData) {

		// ColumnDataSet
		ColumnDataSet columnDataSet = new ColumnDataSet();
		columnDataSet.setColumnDataMap(columnData);

		// ==============================================
		// 年度資料
		// ==============================================
		List<Map<String, Object>> yearDataList = new ArrayList<Map<String, Object>>();

		Map<String, Object> yearDataMap98 = new LinkedHashMap<String, Object>();
		yearDataList.add(yearDataMap98);
		yearDataMap98.put("YEAR", 98);
		yearDataMap98.put("1", 4);
		yearDataMap98.put("2", 1);
		yearDataMap98.put("3-1", 6);
		yearDataMap98.put("3-2", 0);
		yearDataMap98.put("3-3", 12);
		yearDataMap98.put("3-4", 0);
		yearDataMap98.put("3-5", 0);
		yearDataMap98.put("4", 1);
		yearDataMap98.put("5", 0);
		yearDataMap98.put("6", 0);
		yearDataMap98.put("7-1", 0);
		yearDataMap98.put("7-2", 0);
		yearDataMap98.put("7-3", 0);
		yearDataMap98.put("8", 24);

		Map<String, Object> yearDataMap99 = new LinkedHashMap<String, Object>();
		yearDataList.add(yearDataMap99);
		yearDataMap99.put("YEAR", 99);
		yearDataMap99.put("1", 5);
		yearDataMap99.put("2", 0);
		yearDataMap99.put("3-1", 0);
		yearDataMap99.put("3-2", 6);
		yearDataMap99.put("3-3", 0);
		yearDataMap99.put("3-4", 12);
		yearDataMap99.put("3-5", 0);
		yearDataMap99.put("4", 0);
		yearDataMap99.put("5", 0);
		yearDataMap99.put("6", 1);
		yearDataMap99.put("7-1", 0);
		yearDataMap99.put("7-2", 0);
		yearDataMap99.put("7-3", 0);
		yearDataMap99.put("8", 24);

		columnDataSet.setArray("yearDataList", yearDataList);

		// ==============================================
		// 小計
		// ==============================================
		Map<String, Object> singleData = new HashMap<String, Object>();
		singleData.put("1", 9);
		singleData.put("2", 1);
		singleData.put("3", 36);
		singleData.put("4", 1);
		singleData.put("5", 0);
		singleData.put("6", 1);
		singleData.put("7", 0);
		singleData.put("8", 48);
		columnDataSet.setSingle("countMap", singleData);

		return columnDataSet;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String configFile = "C:/workspace/RbtProject/srcExcelOpt/excel-operate-export.xml";
		String configID = "001";
		String exportFile = "H:/0729/測試.xls";

		ExcelExporter excelExporter = new ExcelExporter();
		FileOutputStream out = null;

		try {
			//
			ExportConfigInfo exportConfigInfo = new ExportConfigReader().read(configFile, configID);
			out = new FileOutputStream(new File(exportFile));
			excelExporter.export(exportConfigInfo, excelExporter.perpareTestData(), out);
			// System.out.println(new BeanUtil().showContent(excelExporter.perpareTestData()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
