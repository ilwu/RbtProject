/**
 *
 */
package com.rbt.util.db.orcalediff;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.rbt.util.StringUtil;
import com.rbt.util.db.AbstractDBUtil;
import com.rbt.util.db.RbtDbUtilImpl;
import com.rbt.util.db.TableUtil;
import com.rbt.util.file.FileUtil;
import com.rbt.util.file.PathUtil;

/**
 * @author Allen
 *
 */
public class OracleDiff {

	private String testTable = "";

	// private String testTable = "ALBUM";

	public OracleDiff(){

	}


	/**
	 * @throws Exception
	 *
	 */
	public OracleDiff(
			AbstractDBUtil sourceDB, String sourceSchema,
			AbstractDBUtil targetDB, String targetSchema,
			String resultFilePath,
			String resultFileName) throws Exception {

		FileUtil fu = new FileUtil();

		// ==============================================================
		// 取得資料庫的 Schema 資訊
		// ==============================================================
		// 取得來源的table Schema 資料
		System.out.println("取得來源資料庫 Schema..");
		LinkedHashMap<String, TableInfo> sourceTableInfoMap = this.getAllTable(sourceDB, sourceSchema);
		// System.out.println(new BeanUtil().showContent(sourceTableInfoMap));
		// 取得來源的table Schema 資料
		System.out.println("取得目標資料庫 Schema..");
		LinkedHashMap<String, TableInfo> targetTableInfoMap = this.getAllTable(targetDB, targetSchema);
		System.out.println("比對中..");

		// ==============================================================
		// 比對缺少的 TABLE
		// ==============================================================
		boolean firstAddFlag = true;

		for (String tableName : sourceTableInfoMap.keySet()) {
			if (!targetTableInfoMap.containsKey(tableName)) {
				System.out.println("目標缺少 TABLE :[" + tableName + "]");

				if (firstAddFlag) {
					fu.addLine("/*==============================================================*/");
					fu.addLine("/* 新增 Table*/");
					fu.addLine("/*==============================================================*/");
					firstAddFlag = false;
				}

				// 產生 create sql
				TableInfo sourceTableInfo = sourceTableInfoMap.get(tableName);
				fu.addLine(this.ganCreateSQL(targetSchema, tableName, sourceTableInfo.getColumnDataMapList(), sourceTableInfo.getpKeySet()));
			}
		}

		// ==============================================================
		// 各 table 欄位比對
		// ==============================================================
		for (String tableName : sourceTableInfoMap.keySet()) {
			// 目標無此 table 時跳過
			if (!targetTableInfoMap.containsKey(tableName)) {
				continue;
			}

			// 取得來源 TableInfo
			TableInfo sourceTableInfo = sourceTableInfoMap.get(tableName);
			// 取得目標 TableInfo
			TableInfo targetTableInfo = targetTableInfoMap.get(tableName);

			// 新增欄位
			String addSql = this.diffAddColumn(targetSchema, tableName, sourceTableInfo.getColumnDataMapList(), targetTableInfo.getColumnNameSet());

			// 修改欄位
			String modifySql = this.diffModifyColumn(targetSchema, tableName, sourceTableInfo.getColumnDataMapByColName(), targetTableInfo.getColumnDataMapByColName());

			if (StringUtil.notEmpty(addSql) || StringUtil.notEmpty(modifySql)) {
				fu.addLine("/*==============================================================*/");
				fu.addLine("/* 異動 Table : " + tableName + " */");
				fu.addLine("/*==============================================================*/");
				fu.addLine(addSql);
				fu.addLine(modifySql);
			}

		}

		// ==============================================================
		// 反向比對
		// ==============================================================
		FileUtil reFu = new FileUtil();
		for (String tableName : targetTableInfoMap.keySet()) {
			// 比對缺少TABLE
			if (!sourceTableInfoMap.containsKey(tableName)) {
				reFu.addLine("--來源缺少 TABLE :[" + tableName + "]");
				continue;
			}
			// 比對缺少欄位
			// 取得來源 TableInfo
			TableInfo sourceTableInfo = sourceTableInfoMap.get(tableName);
			// 取得目標 TableInfo
			TableInfo targetTableInfo = targetTableInfoMap.get(tableName);

			for (String columnName : targetTableInfo.getColumnNameSet()) {
				if (!sourceTableInfo.getColumnNameSet().contains(columnName)) {
					reFu.addLine("--來源 TABLE " + tableName + " 缺少欄位 :[" + columnName + "]");
				}
			}
		}

		if (StringUtil.notEmpty(reFu.getContent())) {
			fu.addLine("/*==============================================================*/");
			fu.addLine("/* 反向比對差異部分 */");
			fu.addLine("/*==============================================================*/");
			fu.addLine(reFu.getContent());
		}

		fu.writeToFile(resultFilePath, resultFileName);
	}

