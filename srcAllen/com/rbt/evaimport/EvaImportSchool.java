package com.rbt.evaimport;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;

import com.rbt.util.db.RbtDbUtilImpl;
import com.rbt.util.db.SqlUtil;
import com.rbt.util.file.FileUtil;

/**
 * 學校資料處理
 * @author Allen
 */
public class EvaImportSchool extends BaseEvaImport{

	/**
	 * 讀取後直接 insert 到 DB
	 * @param configFilePath 設定檔路徑
	 * @param configID 設定組 ID
	 * @param importFilePath 匯入檔案路徑
	 * @throws Exception
	 */
	public void readAndInsert(String configFilePath, String configID, String importFilePath) throws Exception {

		// 讀檔
		List<LinkedHashMap<String, Object>> dataMapList =
				this.readFile(configFilePath, configID, importFilePath);

		RbtDbUtilImpl dbUtil = new RbtDbUtilImpl();
		Connection conn = null;

		try {

			// 取得 Connection
			conn = dbUtil.getConnection();
			conn.setAutoCommit(false);

			// Insert
			for (LinkedHashMap<String, Object> schoolbase : dataMapList) {

				// todo

				// TD_SCHOOL_BASE
				if (!this.checkSchoolBase(schoolbase.get("SCHOOL_ID") + "")) {
					System.out.println("INSERT [TD_SCHOOL_BASE]");
					dbUtil.insert(conn, "TD_SCHOOL_BASE", schoolbase);
				}
				// TS_USER
				if (!this.checkSchoolBase(schoolbase.get("SCHOOL_ID") + "")) {
					System.out.println("INSERT [TS_USER]");
					LinkedHashMap<String, Object> tsUser = this.perpareTsUser(schoolbase, this.function, null);
					dbUtil.insert(conn, "TS_USER", tsUser);
					System.out.println("INSERT [TS_ROLE_USER]");
					dbUtil.insert(conn, "TS_ROLE_USER", this.perpareTsRoleUser(schoolbase, tsUser.get("USER_UID") + "", this.function, conn));
				}

				// TD_SCHOOL_INFO
				if (!this.checkSchoolInfo(schoolbase.get("SCHOOL_ID") + "")) {
					System.out.println("INSERT [TD_SCHOOL_INFO] UNIT_INFO_UID:1");
					dbUtil.insert(conn, "TD_SCHOOL_INFO", this.perpareSchoolInfo(schoolbase, "1", this.function, conn));
					System.out.println("INSERT [TD_SCHOOL_INFO] UNIT_INFO_UID:2");
					dbUtil.insert(conn, "TD_SCHOOL_INFO", this.perpareSchoolInfo(schoolbase, "2", this.function, conn));
					System.out.println("INSERT [TD_SCHOOL_INFO] UNIT_INFO_UID:3");
					dbUtil.insert(conn, "TD_SCHOOL_INFO", this.perpareSchoolInfo(schoolbase, "3", this.function, conn));
					System.out.println("INSERT [TD_SCHOOL_INFO] UNIT_INFO_UID:4");
					dbUtil.insert(conn, "TD_SCHOOL_INFO", this.perpareSchoolInfo(schoolbase, "4", this.function, conn));
					System.out.println("INSERT [TD_SCHOOL_INFO] UNIT_INFO_UID:5");
					dbUtil.insert(conn, "TD_SCHOOL_INFO", this.perpareSchoolInfo(schoolbase, "5", this.function, conn));
				}

				// TD_SCHOOL_YEAR
				System.out.println("INSERT [TD_SCHOOL_YEAR]");
				dbUtil.insert(conn, "TD_SCHOOL_YEAR", this.perpareSchoolYear(schoolbase, this.function, conn));
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
	 * @param configID 設定組 ID
	 * @param importFilePath 匯入檔案路徑
	 * @param exportSQLFilePath 要匯出的 INSERT 語法檔案路徑
	 * @throws Exception
	 */
	public void readAndExportSql(
			String configFilePath,
			String configID,
			String importFilePath,
			String exportSQLFilePath) throws Exception {

		// 讀檔
		List<LinkedHashMap<String, Object>> dataMapList =
				this.readFile(configFilePath, configID, importFilePath);

		// 產生 sql
		FileUtil fileUtil = new FileUtil();
		fileUtil.setUseUTF8BOM(true);

		for (LinkedHashMap<String, Object> schoolbase : dataMapList) {

			fileUtil.addLine("------------------------------------------------------");
			fileUtil.addLine("--" + schoolbase.get("SCHOOL_CHN_NAME"));
			fileUtil.addLine("------------------------------------------------------");

			// TD_SCHOOL_BASE
			if (!this.checkSchoolBase(schoolbase.get("SCHOOL_ID") + "")) {
				fileUtil.addLine("--[TD_SCHOOL_BASE]");
				fileUtil.addLine(SqlUtil.genInsertSQL("TD_SCHOOL_BASE", schoolbase, true) + ";");
				fileUtil.addLine();
			}

			// TS_USER、TS_ROLE_USER
			if (!this.checkSchoolBase(schoolbase.get("SCHOOL_ID") + "")) {
				fileUtil.addLine("--[TS_USER]");
				LinkedHashMap<String, Object> tsUser = this.perpareTsUser(schoolbase, this.function, null);
				fileUtil.addLine(SqlUtil.genInsertSQL("TS_USER", tsUser, true) + ";");
				fileUtil.addLine("--[TS_ROLE_USER]");
				fileUtil.addLine(SqlUtil.genInsertSQL("TS_ROLE_USER", this.perpareTsRoleUser(schoolbase, tsUser.get("USER_UID") + "", this.function, null), true) + ";");
				fileUtil.addLine();
			}

			// TD_SCHOOL_INFO
			if (!this.checkSchoolInfo(schoolbase.get("SCHOOL_ID") + "")) {
				fileUtil.addLine("--[TD_SCHOOL_INFO]");
				fileUtil.addLine(
						SqlUtil.genInsertSQL("TD_SCHOOL_INFO",
								this.perpareSchoolInfo(schoolbase, "1", this.function, null), true) + ";");
				fileUtil.addLine(
						SqlUtil.genInsertSQL("TD_SCHOOL_INFO",
								this.perpareSchoolInfo(schoolbase, "2", this.function, null), true) + ";");
				fileUtil.addLine(
						SqlUtil.genInsertSQL("TD_SCHOOL_INFO",
								this.perpareSchoolInfo(schoolbase, "3", this.function, null), true) + ";");
				fileUtil.addLine(
						SqlUtil.genInsertSQL("TD_SCHOOL_INFO",
								this.perpareSchoolInfo(schoolbase, "4", this.function, null), true) + ";");
				fileUtil.addLine(
						SqlUtil.genInsertSQL("TD_SCHOOL_INFO",
								this.perpareSchoolInfo(schoolbase, "5", this.function, null), true) + ";");
				fileUtil.addLine();
			}

			// TD_SCHOOL_YEAR
			fileUtil.addLine("--[TD_SCHOOL_YEAR]");
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_SCHOOL_YEAR", this.perpareSchoolYear(schoolbase, this.function, null), true) + ";");

			fileUtil.addLine();
		}

		// 寫入檔案
		fileUtil.writeToFile(exportSQLFilePath);
	}

	/**
	 * @param SCHOOL_ID
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean checkSchoolBase(String SCHOOL_ID) throws ClassNotFoundException, IOException, Exception {
		return (new RbtDbUtilImpl().query("SELECT 1 FROM TD_SCHOOL_BASE WHERE SCHOOL_ID='" + SCHOOL_ID + "'").size() > 0);
	}

	/**
	 * @param USER_ID
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean checkTsUser(String SCHOOL_ID) throws ClassNotFoundException, IOException, Exception {
		return (new RbtDbUtilImpl().query("SELECT 1 FROM TS_USER WHERE USER_ID='" + SCHOOL_ID + "'").size() > 0);
	}

	/**
	 * @param SCHOOL_ID
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean checkSchoolInfo(String SCHOOL_ID) throws ClassNotFoundException, IOException, Exception {
		return (new RbtDbUtilImpl().query("SELECT 1 FROM TD_SCHOOL_INFO WHERE SCHOOL_ID='" + SCHOOL_ID + "'").size() > 0);
	}

	/**
	 * 準備 TS_USER 資料
	 * @param schoolbase
	 * @return
	 */
	public LinkedHashMap<String, Object> perpareTsUser(LinkedHashMap<String, Object> schoolbase, EvaImportFunctionImpl func, Connection conn) {

		LinkedHashMap<String, Object> tsUser = new LinkedHashMap<String, Object>();

		tsUser.put("USER_UID", func.genUID("[DATE0-14][R][NUM5]"));
		tsUser.put("USER_TYPE", "20");
		tsUser.put("USER_ID", schoolbase.get("SCHOOL_ID"));
		tsUser.put("PASSWORD", schoolbase.get("SCHOOL_ID"));
		tsUser.put("CHN_NAME", schoolbase.get("SCHOOL_CHN_NAME"));
		tsUser.put("ENG_NAME", schoolbase.get("SCHOOL_ENG_NAME"));
		tsUser.put("UNIT_TYPE", "2");
		tsUser.put("UNIT_UID", schoolbase.get("SCHOOL_BASE_UID"));
		tsUser.put("UNIT_NAME", schoolbase.get("SCHOOL_CHN_NAME"));
		tsUser.put("CON_CITY_UID", schoolbase.get("CITY_UID"));
		tsUser.put("CON_TOWN_UID", schoolbase.get("TOWN_UID"));
		tsUser.put("CON_POST_ID", schoolbase.get("POST_ID"));
		tsUser.put("CON_ADDRESS", schoolbase.get("CHN_ADDRESS"));
		tsUser.put("LOCKED", "1");
		tsUser.put("START_DATE", schoolbase.get("APPLY_DATE"));
		tsUser.put("END_DATE", "2020-12-31");
		tsUser.put("USER_STATUS", "1");
		tsUser.put("ADD_UID", schoolbase.get("ADD_UID"));
		tsUser.put("ADD_DATE", schoolbase.get("ADD_DATE"));
		tsUser.put("ADD_TIME", schoolbase.get("ADD_TIME"));
		tsUser.put("LUPD_UID", schoolbase.get("LUPD_UID"));
		tsUser.put("LUPD_DATE", schoolbase.get("LUPD_DATE"));
		tsUser.put("LUPD_TIME", schoolbase.get("LUPD_TIME"));
		tsUser.put("UNIT_INFO_UID", schoolbase.get("UNIT_INFO_UID"));
		tsUser.put("ISWORK", "Y");
		return tsUser;
	}

	/**
	 * 準備 TS_USER 資料
	 * @param schoolbase
	 * @return
	 */
	public LinkedHashMap<String, Object> perpareTsRoleUser(LinkedHashMap<String, Object> schoolbase, String USER_UID, EvaImportFunctionImpl func, Connection conn) {

		LinkedHashMap<String, Object> tsUser = new LinkedHashMap<String, Object>();
		tsUser.put("ROLE_USER_UID", func.genUID("[DATE0-14][R][NUM5]"));
		tsUser.put("USER_UID", USER_UID);
		tsUser.put("ROLE_UID", "20100805071142400001");
		tsUser.put("ADD_UID", schoolbase.get("ADD_UID"));
		tsUser.put("ADD_DATE", schoolbase.get("ADD_DATE"));
		tsUser.put("ADD_TIME", schoolbase.get("ADD_TIME"));
		tsUser.put("LUPD_UID", schoolbase.get("LUPD_UID"));
		tsUser.put("LUPD_DATE", schoolbase.get("LUPD_DATE"));
		tsUser.put("LUPD_TIME", schoolbase.get("LUPD_TIME"));
		return tsUser;
	}

	/**
	 * TD_SCHOOL_INFO
	 * @param schoolbase
	 * @param UNIT_INFO_UID
	 * @param func
	 * @param conn
	 * @return
	 */
	public LinkedHashMap<String, Object> perpareSchoolInfo(
			LinkedHashMap<String, Object> schoolbase, String UNIT_INFO_UID,
			EvaImportFunctionImpl func, Connection conn) {

		LinkedHashMap<String, Object> schoolInfo = new LinkedHashMap<String, Object>();

		schoolInfo.put("SCHOOL_INFO_UID", func.genUID("[DATE0-14][R][NUM5]"));
		schoolInfo.put("SCHOOL_BASE_UID", schoolbase.get("SCHOOL_BASE_UID"));
		schoolInfo.put("SCHOOL_ID", schoolbase.get("SCHOOL_ID"));
		schoolInfo.put("SCHOOL_CHN_NAME", schoolbase.get("SCHOOL_CHN_NAME"));
		schoolInfo.put("SCHOOL_ENG_NAME", schoolbase.get("SCHOOL_ENG_NAME"));
		schoolInfo.put("CITY_UID", schoolbase.get("CITY_UID"));
		schoolInfo.put("TOWN_UID", schoolbase.get("TOWN_UID"));
		schoolInfo.put("POST_ID", schoolbase.get("POST_ID"));
		schoolInfo.put("CHN_ADDRESS", schoolbase.get("CHN_ADDRESS"));
		schoolInfo.put("SCHOOL_MASTER", schoolbase.get("SCHOOL_MASTER"));
		schoolInfo.put("LM_NAME", schoolbase.get("LM_NAME"));
		schoolInfo.put("LM_PHONE", schoolbase.get("LM_PHONE"));
		schoolInfo.put("LM_FAX", schoolbase.get("LM_FAX"));
		schoolInfo.put("LM_EMAIL", schoolbase.get("LM_EMAIL"));
		schoolInfo.put("ADD_UID", schoolbase.get("ADD_UID"));
		schoolInfo.put("ADD_DATE", schoolbase.get("ADD_DATE"));
		schoolInfo.put("ADD_TIME", schoolbase.get("ADD_TIME"));
		schoolInfo.put("LUPD_UID", schoolbase.get("LUPD_UID"));
		schoolInfo.put("LUPD_DATE", schoolbase.get("LUPD_DATE"));
		schoolInfo.put("LUPD_TIME", schoolbase.get("LUPD_TIME"));
		schoolInfo.put("UNIT_INFO_UID", UNIT_INFO_UID);
		return schoolInfo;
	}

	/**
	 * TD_SCHOOL_INFO
	 * @param schoolbase
	 * @param UNIT_INFO_UID
	 * @param func
	 * @param conn
	 * @return
	 * @throws Exception
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public LinkedHashMap<String, Object> perpareSchoolYear(
			LinkedHashMap<String, Object> schoolbase,
			EvaImportFunctionImpl func, Connection conn) throws ClassNotFoundException, IOException, Exception {

		LinkedHashMap<String, Object> schoolYear = new LinkedHashMap<String, Object>();

		schoolYear.put("SCHOOL_YEAR_UID", func.genUID("[DATE0-14][R][NUM5]"));

		String SCHOOL_BASE_UID = schoolbase.get("SCHOOL_BASE_UID")+"";
		if (this.checkSchoolBase(schoolbase.get("SCHOOL_ID")+"")) {
			SCHOOL_BASE_UID = func.select(
					"SCHOOL_BASE_UID",
					"select school_base_uid from td_school_base where school_id ='" + schoolbase.get("SCHOOL_ID") + "'",
					null, conn);
		}
		schoolYear.put("SCHOOL_BASE_UID", SCHOOL_BASE_UID);
		schoolYear.put("CLASS_TYPE_UID", "20120911003905200001"); // 年度代碼
		schoolYear.put("SCHOOL_ID", schoolbase.get("SCHOOL_ID"));
		schoolYear.put("SCHOOL_CHN_NAME", schoolbase.get("SCHOOL_CHN_NAME"));
		schoolYear.put("SCHOOL_ENG_NAME", this.trnsSpace(schoolbase.get("SCHOOL_ENG_NAME")));
		schoolYear.put("SCHOOL_KIND", "2");
		schoolYear.put("CITY_UID", schoolbase.get("CITY_UID"));
		schoolYear.put("TOWN_UID", schoolbase.get("TOWN_UID"));
		schoolYear.put("POST_ID", schoolbase.get("POST_ID"));
		schoolYear.put("CHN_ADDRESS", schoolbase.get("CHN_ADDRESS"));
		schoolYear.put("ENG_ADDRESS", schoolbase.get("ENG_ADDRESS"));
		schoolYear.put("SCHOOL_WEBSET", this.trnsSpace(schoolbase.get("SCHOOL_WEBSET")));
		schoolYear.put("SCHOOL_MASTER", this.trnsSpace(schoolbase.get("SCHOOL_MASTER")));
		schoolYear.put("LM_NAME", this.trnsSpace(schoolbase.get("LM_NAME")));
		schoolYear.put("LM_TITLE", schoolbase.get("LM_TITLE"));
		schoolYear.put("LM_PHONE", this.trnsSpace(schoolbase.get("LM_PHONE")));
		schoolYear.put("LM_BRANCH_PHONE", schoolbase.get("LM_BRANCH_PHONE"));
		schoolYear.put("LM_MOBILE_PHONE", schoolbase.get("LM_MOBILE_PHONE"));
		schoolYear.put("LM_FAX", this.trnsSpace(schoolbase.get("LM_FAX")));
		schoolYear.put("LM_EMAIL", this.trnsSpace(schoolbase.get("LM_EMAIL")));
		schoolYear.put("BELONG_UNIT", schoolbase.get("BELONG_UNIT"));
		schoolYear.put("TL_NAME", this.trnsSpace(this.trnsSpace(this.trnsSpace(schoolbase.get("TL_NAME")))));
		schoolYear.put("TL_PHONE", this.trnsSpace(this.trnsSpace(schoolbase.get("TL_PHONE"))));
		schoolYear.put("TL_BRANCH_PHONE", schoolbase.get("TL_BRANCH_PHONE"));
		schoolYear.put("MEMO", schoolbase.get("MEMO"));
		schoolYear.put("SCHOOL_INTRO_CHN", this.trnsSpace(schoolbase.get("SCHOOL_INTRO_CHN")));
		schoolYear.put("SCHOOL_INTRO_ENG", schoolbase.get("SCHOOL_INTRO_ENG"));
		schoolYear.put("FREEZE_FLAG", "0");
		schoolYear.put("APPLY_UID", schoolbase.get("APPLY_UID"));
		schoolYear.put("APPLY_DATE", schoolbase.get("APPLY_DATE"));
		schoolYear.put("APPLY_TIME", schoolbase.get("APPLY_TIME"));
		schoolYear.put("AUTH_UID", schoolbase.get("ASSIGN_UID"));
		schoolYear.put("AUTH_DATE", schoolbase.get("APPLY_DATE"));
		schoolYear.put("AUTH_TIME", schoolbase.get("APPLY_TIME"));
		schoolYear.put("STATUS", "9");
		schoolYear.put("ADD_UID", schoolbase.get("ADD_UID"));
		schoolYear.put("ADD_DATE", schoolbase.get("ADD_DATE"));
		schoolYear.put("ADD_TIME", schoolbase.get("ADD_TIME"));
		schoolYear.put("LUPD_UID", schoolbase.get("LUPD_UID"));
		schoolYear.put("LUPD_DATE", schoolbase.get("LUPD_DATE"));
		schoolYear.put("LUPD_TIME", schoolbase.get("LUPD_TIME"));
		// schoolYear.put("AUTH_MEMO", schoolbase.get("XXXXXXXXXXXXXXXXXXXXXXXXXXX"));
		schoolYear.put("APPLY_PASS_FLAG", "0");
		schoolYear.put("SCHOOL_PASS_FLAG", "0");
		schoolYear.put("CLASS_PASS_FLAG", "0");
		schoolYear.put("INTRO_PASS_FLAG", "0");
		schoolYear.put("UNIT_INFO_UID", schoolbase.get("UNIT_INFO_UID"));
		schoolYear.put("ASSIGN_DATE", schoolbase.get("ASSIGN_DATE"));
		schoolYear.put("ASSIGN_TIME", schoolbase.get("ASSIGN_TIME"));
		schoolYear.put("ASSIGN_UID", schoolbase.get("ASSIGN_UID"));
		// schoolYear.put("RETURN_DATE", schoolbase.get("XXXXXXXXXXXXXXXXXXXXXXXXXXX"));

		return schoolYear;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// 參數設定
		String configFilePath = "C:/workspace/RbtProject/srcAllen/EvaImport.xml";
		String configID = "school";
		String importFilePath = "h:/0801/環球科技大學-shuo-OK.xls";
		String exportSQLFilePath = "h:/0801/school_test.sql";

//		String configFilePath = "C:/Users/turbo/workspace/RbtProject/srcAllen/EvaImport.xml";
//		String configID = "school";
//		String importFilePath = "D:/turbo/資料匯入/ImportFile/學校/中區學校單位.xls";
//		String exportSQLFilePath = "D:/turbo/資料匯入/OutputFile/school_test.sql";

		EvaImportSchool evaImportSchool = new EvaImportSchool();

		// 讀取後直接，匯出 INSERT 語法檔案
		evaImportSchool.readAndExportSql(configFilePath, configID, importFilePath, exportSQLFilePath);

		// 讀取後直接 insert 到 DB
		// evaImportSchool.readAndInsert(configFilePath, configID, importFilePath);
	}
}
