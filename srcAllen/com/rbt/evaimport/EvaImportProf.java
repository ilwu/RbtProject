package com.rbt.evaimport;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;

import com.rbt.util.StringUtil;
import com.rbt.util.db.RbtDbUtilImpl;
import com.rbt.util.db.SqlUtil;
import com.rbt.util.file.FileUtil;

/**
 * 學校資料處理
 * @author Allen
 */
public class EvaImportProf extends BaseEvaImport {

	/**
	 * TD_SCHOOL_YEAR_ES
	 */
	private List<LinkedHashMap<String, Object>> yearEsMapList;
	/**
	 * TD_SCHOOL_YEAR_ST
	 */
	private List<LinkedHashMap<String, Object>> yearStMapList;
	/**
	 * TD_SCHOOL_YEAR_INSTITUE
	 */
	private List<LinkedHashMap<String, Object>> yearInstMapList;
	/**
	 * TD_SCHOOL_YEAR_TEACHER
	 */
	private List<LinkedHashMap<String, Object>> yearTeacherMapList;
	/**
	 * TD_SCHOOL_YEAR_CORP
	 */
	private List<LinkedHashMap<String, Object>> yearCorpMapList;


	/**
	 * 讀取所有資料
	 * @param configFilePath
	 * @param importFilePath
	 * @throws Exception
	 */
	public void readAll(String configFilePath, String importFilePath) throws Exception {

		// =====================================
		// 讀檔
		// =====================================
		// 讀檔 TD_SCHOOL_YEAR_ES
		this.yearEsMapList = this.readFile(configFilePath, "TD_SCHOOL_YEAR_ES", importFilePath);
		// 讀檔 TD_SCHOOL_YEAR_ST
		this.yearStMapList = this.readFile(configFilePath, "TD_SCHOOL_YEAR_ST", importFilePath);
		// 讀檔 TD_SCHOOL_YEAR_INSTITUE
		this.yearInstMapList = this.readFile(configFilePath, "TD_SCHOOL_YEAR_INSTITUE", importFilePath);
		// 讀檔 TD_SCHOOL_YEAR_TEACHER 專班導師清單
		this.yearTeacherMapList = this.readFile(configFilePath, "TD_SCHOOL_YEAR_TEACHER", importFilePath);
		// 讀檔 TD_SCHOOL_YEAR_CORP 計畫申請學校年度合作事業單位檔
		this.yearCorpMapList = this.readFile(configFilePath, "TD_SCHOOL_YEAR_CORP", importFilePath);

		// =====================================
		// 引索
		// =====================================
		// TD_SCHOOL_YEAR_ES
		LinkedHashMap<String, LinkedHashMap<String, Object>> yearEsMap =
				new LinkedHashMap<String, LinkedHashMap<String, Object>>();

		for (LinkedHashMap<String, Object> item : this.yearEsMapList) {
			String key = this.getKey(item);
			yearEsMap.put(key, item);
		}

		// TD_SCHOOL_YEAR_ES
		LinkedHashMap<String, LinkedHashMap<String, Object>> yearStMap =
				new LinkedHashMap<String, LinkedHashMap<String, Object>>();

		for (LinkedHashMap<String, Object> item : this.yearStMapList) {
			String key = this.getKey(item);
			yearStMap.put(key, item);
		}

		// =====================================
		// TD_SCHOOL_YEAR_ES 重整
		// =====================================
		for (LinkedHashMap<String, Object> item : this.yearEsMapList) {
			this.removeUnUse(item);
		}

		// =====================================
		// TD_SCHOOL_YEAR_ST 重整
		// =====================================
		for (LinkedHashMap<String, Object> item : this.yearStMapList) {
			LinkedHashMap<String, Object> yearEsItem = yearEsMap.get(this.getKey(item));
			if (yearEsItem == null) {
				throw new Exception("TD_SCHOOL_YEAR_ST 無法對照 TD_SCHOOL_YEAR_ES 資料");
			}
			item.put("SCHOOL_YEAR_ES_UID", yearEsItem.get("SCHOOL_YEAR_ES_UID"));
			//移除無用欄位
			this.removeUnUse(item);
		}

		// =====================================
		// TD_SCHOOL_YEAR_INSTITUE 重整
		// =====================================
		for (LinkedHashMap<String, Object> item : this.yearInstMapList) {
			LinkedHashMap<String, Object> yearStItem = yearStMap.get(this.getKey(item));
			if (yearStItem == null) {
				throw new Exception("TD_SCHOOL_YEAR_INSTITUE 無法對照 TD_SCHOOL_YEAR_ST 資料");
			}
			item.put("SCHOOL_YEAR_ST_UID", yearStItem.get("SCHOOL_YEAR_ST_UID"));
			this.removeUnUse(item);
		}

		// =====================================
		// TD_SCHOOL_YEAR_TEACHER 重整
		// =====================================
		for (LinkedHashMap<String, Object> item : this.yearTeacherMapList) {
			LinkedHashMap<String, Object> yearStItem = yearStMap.get(this.getKey(item));
			if (yearStItem == null) {
				throw new Exception("TD_SCHOOL_YEAR_TEACHER 無法對照 TD_SCHOOL_YEAR_ST 資料 :[" + this.getKey(item) + "]");
			}
			item.put("SCHOOL_YEAR_ES_UID", yearStItem.get("SCHOOL_YEAR_ES_UID"));
			item.put("SCHOOL_YEAR_ST_UID", yearStItem.get("SCHOOL_YEAR_ST_UID"));
			this.removeUnUse(item);
		}

		// =====================================
		// TD_SCHOOL_YEAR_CORP 重整
		// =====================================
		for (LinkedHashMap<String, Object> item : this.yearCorpMapList) {
			LinkedHashMap<String, Object> yearStItem = yearStMap.get(this.getKey(item));
			if (yearStItem == null) {
				for (String key : yearStMap.keySet()) {
					System.out.println("[" + key + "]");
				}
				throw new Exception("TD_SCHOOL_YEAR_CORP 無法對照 TD_SCHOOL_YEAR_ST 資料 :[" + this.getKey(item) + "]");
			}
			item.put("SCHOOL_YEAR_ST_UID", yearStItem.get("SCHOOL_YEAR_ST_UID"));
			this.removeUnUseWhitOutUnitInfoUid(item);
		}


		// System.out.println(new BeanUtil().showContent("TD_SCHOOL_YEAR_ES", this.yearEsMapList));
		// System.out.println(new BeanUtil().showContent("TD_SCHOOL_YEAR_ST", this.yearStMapList));
		// System.out.println(new BeanUtil().showContent("TD_SCHOOL_YEAR_INSTITUE", this.yearInstMapList));
	}