	/**
	 * 比對新增欄位
	 * @param targetSchema
	 * @param tableName
	 * @param srcColumnDataList
	 * @param targetColumnSet
	 * @return
	 */
	private String diffAddColumn(String targetSchema, String tableName,
			List<LinkedHashMap<String, Object>> srcColumnDataList,
			LinkedHashSet<String> targetColumnSet) {

		FileUtil fu = new FileUtil();
		for (LinkedHashMap<String, Object> srcColumnInfo : srcColumnDataList) {

			String COLUMN_NAME = StringUtil.safeTrim(srcColumnInfo.get("COLUMN_NAME"));
			String DATA_TYPE = StringUtil.safeTrim(srcColumnInfo.get("DATA_TYPE"));
			String DATA_LENGTH = StringUtil.safeTrim(srcColumnInfo.get("DATA_LENGTH"));
			String DATA_PRECISION = StringUtil.safeTrim(srcColumnInfo.get("DATA_PRECISION"));
			String DATA_DEFAULT = StringUtil.safeTrim(srcColumnInfo.get("DATA_DEFAULT"));
			String NULLABLE = StringUtil.safeTrim(srcColumnInfo.get("NULLABLE"));
			String CHAR_LENGTH = StringUtil.safeTrim(srcColumnInfo.get("CHAR_LENGTH"));

			// 存在時略過
			if (targetColumnSet.contains(COLUMN_NAME)) {
				continue;
			}

			fu.addLine("ALTER TABLE " + targetSchema + "." + tableName + " ADD (" +
					this.getCloumSql(COLUMN_NAME, DATA_TYPE, DATA_LENGTH, DATA_PRECISION, DATA_DEFAULT, NULLABLE, CHAR_LENGTH) + ");");
		}

		String result = fu.getContent();
		if (StringUtil.notEmpty(result)) {
			result = "--新增欄位\r\n" + result;
		}

		return result;
	}

