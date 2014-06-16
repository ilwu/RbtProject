package com.rbt.util.exceloperate.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import jxl.format.PaperSize;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import com.rbt.util.BeanUtil;
import com.rbt.util.StringUtil;
import com.rbt.util.exceloperate.Constant;
import com.rbt.util.exceloperate.bean.expt.config.ColumnDetailInfo;
import com.rbt.util.exceloperate.bean.expt.config.ColumnInfo;
import com.rbt.util.exceloperate.bean.expt.config.ContextInfo;
import com.rbt.util.exceloperate.bean.expt.config.DetailInfo;
import com.rbt.util.exceloperate.bean.expt.config.ExportConfigInfo;
import com.rbt.util.exceloperate.bean.expt.config.SheetlInfo;
import com.rbt.util.exceloperate.bean.expt.config.StyleInfo;
import com.rbt.util.exceloperate.bean.expt.config.TdInfo;
import com.rbt.util.exceloperate.bean.expt.config.TrInfo;
import com.rbt.util.exceloperate.exception.ExcelOperateException;

/**
 * 讀取設定資訊
 * @author Allen
 */
public class ExportConfigReader extends BaseConfigReader{

	/**
	 * 依據ID讀取設定檔案
	 * @param configFilePath 設定檔案完整路徑
	 * @param id 設定資訊 ID
	 * @return
	 * @throws ExcelOperateException
	 * @throws DocumentException
	 */
	public ExportConfigInfo read(String configFilePath, String id) throws ExcelOperateException, DocumentException {

		// =========================================================
		// 讀取設定檔案
		// =========================================================
		Document document = this.readConfigFile(configFilePath);

		// =========================================================
		// 讀取 ExportConfigInfo
		// =========================================================
		ExportConfigInfo exportConfigInfo = this.readExcelInfo(document, id);

		// =========================================================
		// 讀取 FormatInfo
		// =========================================================
		exportConfigInfo.setFunctionInfoMap(this.readFunctionInfo(document));

		return exportConfigInfo;
	}