	private String getKey(LinkedHashMap<String, Object> item) {
		String key = StringUtil.safeTrim(item.get("UNIT_INFO_UID")) + "_";
		key += StringUtil.safeTrim(item.get("SCHOOL_ID")) + "_";
		key += StringUtil.safeTrim(item.get("ST_TYPE")) + "_";
		key += StringUtil.safeTrim(item.get("INSTITUE_NAME"));
		return key;
	}

	/**
	 * 移除無用欄位
	 * @param item
	 */
	private LinkedHashMap<String, Object> removeUnUse(LinkedHashMap<String, Object> item) {
		// 移除欄位
		item.remove("UNIT_INFO_UID");
		item.remove("SCHOOL_ID");
		item.remove("ST_TYPE");
		item.remove("INSTITUE_NAME");
		return item;
	}


	/**
	 * 移除無用欄位
	 * @param item
	 */
	private LinkedHashMap<String, Object> removeUnUseWhitOutUnitInfoUid(LinkedHashMap<String, Object> item) {
		// 移除欄位
		item.remove("SCHOOL_ID");
		item.remove("ST_TYPE");
		item.remove("INSTITUE_NAME");
		return item;
	}

	/**
	 * 讀取後直接 insert 到 DB
	 * @param configFilePath 設定檔路徑
	 * @param configID 設定組 ID
	 * @param importFilePath 匯入檔案路徑
	 * @throws Exception
	 */
	public void readAndInsert(String configFilePath, String importFilePath) throws Exception {

		// =================================================
		// 讀檔
		// =================================================
		this.readAll(configFilePath, importFilePath);

		// =================================================
		// INSERT
		// =================================================
		RbtDbUtilImpl dbUtil = new RbtDbUtilImpl();
		Connection conn = null;

		try {

			// 取得 Connection
			conn = dbUtil.getConnection();
			conn.setAutoCommit(false);

			// Insert
			for (LinkedHashMap<String, Object> item : this.yearEsMapList) {
				System.out.println("INSERT [TD_SCHOOL_YEAR_ES]");
				dbUtil.insert(conn, "TD_SCHOOL_YEAR_ES", item);
			}
			for (LinkedHashMap<String, Object> item : this.yearStMapList) {
				System.out.println("INSERT [TD_SCHOOL_YEAR_ST]");
				dbUtil.insert(conn, "TD_SCHOOL_YEAR_ST", item);
			}
			for (LinkedHashMap<String, Object> item : this.yearInstMapList) {
				System.out.println("INSERT [TD_SCHOOL_YEAR_INSTITUE]");
				dbUtil.insert(conn, "TD_SCHOOL_YEAR_INSTITUE", item);
			}
			for (LinkedHashMap<String, Object> item : this.yearTeacherMapList) {
				System.out.println("INSERT [TD_SCHOOL_YEAR_TEACHER]");
				dbUtil.insert(conn, "TD_SCHOOL_YEAR_TEACHER", item);
			}
			for (LinkedHashMap<String, Object> item : this.yearCorpMapList) {
				System.out.println("INSERT [TD_SCHOOL_YEAR_CORP]");
				dbUtil.insert(conn, "TD_SCHOOL_YEAR_CORP", item);
			}

			// commit
			conn.commit();

		} catch (Exception e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}

	/**
	 * 讀取後直接，匯出 INSERT 語法檔案
	 * @param configFilePath 設定檔路徑
	 * @param importFilePath 匯入檔案路徑
	 * @param exportSQLFilePath 要匯出的 INSERT 語法檔案路徑
	 * @throws Exception
	 */
	public void readAndExportSql(
			String configFilePath,
			String importFilePath,
			String exportSQLFilePath) throws Exception {

		// =================================================
		// 讀檔
		// =================================================
		this.readAll(configFilePath, importFilePath);

		// =================================================
		// 產生 sql
		// =================================================
		FileUtil fileUtil = new FileUtil();
		fileUtil.setUseUTF8BOM(true);

		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TD_SCHOOL_YEAR_ES]");
		fileUtil.addLine("------------------------------------------------------");

		for (LinkedHashMap<String, Object> item : this.yearEsMapList) {
			// TD_SCHOOL_YEAR_ES
			fileUtil.addLine("--");
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_SCHOOL_YEAR_ES", item, true) + ";");
			fileUtil.addLine();
		}

		fileUtil.addLine();
		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TD_SCHOOL_YEAR_ST]");
		fileUtil.addLine("------------------------------------------------------");