	private String diffModifyColumn(String targetSchema, String tableName,
			LinkedHashMap<String, LinkedHashMap<String, Object>> srcColumnInfoByColName,
			LinkedHashMap<String, LinkedHashMap<String, Object>> targetColumnInfoByColName) {

		FileUtil fu = new FileUtil();

		for (LinkedHashMap<String, Object> srcColumnInfo : srcColumnInfoByColName.values()) {
			// 欄位名稱
			String COLUMN_NAME = StringUtil.safeTrim(srcColumnInfo.get("COLUMN_NAME"));
			// 目標DB欄位資訊
			LinkedHashMap<String, Object> targetColumnInfo = targetColumnInfoByColName.get(COLUMN_NAME);
			if (targetColumnInfo == null) {
				continue;
			}

			FileUtil subFu = new FileUtil();

			// 比較欄位型態
			String srcDATA_TYPE = StringUtil.safeTrim(srcColumnInfo.get("DATA_TYPE"));
			String srcDATA_LENGTH = StringUtil.safeTrim(srcColumnInfo.get("DATA_LENGTH"));
			String srcDATA_PRECISION = StringUtil.safeTrim(srcColumnInfo.get("DATA_PRECISION"));
			String srcCHAR_LENGTH = StringUtil.safeTrim(srcColumnInfo.get("CHAR_LENGTH"));

			String targetDATA_TYPE = StringUtil.safeTrim(targetColumnInfo.get("DATA_TYPE"));
			String targetDATA_LENGTH = StringUtil.safeTrim(targetColumnInfo.get("DATA_LENGTH"));
			String targetDATA_PRECISION = StringUtil.safeTrim(targetColumnInfo.get("DATA_PRECISION"));
			String targetCHAR_LENGTH = StringUtil.safeTrim(targetColumnInfo.get("CHAR_LENGTH"));

			if (!srcDATA_TYPE.equals(targetDATA_TYPE) ||
					!srcDATA_LENGTH.equals(targetDATA_LENGTH) ||
					!srcDATA_PRECISION.equals(targetDATA_PRECISION) ||
					!srcCHAR_LENGTH.equals(targetCHAR_LENGTH)) {

				// ALTER TABLE TES.ALBUM MODIFY (MODTIME LONG RAW(13))
				if ("NVARCHAR2".equals(srcDATA_TYPE) &&
						"NVARCHAR2".equals(targetDATA_TYPE) &&
						srcCHAR_LENGTH.equals(targetCHAR_LENGTH)) {
					// 若型態為 NVARCHAR2 時，DATA_LENGTH 可能依據資料庫不同而異, 故只比對 CHAR_LENGTH

				} else {
					subFu.addLine("ALTER TABLE " +
							targetSchema + "." +
							tableName + " MODIFY (" + COLUMN_NAME +
							" " +
							this.getType(srcDATA_TYPE, srcDATA_LENGTH, srcDATA_PRECISION, srcCHAR_LENGTH)
							+ ");");
				}
			}

			// 比較nullable
			String srcNULLABLE = StringUtil.safeTrim(srcColumnInfo.get("NULLABLE"));
			String targetNULLABLE = StringUtil.safeTrim(targetColumnInfo.get("NULLABLE"));

			if (!srcNULLABLE.equals(targetNULLABLE)) {
				subFu.addLine(
						"ALTER TABLE " +
						targetSchema + "." +
						tableName +
						" MODIFY (" + COLUMN_NAME + " " + ("N".equals(srcNULLABLE) ? "NOT " : "") + "NULL);");
			}

			// 比較 DATA_DEFAULT
			String srcDATA_DEFAULT = StringUtil.safeTrim(srcColumnInfo.get("DATA_DEFAULT"));
			String targetDATA_DEFAULT = StringUtil.safeTrim(targetColumnInfo.get("DATA_DEFAULT"));

			if (!srcDATA_DEFAULT.equals(targetDATA_DEFAULT) && // 一般判斷
					!(this.isNULL(srcDATA_DEFAULT) && this.isNULL(srcDATA_DEFAULT)) // 特殊判斷, 將 NULL 字串也視為空 (已經設過 DEFAULT NULL 者, 雖然DEFAULT 預設不塞, 但撈出來為為 NULL 字串)
			) {
				subFu.addLine("ALTER TABLE " + targetSchema + "." + tableName + " MODIFY (" + COLUMN_NAME + " DEFAULT " + (this.isNULL(srcDATA_DEFAULT) ? "NULL " : srcDATA_DEFAULT) + ");");
			}

			//
			if (StringUtil.notEmpty(subFu.getContent())) {
				fu.addLine("--異動前:[" + this.getCloumSql(COLUMN_NAME, targetDATA_TYPE, targetDATA_LENGTH, targetDATA_PRECISION, targetDATA_DEFAULT, targetNULLABLE, targetCHAR_LENGTH) + "]");
				fu.addLine("--異動後:[" + this.getCloumSql(COLUMN_NAME, srcDATA_TYPE, srcDATA_LENGTH, srcDATA_PRECISION, srcDATA_DEFAULT, srcNULLABLE, srcCHAR_LENGTH) + "]");
				fu.addLine(subFu.getContent());
			}
		}

		String result = fu.getContent();
		if (StringUtil.notEmpty(result)) {
			result = "--修改\r\n" + result;
		}
		return result;
	}

	private boolean isNULL(String value) {
		if (StringUtil.isEmpty(value)) {
			return true;
		}
		if ("NULL".equals(StringUtil.safeTrim(value))) {
			return true;
		}
		return false;
	}