	/**
	 * ExportConfigInfo
	 * @param document
	 * @param id
	 * @return
	 * @throws ExcelOperateException
	 */
	private ExportConfigInfo readExcelInfo(Document document, String id) throws ExcelOperateException {

		// =========================================================
		// 讀取設定資訊
		// =========================================================
		Node excelNode = document.selectSingleNode("//" + Constant.ELEMENT_EXCEL + "[" + Constant.ATTRIBUTE_ID + "=\"" + id + "\"]");
		if (excelNode == null) {
			throw new ExcelOperateException("設定資訊:[" + id + "] 不存在!");
		}

		// =========================================================
		// 讀取 excelInfo
		// =========================================================
		ExportConfigInfo exportConfigInfo = new ExportConfigInfo();
		exportConfigInfo.setId(StringUtil.safeTrim(excelNode.valueOf(Constant.ATTRIBUTE_ID)));
		exportConfigInfo.setFileName(StringUtil.safeTrim(excelNode.valueOf(Constant.ATTRIBUTE_FILENAME)));
		exportConfigInfo.setPaperSize(this.getPaperSize(StringUtil.safeTrim(excelNode.valueOf(Constant.ATTRIBUTE_PAPERSIZE))));

		// =========================================================
		// 讀取 範圍 style 設定
		// =========================================================
		// 讀取 style node
		Node styleNode = excelNode.selectSingleNode(Constant.ELEMENT_STYLE);
		// 讀取屬性設定
		StyleInfo styleInfo = new StyleInfo();
		styleInfo.readStyleAttr(styleNode);
		// 將未設定的屬性，設為系統預設值
		styleInfo.setEmptyAttrToSystemDefault();
		// 放入 excelInfo
		exportConfigInfo.setStyleInfo(styleInfo);

		// =========================================================
		// 讀取 sheet 設定
		// =========================================================」
		List<Node> sheetNodeList = excelNode.selectNodes(Constant.ELEMENT_SHEET);

		// 記錄已使用的 dataId
		HashSet<String> dataIdSet = new HashSet<String>();
		// 逐筆讀取
		List<SheetlInfo> sheetList = new ArrayList<SheetlInfo>();
		for (Node sheetNode : sheetNodeList) {

			// sheet 基本資訊
			SheetlInfo sheetlInfo = new SheetlInfo();
			sheetlInfo.setId(StringUtil.safeTrim(sheetNode.valueOf(Constant.ATTRIBUTE_ID)));
			sheetlInfo.setSheetName(StringUtil.safeTrim(sheetNode.valueOf(Constant.ATTRIBUTE_SHEETNAME)));

			// sheet 以下的 part 設定
			LinkedHashMap<String, Object> partInfoMap = new LinkedHashMap<String, Object>();

			// 取得Node list
			List<Node> nodeList = sheetNode.selectNodes("(" + Constant.ELEMENT_CONTEXT + "|" + Constant.ELEMENT_DETAIL + ")");

			// 解析 node 設定
			for (int i = 0; i < nodeList.size(); i++) {
				// 取得 node
				Node partInfoNode = nodeList.get(i);
				// 取得 dataId
				String dataId = StringUtil.safeTrim(partInfoNode.valueOf(Constant.ATTRIBUTE_DATAID));
				// 檢核 dataId 不可重複
				if (dataIdSet.contains(dataId)) {
					throw new ExcelOperateException(" <sheet> 標籤下的 (context|detail) 標籤, dataId 不可重複! [" + dataId + "] (取用 ExportDataSet 中資料時會造成異常)");
				}
				dataIdSet.add(dataId);

				// 依據標籤類型，進行解析
				if (Constant.ELEMENT_CONTEXT.equals(partInfoNode.getName())) {
					ContextInfo contextInfo = new ContextInfo();
					contextInfo.setDataId(dataId);
					contextInfo.setTrInfoList(this.readContextInfo(partInfoNode));
					partInfoMap.put(partInfoNode.getName() + "_" + i, contextInfo);
				} else if (Constant.ELEMENT_DETAIL.equals(partInfoNode.getName())) {
					if (partInfoNode.selectNodes(Constant.ELEMENT_COLUMN).size() < 1) {
						throw new ExcelOperateException("<detail> 標籤下, 不可無 <column> 設定!");
					}
					DetailInfo detailInfo = new DetailInfo();
					detailInfo.setDataId(dataId);
					detailInfo.setColumnInfoList(this.readDetailInfo(partInfoNode));
					partInfoMap.put(partInfoNode.getName() + "_" + i, detailInfo);
				}
			}
			//
			sheetlInfo.setPartInfoMap(partInfoMap);

			// 放入List
			sheetList.add(sheetlInfo);
		}

		// 放入 excelInfo
		exportConfigInfo.setSheetList(sheetList);

		return exportConfigInfo;

	}

	/**
	 * 解析 sheet 層的設定
	 * @param sheetNode sheet node
	 * @param sheetNodeName 子項目名稱
	 * @return
	 * @throws Exception
	 */
	private List<ColumnInfo> readDetailInfo(Node partNode) throws ExcelOperateException {

		List<ColumnInfo> columnInfoList = new ArrayList<ColumnInfo>();

		// 取得 column node list
		List<Node> columnList = partNode.selectNodes(Constant.ELEMENT_COLUMN);

		for (Node columnNode : columnList) {
			ColumnInfo columnInfo = new ColumnInfo();
			// 讀取資料元素設定
			columnInfo.readDataColumnAttr(columnNode);
			// 讀取 style 屬性設定
			columnInfo.readStyleAttr(columnNode);

			// 取得子元素list
			List<Node> columnDeatilNodList = columnNode.selectNodes("(" + Constant.ELEMENT_ARRAY + "|" + Constant.ELEMENT_SINGLE + ")");

			// column(array|single)[遞迴]
			List<ColumnDetailInfo> columnDetailInfoList = new ArrayList<ColumnDetailInfo>();
			for (Node columnDetailNode : columnDeatilNodList) {
				ColumnDetailInfo columnDetailInfo = new ColumnDetailInfo();
				// type
				columnDetailInfo.setType(columnDetailNode.getName());
				// dataId
				columnDetailInfo.setDataId(StringUtil.safeTrim(columnDetailNode.valueOf(Constant.ATTRIBUTE_DATAID)));
				// column
				if (columnDetailNode.selectNodes(Constant.ELEMENT_COLUMN).size() > 0) {
					columnDetailInfo.setColumnInfoList(this.readDetailInfo(columnDetailNode));
				}
				// add to list
				columnDetailInfoList.add(columnDetailInfo);
			}
			columnInfo.setColumnDetailInfoList(columnDetailInfoList);

			columnInfoList.add(columnInfo);
		}
		return columnInfoList;
	}

