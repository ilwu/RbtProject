package com.rbt.evaimport;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.rbt.util.StringUtil;
import com.rbt.util.db.RbtDbUtilImpl;
import com.rbt.util.db.SqlUtil;
import com.rbt.util.exceloperate.ExcelImporter;
import com.rbt.util.exceloperate.bean.impt.config.ImportConfigInfo;
import com.rbt.util.exceloperate.config.ImportConfigReader;
import com.rbt.util.file.FileUtil;

public abstract class BaseEvaImport {

	/**
	 * LOG4j
	 */
	protected Logger LOG = Logger.getLogger(this.getClass());

	/**
	 *
	 */
	protected EvaImportFunctionImpl function = new EvaImportFunctionImpl();

	/**
	 * 讀取檔案
	 * @param configFilePath 設定檔路徑
	 * @param configID 設定組 ID
	 * @param importFilePath 匯入檔案路徑
	 * @return
	 * @throws Exception
	 */
	public List<LinkedHashMap<String, Object>> readFile(
			String configFilePath,
			String configID,
			String importFilePath) throws Exception {

		ExcelImporter excelImporter = new ExcelImporter();
		FileInputStream is = null;
		Connection conn = null;

		try {
			RbtDbUtilImpl dbUtil = new RbtDbUtilImpl();
			conn = dbUtil.getConnection();

			// 讀取 config
			ImportConfigReader importConfigReader = new ImportConfigReader();
			ImportConfigInfo importConfigInfo = importConfigReader.read(configFilePath, configID);

			// 讀取 檔案
			is = new FileInputStream(new File(importFilePath));
			excelImporter.setConnection(conn);
			return excelImporter.read(is, importConfigInfo);

		} finally {
			if (conn != null) {
				conn.close();
			}
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * 產出 SQL
	 * @param fileUtil
	 * @param tableName
	 * @param descParamName
	 * @param dataList
	 */
	protected void prepareInsertSQL(FileUtil fileUtil, String tableName, String descParamName, List<LinkedHashMap<String, Object>> dataList){
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [" + tableName + "]");
		fileUtil.addLine("------------------------------------------------------");

		for (LinkedHashMap<String, Object> item : dataList) {
			fileUtil.addLine("--" + StringUtil.safeTrim(item.get(descParamName)));
			fileUtil.addLine(SqlUtil.genInsertSQL(tableName, item, true).replace(") VALUES (", ")\r\nVALUES (") + ";");
		}
		fileUtil.addLine();
	}

	/**
	 * 值為空白時轉全形
	 * @param value
	 * @return
	 */
	protected String trnsSpace(Object value) {
		if (StringUtil.isEmpty(value)) {
			return "　";
		}
		return StringUtil.safeTrim(value);
	}
}