	/**
	 * 產生 Create Table 的 SQL
	 * @param targetSchema
	 * @param tableName
	 * @param columnDataList
	 * @param pKeySet
	 * @return
	 */
	private String ganCreateSQL(String targetSchema, String tableName, List<LinkedHashMap<String, Object>> columnDataList, LinkedHashSet<String> pKeySet) {
		FileUtil fu = new FileUtil();
		String TABLE_COMMENTS = "";

		fu.addLine("CREATE TABLE " + targetSchema + "." + tableName + " ");
		fu.addLine("( ");
		// 一般欄位
		for (int i = 0; i < columnDataList.size(); i++) {
			LinkedHashMap<String, Object> columnData = columnDataList.get(i);
			TABLE_COMMENTS = StringUtil.safeTrim(columnData.get("TABLE_COMMENTS"));
			String COLUMN_NAME = StringUtil.safeTrim(columnData.get("COLUMN_NAME"));
			String DATA_TYPE = StringUtil.safeTrim(columnData.get("DATA_TYPE"));
			String DATA_LENGTH = StringUtil.safeTrim(columnData.get("DATA_LENGTH"));
			String DATA_PRECISION = StringUtil.safeTrim(columnData.get("DATA_PRECISION"));
			String DATA_DEFAULT = StringUtil.safeTrim(columnData.get("DATA_DEFAULT"));
			String NULLABLE = StringUtil.safeTrim(columnData.get("NULLABLE"));
			String CHAR_LENGTH = StringUtil.safeTrim(columnData.get("CHAR_LENGTH"));

			fu.addStr("	" + this.getCloumSql(COLUMN_NAME, DATA_TYPE, DATA_LENGTH, DATA_PRECISION, DATA_DEFAULT, NULLABLE, CHAR_LENGTH));

			if (i != columnDataList.size() ||
					(i == columnDataList.size() && pKeySet.size() > 0)) {
				fu.addStr(",");
			}
			fu.addLine("");
		}

		// PK
		if (pKeySet.size() > 0) {
			fu.addStr("	CONSTRAINT PK_" + tableName + " PRIMARY KEY (");
			String pkStr = "";
			for (String pkey : pKeySet) {
				pkStr += "," + pkey;
			}
			fu.addStr(pkStr.substring(1));
			fu.addStr(")");
			fu.addLine("");
		}else{

		}

		fu.addLine(");");
		fu.addLine("");

		// 註解
		fu.addLine("COMMENT ON TABLE " + targetSchema + "." + tableName + " IS '" + TABLE_COMMENTS + "';");

		for (LinkedHashMap<String, Object> columnData : columnDataList) {
			String COLUMN_NAME = StringUtil.safeTrim(columnData.get("COLUMN_NAME"));
			String COMMENTS = StringUtil.safeTrim(columnData.get("COMMENTS"));

			fu.addStr("COMMENT ON COLUMN ");
			fu.addStr(targetSchema + ".");
			fu.addStr(tableName + ".");
			fu.addStr(COLUMN_NAME);
			fu.addStr(" IS ");
			fu.addStr("'" + COMMENTS + "';");
			fu.addLine();
		}

		return fu.getContent();

	}

	/**
	 * 產生欄位建置 SQL
	 * @param COLUMN_NAME
	 * @param DATA_TYPE
	 * @param DATA_LENGTH
	 * @param DATA_PRECISION
	 * @param DATA_DEFAULT
	 * @param NULLABLE
	 * @return
	 */
	private String getCloumSql(String COLUMN_NAME, String DATA_TYPE, String DATA_LENGTH, String DATA_PRECISION, String DATA_DEFAULT, String NULLABLE, String CHAR_LENGTH) {
		String str = "";

		str += COLUMN_NAME;
		str += "	" + this.getType(DATA_TYPE, DATA_LENGTH, DATA_PRECISION, CHAR_LENGTH);
		if (StringUtil.notEmpty(DATA_DEFAULT)) {
			str += "	DEFAULT	" + DATA_DEFAULT;
		}
		if ("N".equals(NULLABLE)) {
			str += "	NOT NULL";
		}
		return str;
	}