	/**
	 * 解析 sheet 層的設定
	 * @param sheetNode sheet node
	 * @param sheetNodeName 子項目名稱
	 * @return
	 * @throws Exception
	 */
	private List<TrInfo> readContextInfo(Node partNode) throws ExcelOperateException {

		List<TrInfo> trInfoList = new ArrayList<TrInfo>();

		// 未設定時返回
		if (partNode == null) {
			return trInfoList;
		}

		// 取得 tr node list
		List<Node> trNodeList = partNode.selectNodes(Constant.ELEMENT_TR);

		// 未設定時返回
		if (trNodeList == null || trNodeList.size() == 0) {
			return trInfoList;
		}

		for (Node trNode : trNodeList) {

			TrInfo trInfo = new TrInfo();
			// 讀取 style 屬性設定
			trInfo.readStyleAttr(trNode);
			// 取得 TD 設定 list 設定
			List<Node> tdNodeList = trNode.selectNodes(Constant.ELEMENT_TD);
			// 檢核
			if (tdNodeList == null || tdNodeList.size() == 0) {
				throw new ExcelOperateException("<tr> 標籤下, 不可無 <td> 設定!");
			}

			// 取得TD 設定
			List<TdInfo> tdInfoList = new ArrayList<TdInfo>();
			for (Node tdNode : tdNodeList) {
				TdInfo tdInfo = new TdInfo();

				// 讀取rowspan 屬性 (TdInfo 獨有)
				tdInfo.setRowspan(Integer.valueOf(StringUtil.safeTrim(tdNode.valueOf(Constant.ATTRIBUTE_ROWSPAN), "0")));

				// 讀取資料元素設定
				tdInfo.readDataColumnAttr(tdNode);

				// 讀取 style 屬性設定
				tdInfo.readStyleAttr(tdNode);

				tdInfoList.add(tdInfo);
			}
			trInfo.setTdInfoList(tdInfoList);
			trInfoList.add(trInfo);
		}

		return trInfoList;
	}

	/**
	 * 轉換頁面大小
	 * @param paperSize
	 * @return
	 */
	private PaperSize getPaperSize(String paperSize) {
		if ("A2".equalsIgnoreCase(paperSize)) {
			return PaperSize.A2;
		} else if ("A3".equalsIgnoreCase(paperSize)) {
			return PaperSize.A3;
		} else if ("A4".equalsIgnoreCase(paperSize)) {
			return PaperSize.A4;
		} else if ("A5".equalsIgnoreCase(paperSize)) {
			return PaperSize.A5;
		} else if ("B4".equalsIgnoreCase(paperSize)) {
			return PaperSize.B4;
		} else if ("B5".equalsIgnoreCase(paperSize)) {
			return PaperSize.B5;
		}
		return PaperSize.A4;
	}

	public static void main(String[] args) throws ExcelOperateException, DocumentException {
		ExportConfigInfo info =
				new ExportConfigReader().read("C:/workspace/elite/WebContent/WEB-INF/platformconfig/excel-operate-export.xml", "08-01-01-01");

		System.out.println(new BeanUtil().showContent(info));
	}
}