		for (LinkedHashMap<String, Object> item : this.yearStMapList) {
			// TD_SCHOOL_YEAR_ST
			fileUtil.addLine("--");
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_SCHOOL_YEAR_ST", item, true) + ";");
			fileUtil.addLine();
		}

		fileUtil.addLine();
		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TD_SCHOOL_YEAR_INSTITUE]");
		fileUtil.addLine("------------------------------------------------------");
		for (LinkedHashMap<String, Object> item : this.yearInstMapList) {
			// TD_SCHOOL_YEAR_INSTITUE
			fileUtil.addLine("--" + item.get("INSTITUE_CHN_NAME"));
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_SCHOOL_YEAR_INSTITUE", item, true) + ";");
			fileUtil.addLine();
		}

		fileUtil.addLine();
		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TD_SCHOOL_YEAR_TEACHER]");
		fileUtil.addLine("------------------------------------------------------");
		for (LinkedHashMap<String, Object> item : this.yearTeacherMapList) {
			// TD_SCHOOL_YEAR_TEACHER
			fileUtil.addLine("--" + item.get("NAME"));
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_SCHOOL_YEAR_TEACHER", item, true) + ";");
			fileUtil.addLine();
		}

		fileUtil.addLine();
		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TD_SCHOOL_YEAR_CORP]");
		fileUtil.addLine("------------------------------------------------------");
		for (LinkedHashMap<String, Object> item : this.yearCorpMapList) {
			// TD_SCHOOL_YEAR_TEACHER
			fileUtil.addLine("--");
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_SCHOOL_YEAR_CORP", item, true) + ";");
			fileUtil.addLine();
		}

		// 寫入檔案
		fileUtil.writeToFile(exportSQLFilePath);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// 參數設定 eva
		String configFilePath = "C:/Users/turbo/workspace/RbtProject/srcAllen/EvaImport.xml";
		String importFilePath = "D:/turbo/資料匯入/ImportFile/學校/台南/嘉南藥理科技大學-hmt-OK.xls";
		String exportSQLFilePath = "D:/turbo/資料匯入/OutputFile/PROF.sql";

		// 參數設定 allen
		//String configFilePath = "C:/workspace/RbtProject/srcAllen/EvaImport.xml";
		//String importFilePath = "h:/0801/學校單位-(中區).xls";
		//String exportSQLFilePath = "h:/0801/PROF.sql";

		EvaImportProf evaImportProf = new EvaImportProf();

		// 讀取後直接，匯出 INSERT 語法檔案
		evaImportProf.readAndExportSql(configFilePath, importFilePath, exportSQLFilePath);

		// 讀取後直接 insert 到 DB
		// evaImportSchool.readAndInsert(configFilePath, importFilePath);
	}
}