	/**
	 * @param DATA_TYPE
	 * @param DATA_LENGTH
	 * @param DATA_PRECISION
	 * @return
	 */
	private String getType(String DATA_TYPE, String DATA_LENGTH, String DATA_PRECISION, String CHAR_LENGTH) {
		if ("NUMBER".equals(DATA_TYPE.toUpperCase())) {
			if (StringUtil.isEmpty(DATA_PRECISION)) {
				return "NUMBER";
			}
			return "NUMBER(" + DATA_PRECISION + ")";
		}

		if ("NVARCHAR2".equals(DATA_TYPE.toUpperCase())) {
			return DATA_TYPE + "(" + CHAR_LENGTH + ")";
		}

		if ("NVARCHAR2".equals(DATA_TYPE.toUpperCase())) {
			return DATA_TYPE + "(" + CHAR_LENGTH + ")";
		}

		//為DATE時，無須長度
		if ("DATE".equals(DATA_TYPE.toUpperCase())) {
			return "DATE";
		}

		//BLOB
		if ("BLOB".equals(DATA_TYPE.toUpperCase())) {
			return "BLOB";
		}


		return DATA_TYPE + "(" + DATA_LENGTH + ")";
	}

	/**
	 * @param dbUtil
	 * @param schema
	 * @return
	 * @throws Exception
	 */
	public LinkedHashMap<String, TableInfo> getAllTable(AbstractDBUtil dbUtil, String schema) throws Exception {

		// =======================================================
		// 查詢 所有的欄位資訊
		// =======================================================
		// 兜組SQL
		StringBuffer columnsQSql = new StringBuffer();
		columnsQSql.append("SELECT col.OWNER, ");
		columnsQSql.append("	   col.TABLE_NAME, ");
		columnsQSql.append("	   tabComt.COMMENTS AS TABLE_COMMENTS, ");
		columnsQSql.append("	   col.COLUMN_ID, ");
		columnsQSql.append("	   col.COLUMN_NAME, ");
		columnsQSql.append("	   col.DATA_TYPE, ");
		columnsQSql.append("	   col.DATA_LENGTH, ");
		columnsQSql.append("	   col.DATA_PRECISION, ");
		columnsQSql.append("	   col.DATA_DEFAULT, ");
		columnsQSql.append("	   col.CHAR_LENGTH, ");
		columnsQSql.append("	   col.NULLABLE, ");
		columnsQSql.append("	   colComt.COMMENTS ");
		columnsQSql.append("FROM   ALL_TAB_COLUMNS col ");
		columnsQSql.append("	   JOIN ALL_TABLES tab ");
		columnsQSql.append("		 ON col.OWNER = tab.OWNER ");
		columnsQSql.append("			AND col.TABLE_NAME = tab.TABLE_NAME ");
		columnsQSql.append("	   LEFT JOIN ALL_COL_COMMENTS colComt ");
		columnsQSql.append("			  ON col.OWNER = colComt.Owner ");
		columnsQSql.append("				 AND col.TABLE_NAME = colComt.TABLE_NAME ");
		columnsQSql.append("				 AND col.COLUMN_NAME = colComt.COLUMN_NAME ");
		columnsQSql.append("	   LEFT JOIN sys.USER_TAB_COMMENTS tabComt ");
		columnsQSql.append("			  ON tabComt.TABLE_TYPE = 'TABLE' ");
		columnsQSql.append("				 AND tabComt.TABLE_NAME = col.TABLE_NAME ");
		columnsQSql.append("WHERE  col.OWNER = '" + schema + "' ");
		// 測試縮限範圍
		if (StringUtil.notEmpty(this.testTable)) {
			columnsQSql.append("AND  col.TABLE_NAME = '" + this.testTable + "' ");
		}
		columnsQSql.append("ORDER  BY col.TABLE_NAME, ");
		columnsQSql.append("		  col.COLUMN_ID ");

		// 查詢所有的欄位List
		List<LinkedHashMap<String, Object>> allColumnList = dbUtil.query(columnsQSql.toString());
		// 依據 table 分類
		ProcResult tableColumnProcResult = this.process(allColumnList);

		// =======================================================
		// 查詢 所有的欄位資訊
		// =======================================================
		StringBuffer pKeyQSql = new StringBuffer();
		pKeyQSql.append("SELECT C.OWNER, ");
		pKeyQSql.append("	   C.TABLE_NAME, ");
		pKeyQSql.append("	   D.POSITION, ");
		pKeyQSql.append("	   D.COLUMN_NAME ");
		pKeyQSql.append("FROM   ALL_CONSTRAINTS C ");
		pKeyQSql.append("	   JOIN ALL_CONS_COLUMNS D ");
		pKeyQSql.append("		 ON C.OWNER = D.OWNER ");
		pKeyQSql.append("			AND C.CONSTRAINT_NAME = D.CONSTRAINT_NAME ");
		pKeyQSql.append("WHERE  C.CONSTRAINT_TYPE = 'P' ");
		pKeyQSql.append("	   AND C.OWNER = '" + schema + "' ");
		// 測試縮限範圍
		if (StringUtil.notEmpty(this.testTable)) {
			columnsQSql.append("AND  C.TABLE_NAME = '" + this.testTable + "' ");
		}
		pKeyQSql.append("ORDER  BY C.TABLE_NAME, ");
		pKeyQSql.append("		  D.POSITION ");

		List<LinkedHashMap<String, Object>> allPKeyColumnList = dbUtil.query(pKeyQSql.toString());
		// 依據 table 分類
		ProcResult pkColumnProcResult = this.process(allPKeyColumnList);

		// =======================================================
		// 組 tableInfo
		// =======================================================
		LinkedHashMap<String, TableInfo> tableInfoMap = new LinkedHashMap<String, TableInfo>();

		for (String tableName : tableColumnProcResult.columnInfoListByTableName.keySet()) {
			TableInfo tableInfo = new TableInfo();
			// 一般欄位
			tableInfo.columnDataMapList = tableColumnProcResult.columnInfoListByTableName.get(tableName);
			tableInfo.columnDataMapByColName = tableColumnProcResult.columnInfoByColNameTableName.get(tableName);
			tableInfo.columnNameSet = tableColumnProcResult.columnNameSetByTableName.get(tableName);
			// PK欄位
			tableInfo.pKeyDataMapList = pkColumnProcResult.columnInfoListByTableName.get(tableName);
			tableInfo.pKeySet = pkColumnProcResult.columnNameSetByTableName.get(tableName);
			// PUT
			tableInfoMap.put(tableName, tableInfo);
		}
		return tableInfoMap;
	}

	private ProcResult process(List<LinkedHashMap<String, Object>> dataMapList) {

		// 依據 table 分類資料
		LinkedHashMap<String, List<LinkedHashMap<String, Object>>> columnInfoListByTableName = new LinkedHashMap();
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> columnInfoByColNameTableName = new LinkedHashMap();
		LinkedHashMap<String, LinkedHashSet<String>> columnNameSetByTableName = new LinkedHashMap();

		for (LinkedHashMap<String, Object> columnDataMap : dataMapList) {
			// TABLE NAME
			String TABLE_NAME = StringUtil.safeTrim(columnDataMap.get("TABLE_NAME"));
			// COLUMN_NAME
			String COLUMN_NAME = StringUtil.safeTrim(columnDataMap.get("COLUMN_NAME"));
			// 為空者略過
			if (StringUtil.isEmpty(TABLE_NAME)) {
				continue;
			}

			// 取得已收集的 List
			List<LinkedHashMap<String, Object>> columnDataMapList = columnInfoListByTableName.get(TABLE_NAME);
			LinkedHashMap<String, LinkedHashMap<String, Object>> columnDataMapByColName = columnInfoByColNameTableName.get(TABLE_NAME);
			LinkedHashSet<String> columnNameSet = columnNameSetByTableName.get(TABLE_NAME);
			// 還不存在時初始化
			if (columnDataMapList == null) {
				//
				columnDataMapList = new ArrayList<LinkedHashMap<String, Object>>();
				columnInfoListByTableName.put(TABLE_NAME, columnDataMapList);

				columnDataMapByColName = new LinkedHashMap();
				columnInfoByColNameTableName.put(TABLE_NAME, columnDataMapByColName);

				columnNameSet = new LinkedHashSet<String>();
				columnNameSetByTableName.put(TABLE_NAME, columnNameSet);

			}
			columnDataMapList.add(columnDataMap);
			columnDataMapByColName.put(COLUMN_NAME, columnDataMap);
			columnNameSet.add(COLUMN_NAME);
		}

		return new ProcResult(columnInfoListByTableName, columnInfoByColNameTableName, columnNameSetByTableName);
	}

	/**
	 * @author Allen
	 *
	 */
	public class ProcResult {
		//
		LinkedHashMap<String, List<LinkedHashMap<String, Object>>> columnInfoListByTableName;
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> columnInfoByColNameTableName;
		LinkedHashMap<String, LinkedHashSet<String>> columnNameSetByTableName;

		public ProcResult(
				LinkedHashMap<String, List<LinkedHashMap<String, Object>>> columnInfoListByTableName,
				LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> columnInfoByColNameTableName,
				LinkedHashMap<String, LinkedHashSet<String>> columnNameSetMapByTableName) {
			this.columnInfoListByTableName = columnInfoListByTableName;
			this.columnInfoByColNameTableName = columnInfoByColNameTableName;
			this.columnNameSetByTableName = columnNameSetMapByTableName;
		}
	}

	/**
	 * @author Allen
	 *
	 */
	public class TableInfo {
		public TableInfo() {
		}

		List<LinkedHashMap<String, Object>> columnDataMapList;
		LinkedHashMap<String, LinkedHashMap<String, Object>> columnDataMapByColName;
		LinkedHashSet<String> columnNameSet;
		List<LinkedHashMap<String, Object>> pKeyDataMapList;
		LinkedHashSet<String> pKeySet;

		/**
		 * @return the columnDataMapList
		 */
		public List<LinkedHashMap<String, Object>> getColumnDataMapList() {
			return this.columnDataMapList;
		}

		/**
		 * @param columnDataMapList the columnDataMapList to set
		 */
		public void setColumnDataMapList(List<LinkedHashMap<String, Object>> columnDataMapList) {
			this.columnDataMapList = columnDataMapList;
		}

		/**
		 * @return the pKeyDataMapList
		 */
		public List<LinkedHashMap<String, Object>> getpKeyDataMapList() {
			return this.pKeyDataMapList;
		}

		/**
		 * @param pKeyDataMapList the pKeyDataMapList to set
		 */
		public void setpKeyDataMapList(List<LinkedHashMap<String, Object>> pKeyDataMapList) {
			this.pKeyDataMapList = pKeyDataMapList;
		}

		/**
		 * @return the pKeySet
		 */
		public LinkedHashSet<String> getpKeySet() {
			if (this.pKeySet == null) {
				return new LinkedHashSet<String>();
			}
			return this.pKeySet;
		}

		/**
		 * @param pKeySet the pKeySet to set
		 */
		public void setpKeySet(LinkedHashSet<String> pKeySet) {
			this.pKeySet = pKeySet;
		}

		/**
		 * @return the columnDataMapByColName
		 */
		public LinkedHashMap<String, LinkedHashMap<String, Object>> getColumnDataMapByColName() {
			return this.columnDataMapByColName;
		}

		/**
		 * @param columnDataMapByColName the columnDataMapByColName to set
		 */
		public void setColumnDataMapByColName(LinkedHashMap<String, LinkedHashMap<String, Object>> columnDataMapByColName) {
			this.columnDataMapByColName = columnDataMapByColName;
		}

		/**
		 * @return the columnNameSet
		 */
		public LinkedHashSet<String> getColumnNameSet() {
			return this.columnNameSet;
		}

		/**
		 * @param columnNameSet the columnNameSet to set
		 */
		public void setColumnNameSet(LinkedHashSet<String> columnNameSet) {
			this.columnNameSet = columnNameSet;
		}

	}



	public static void mainFromTest(String[] args) throws Exception {

		RbtDbUtilImpl targetDbUtil  = new RbtDbUtilImpl(
				TableUtil.DRIVER_Oracle,
				"jdbc:oracle:thin:@10.100.2.1:1521:LABOR",
				"cla",
				"clalabor",
				5
				);

		RbtDbUtilImpl srcDbUtil = new RbtDbUtilImpl(
				TableUtil.DRIVER_Oracle,
				"jdbc:oracle:thin:@192.168.0.51:1521:evtadb1",
				"cla",
				"clalabor!QAZ",
				5
				);

		new OracleDiff(srcDbUtil, "CLA", targetDbUtil, "CLA", "h:/DB_DIFF/", "DB_DIFF_CLA(測試為主).sql");
	}


	/**
	 * @param args
	 * @throws Exception
	 */
	public static void mainCla(String[] args) throws Exception {

		RbtDbUtilImpl srcDbUtil = new RbtDbUtilImpl(
				TableUtil.DRIVER_Oracle,
				"jdbc:oracle:thin:@10.100.2.1:1521:LABOR",
				"cla",
				"clalabor",
				5
				);

		RbtDbUtilImpl targetDbUtil = new RbtDbUtilImpl(
				TableUtil.DRIVER_Oracle,
				"jdbc:oracle:thin:@192.168.0.51:1521:evtadb1",
				"cla",
				"clalabor!QAZ",
				5
				);

		new OracleDiff(srcDbUtil, "CLA", targetDbUtil, "CLA", "h:/DB_DIFF/", "DB_DIFF_CLA(正式為主).sql");
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void mainTES_T2E(String[] args) throws Exception {

		RbtDbUtilImpl targetDbUtil = new RbtDbUtilImpl(
		//RbtDbUtilImpl srcDbUtil = new RbtDbUtilImpl(
				TableUtil.DRIVER_Oracle,
				"jdbc:oracle:thin:@10.100.2.1:1521:LABOR",
				"tes",
				"teslabor",
				5
				);

		RbtDbUtilImpl srcDbUtil = new RbtDbUtilImpl(
		//RbtDbUtilImpl targetDbUtil = new RbtDbUtilImpl(
				TableUtil.DRIVER_Oracle,
				"jdbc:oracle:thin:@192.168.0.51:1521:evtadb1",
				"tes",
				"teslabor!QAZ",
				5
				);

		new OracleDiff(srcDbUtil, "TES", targetDbUtil, "TES", "h:/DB_DIFF/", "DB_DIFF_TES.sql");
	}

	/**
	 * 讀取設定檔 OracleDiff
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String resourceFileName = "OracleDiff";
		Locale currentLocale = Locale.getDefault();
		File resourceFile = new File(new PathUtil().getCurrentDirectory());
		URL resourceUrl = resourceFile.toURI().toURL();
		URL[] urls = { resourceUrl };
		ClassLoader loader = new URLClassLoader(urls);
		ResourceBundle bundle = ResourceBundle.getBundle(resourceFileName, currentLocale, loader);

		RbtDbUtilImpl srcDbUtil = new RbtDbUtilImpl(
				TableUtil.DRIVER_Oracle,
				"jdbc:oracle:thin:@" + bundle.getString("src.IP") + ":" + bundle.getString("src.PORT") + ":" + bundle.getString("src.SID") + "",
				bundle.getString("src.USERNAME"),
				bundle.getString("src.PASSWORD"),
				5
				);


		System.out.println("來源資料庫連線測試----");
		System.out.println(srcDbUtil.getConnectionInfo());
		try {
			srcDbUtil.getConnection();
			System.out.println("連線成功");
		} catch (Exception e) {
			System.out.println("連線失敗");
			System.out.println(StringUtil.getExceptionStackTrace(e));
			return;
		}

		RbtDbUtilImpl targetDbUtil = new RbtDbUtilImpl(
				TableUtil.DRIVER_Oracle,
				"jdbc:oracle:thin:@" + bundle.getString("target.IP") + ":" + bundle.getString("target.PORT") + ":" + bundle.getString("target.SID") + "",
				bundle.getString("target.USERNAME"),
				bundle.getString("target.PASSWORD"),
				5
				);

		System.out.println("目標資料庫連線測試----");
		System.out.println(targetDbUtil.getConnectionInfo());

		try {
			targetDbUtil.getConnection();
			System.out.println("連線成功");
		} catch (Exception e) {
			System.out.println("連線失敗");
			System.out.println(StringUtil.getExceptionStackTrace(e));
			return;
		}

		new OracleDiff(srcDbUtil, bundle.getString("src.SCHEMA"), targetDbUtil, bundle.getString("target.SCHEMA"), bundle.getString("result.PATH"), bundle.getString("result.FILENAME"));
	}
}
