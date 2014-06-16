package com.rbt.evaimport;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.rbt.util.BeanUtil;
import com.rbt.util.StringUtil;
import com.rbt.util.db.RbtDbUtilImpl;
import com.rbt.util.db.SqlUtil;
import com.rbt.util.file.FileUtil;

/**
 * 學校資料處理
 * @author Allen
 */
public class EvaImportYearOtherLm extends BaseEvaImport {

	/**
	 * TD_SCHOOL_BASE 學校基本資料
	 */
	private List<LinkedHashMap<String, Object>> schoolBaseMapList = new ArrayList<LinkedHashMap<String, Object>>();
	/**
	 * TS_USER
	 */
	private List<LinkedHashMap<String, Object>> tsUserMapList = new ArrayList<LinkedHashMap<String, Object>>();
	/**
	 * TS_ROLE_USER
	 */
	private List<LinkedHashMap<String, Object>> tsRoleUserMapList = new ArrayList<LinkedHashMap<String, Object>>();
	/**
	 * TD_SCHOOL_INFO
	 */
	private List<LinkedHashMap<String, Object>> schoolInfoMapList = new ArrayList<LinkedHashMap<String, Object>>();
	/**
	 * TD_SCHOOL_YEAR
	 */
	private List<LinkedHashMap<String, Object>> schoolYearMapList = new ArrayList<LinkedHashMap<String, Object>>();
	/**
	 * TD_YEAR_OTHER_LM
	 */
	private List<LinkedHashMap<String, Object>> yearOtherLmMapList;
	/**
	 * TD_YEAR_TL_SEED_ADD
	 */
	private List<LinkedHashMap<String, Object>> seedAddList;
	/**
	 * TD_YEAR_TL_SEED_SELECT
	 */
	private List<LinkedHashMap<String, Object>> seedSelectList;

	/**
	 * 讀取所有資料
	 * @param configFilePath
	 * @param importFilePath
	 * @throws Exception
	 */
	public void readAll(Connection conn, String configFilePath, String importFilePath) throws Exception {

		// =====================================
		// 讀檔
		// =====================================
		// 讀檔 學校基本資料
		List<LinkedHashMap<String, Object>> importSchoolBaseMapList = this.readFile(configFilePath, "school", importFilePath);
		// 讀檔 TD_YEAR_OTHER_LM
		this.yearOtherLmMapList = this.readFile(configFilePath, "TD_YEAR_OTHER_LM", importFilePath);
		// 讀檔 TD_YEAR_TL_SEED_ADD
		this.seedAddList = this.readFile(configFilePath, "TD_YEAR_TL_SEED_ADD", importFilePath);
		// 讀檔 TD_YEAR_TL_SEED_SELECT
		System.out.println("========================== TD_YEAR_TL_SEED_SELECT");
		this.seedSelectList = this.readFile(configFilePath, "TD_YEAR_TL_SEED_SELECT", importFilePath);
		System.out.println("========================== TD_YEAR_TL_SEED_SELECT");

		// =====================================
		// 引索
		// =====================================
		// 以 IDCARD_NUM 為 key, 對 seedadd 做引索
		LinkedHashMap<String, LinkedHashMap<String, Object>> seedAddMap =
				new LinkedHashMap<String, LinkedHashMap<String, Object>>();

		for (LinkedHashMap<String, Object> dateMap : this.seedAddList) {
			seedAddMap.put(StringUtil.safeTrim(dateMap.get("IDCARD_NUM")), dateMap);
		}

		// =====================================
		// 先行處理
		// =====================================

		// =====================================
		// TD_SCHOOL_BASE、TS_USER、 TS_ROLE_USER 先行處理
		// =====================================
		for (LinkedHashMap<String, Object> schoolbase : importSchoolBaseMapList) {
			if (!this.checkSchoolBase(schoolbase.get("SCHOOL_ID") + "")) {
				// TD_SCHOOL_BASE
				this.schoolBaseMapList.add(schoolbase);
				// TS_USER
				LinkedHashMap<String, Object> tsUser = this.perpareTsUser(schoolbase, this.function, conn);
				this.tsUserMapList.add(tsUser);
				// TS_ROLE_USER
				this.tsRoleUserMapList.add(this.perpareTsRoleUser(schoolbase, tsUser.get("USER_UID") + "", this.function, conn));
			}
		}

		// =====================================
		// TD_SCHOOL_INFO 先行處理
		// =====================================
		for (LinkedHashMap<String, Object> schoolbase : importSchoolBaseMapList) {
			// TD_SCHOOL_INFO
			if (!this.checkSchoolInfo(schoolbase.get("SCHOOL_ID") + "")) {
				// 5個區都要
				this.schoolInfoMapList.add(this.perpareSchoolInfo(schoolbase, "1", this.function, conn));
				this.schoolInfoMapList.add(this.perpareSchoolInfo(schoolbase, "2", this.function, conn));
				this.schoolInfoMapList.add(this.perpareSchoolInfo(schoolbase, "3", this.function, conn));
				this.schoolInfoMapList.add(this.perpareSchoolInfo(schoolbase, "4", this.function, conn));
				this.schoolInfoMapList.add(this.perpareSchoolInfo(schoolbase, "5", this.function, conn));
			}
		}

		// =====================================
		// TD_SCHOOL_YEAR 先行處理
		// =====================================
		// 並建立以 SCHOOL_ID 為索引的 SCHOOL_YEAR_UID MAP
		LinkedHashMap<String, String> schoolYearUidMap = new LinkedHashMap<String, String>();
		for (LinkedHashMap<String, Object> schoolbase : importSchoolBaseMapList) {
			LinkedHashMap<String, Object> schoolYear = this.perpareSchoolYear(schoolbase, this.function, conn);
			this.schoolYearMapList.add(schoolYear);
			schoolYearUidMap.put(schoolYear.get("SCHOOL_ID") + "", schoolYear.get("SCHOOL_YEAR_UID") + "");
		}

		// =====================================
		// TD_YEAR_OTHER_LM 先行處理
		// =====================================
		for (LinkedHashMap<String, Object> dateMap : this.yearOtherLmMapList) {
			// MAPPING_UID (SCHOOL_YEAR_UID)
			String SCHOOL_ID = StringUtil.safeTrim(dateMap.get("SCHOOL_ID"));
			String MAPPING_UID = StringUtil.safeTrim(schoolYearUidMap.get(SCHOOL_ID));
			if (StringUtil.isEmpty(MAPPING_UID)) {
				throw new Exception("TD_YEAR_OTHER_LM 的 SCHOOL_ID 無法參照 學校基本資料匯入資料! SCHOOL_ID:[" + SCHOOL_ID + "]");
			}
			dateMap.put("MAPPING_UID", MAPPING_UID);
			// 移除 SCHOOL_ID (TD_YEAR_OTHER_LM 中無此欄位)
			dateMap.remove("SCHOOL_ID");

			// 身份證字號
			String IDCARD_NUM = StringUtil.safeTrim(dateMap.get("IDCARD_NUM"));
			// 加入缺失的欄位
			if (seedAddMap.containsKey(IDCARD_NUM)) {
				dateMap.put("MOBILE", seedAddMap.get(IDCARD_NUM).get("MOBILE_PHONE"));
				dateMap.put("EMAIL", seedAddMap.get(IDCARD_NUM).get("EMAIL"));
			}
			// 移除 IDCARD_NUM (TD_YEAR_OTHER_LM 中無此欄位)
			dateMap.remove("IDCARD_NUM");
		}

		// =====================================
		// TD_YEAR_TL_SEED_ADD 先行處理
		// =====================================
		List<LinkedHashMap<String, Object>> newSeedAddList = new ArrayList<LinkedHashMap<String, Object>>();
		for (LinkedHashMap<String, Object> dateMap : this.seedAddList) {
			// MAPPING_UID (SCHOOL_YEAR_UID)
			String SCHOOL_ID = StringUtil.safeTrim(dateMap.get("SCHOOL_ID"));
			String MAPPING_UID = StringUtil.safeTrim(schoolYearUidMap.get(SCHOOL_ID));
			if (StringUtil.isEmpty(MAPPING_UID)) {
				throw new Exception("TD_YEAR_OTHER_LM 的 SCHOOL_ID 無法參照 學校基本資料匯入資料! SCHOOL_ID:[" + SCHOOL_ID + "]");
			}
			dateMap.put("MAPPING_UID", MAPPING_UID);

			// STATION_TYPE
			String STATION_TYPE = StringUtil.safeTrim(dateMap.get("STATION_TYPE"));
			String name= StringUtil.safeTrim(dateMap.get("NAME"));
			String idNum = StringUtil.safeTrim(dateMap.get("IDCARD_NUM"));

			
			if ("3".equals(STATION_TYPE)) {
				// 人員身份需為 3 時
				dateMap.put("STATION_UID", "03");

			}else if(!this.checkTdSeedIsExist(conn, name, idNum)){
				// 人員身份為 1.2 時 且存在於 TD_SEED
				if("1".equals(STATION_TYPE)){
					System.out.println ("STEP1");
					dateMap.put("STATION_UID", "01");
				}else if("2".equals(STATION_TYPE)){
					System.out.println ("STEP2");
					dateMap.put("STATION_UID", "02");
				}else{
					dateMap.put("STATION_UID", "03");
				}
			}else{
				System.out.println(dateMap.get("NAME") + "排除 [TD_YEAR_TL_SEED_ADD]");
				continue;
			}

			dateMap.put("IDCARD_NUM", this.trnsSpace(dateMap.get("IDCARD_NUM")));
			dateMap.put("MOBILE_PHONE", this.trnsSpace(dateMap.get("MOBILE_PHONE")));
			dateMap.remove("STATION_TYPE");
			dateMap.remove("SCHOOL_ID");
			newSeedAddList.add(dateMap);

		}
		// 取代
		this.seedAddList = newSeedAddList;

		// =====================================
		// TD_YEAR_TL_SEED_SELECT 先行處理
		// =====================================
		List<LinkedHashMap<String, Object>> newSeedSelectList = new ArrayList<LinkedHashMap<String, Object>>();
		for (LinkedHashMap<String, Object> dateMap : this.seedSelectList) {
			// MAPPING_UID (SCHOOL_YEAR_UID)
			String SCHOOL_ID = StringUtil.safeTrim(dateMap.get("SCHOOL_ID"));
			String MAPPING_UID = StringUtil.safeTrim(schoolYearUidMap.get(SCHOOL_ID));
			if (StringUtil.isEmpty(MAPPING_UID)) {
				throw new Exception("TD_YEAR_OTHER_LM 的 SCHOOL_ID 無法參照 學校基本資料匯入資料! SCHOOL_ID:[" + SCHOOL_ID + "]");
			}
			dateMap.put("MAPPING_UID", MAPPING_UID);
			// 身份別
			String STATION_TYPE = StringUtil.safeTrim(dateMap.get("STATION_TYPE"));
			// 不為1.或2 時略過
			if (STATION_TYPE.indexOf("1") < 0 && STATION_TYPE.indexOf("2") < 0) {
				System.out.println(dateMap.get("NAME") + "排除 [TD_YEAR_TL_SEED_ADD]");
				continue;
			}
			// 筆數小於0時略過
			String name= StringUtil.safeTrim(dateMap.get("NAME"));
			String idNum = StringUtil.safeTrim(dateMap.get("IDCARD_NUM"));

			if (!this.checkTdSeedIsExist(conn, name, idNum)) {
				System.out.println(dateMap.get("NAME") + "排除 [TD_YEAR_TL_SEED_ADD]");
				continue;
			}
			// 移除欄位
			dateMap.remove("STATION_TYPE");
			dateMap.remove("IDCARD_NUM");
			dateMap.remove("NAME");
			dateMap.remove("SCHOOL_ID");
			dateMap.remove("UNIT_INFO_UID");

			// 放入新List
			newSeedSelectList.add(dateMap);
		}
		// 取代
		this.seedSelectList = newSeedSelectList;

		// System.out.println(new BeanUtil().showContent("TD_YEAR_OTHER_LM", yearOtherLmMapList));
		// System.out.println(new BeanUtil().showContent("TD_YEAR_TL_SEED_ADD", seedAddList));
		System.out.println(new BeanUtil().showContent("TD_YEAR_TL_SEED_SELECT", this.seedSelectList));
	}


	private boolean checkTdSeedIsExist(Connection conn, String name, String idNum) throws ClassNotFoundException, IOException, Exception{

		StringBuffer varname1 = new StringBuffer();
		varname1.append("SELECT DISTINCT a.IDCARD_NUM ");
		varname1.append("FROM   TD_SEED a ");
		varname1.append("	   join TD_SEED_APPLY b ");
		varname1.append("		 ON a.IDCARD_NUM = b.IDCARD_NUM ");
		varname1.append("WHERE  ( b.CHN_NAME = '" + name + "' ");
		varname1.append("		  OR a.IDCARD_NUM = '" + idNum + "' ) ");

		return new RbtDbUtilImpl().query(conn, varname1.toString() ,null).size()>0;
	}


	/**
	 * 讀取後直接 insert 到 DB
	 * @param configFilePath 設定檔路徑
	 * @param configID 設定組 ID
	 * @param importFilePath 匯入檔案路徑
	 * @throws Exception
	 *
	 *             public void readAndInsert(String configFilePath, String importFilePath) throws Exception {
	 *
	 *             // =================================================
	 *             // 讀檔
	 *             // =================================================
	 *             this.readAll(configFilePath, importFilePath);
	 *
	 *             // =================================================
	 *             // INSERT
	 *             // =================================================
	 *             RbtDbUtilImpl dbUtil = new RbtDbUtilImpl();
	 *             Connection conn = null;
	 *
	 *             try {
	 *
	 *             // 取得 Connection
	 *             conn = dbUtil.getConnection();
	 *             conn.setAutoCommit(false);
	 *
	 *             // Insert
	 *             for (LinkedHashMap<String, Object> item : this.yearOtherLmMapList) {
	 *             System.out.println("INSERT [TD_YEAR_OTHER_LM]");
	 *             dbUtil.insert(conn, "TD_YEAR_OTHER_LM", item);
	 *             }
	 *             for (LinkedHashMap<String, Object> item : this.seedAddList) {
	 *             System.out.println("INSERT [TD_YEAR_TL_SEED_ADD]");
	 *             dbUtil.insert(conn, "TD_YEAR_TL_SEED_ADD", item);
	 *             }
	 *             for (LinkedHashMap<String, Object> item : this.seedSelectList) {
	 *             System.out.println("INSERT [TD_YEAR_TL_SEED_SELECT]");
	 *             dbUtil.insert(conn, "TD_YEAR_TL_SEED_SELECT", item);
	 *             }
	 *
	 *             // commit
	 *             conn.commit();
	 *
	 *             } catch (Exception e) {
	 *             if (conn != null) {
	 *             conn.rollback();
	 *             }
	 *             throw e;
	 *             } finally {
	 *             if (conn != null) {
	 *             conn.close();
	 *             }
	 *             }
	 *             }
	 */

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
		Connection conn = null;
		try {
			conn = new RbtDbUtilImpl().getConnection();
			this.readAll(conn, configFilePath, importFilePath);

		} finally {
			if (conn != null) {
				conn.close();
			}
		}

		// 產生 sql
		FileUtil fileUtil = new FileUtil();
		fileUtil.setUseUTF8BOM(true);

		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TD_SCHOOL_BASE]");
		fileUtil.addLine("------------------------------------------------------");
		for (LinkedHashMap<String, Object> item : this.schoolBaseMapList) {
			// TD_SCHOOL_BASE
			fileUtil.addLine("--" + item.get("SCHOOL_CHN_NAME"));
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_SCHOOL_BASE", item, true) + ";");
		}

		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TS_USER]");
		fileUtil.addLine("------------------------------------------------------");
		for (LinkedHashMap<String, Object> item : this.tsUserMapList) {
			// TS_USER
			fileUtil.addLine("--" + item.get("CHN_NAME"));
			fileUtil.addLine(SqlUtil.genInsertSQL("TS_USER", item, true) + ";");
		}

		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TS_ROLE_USER]");
		fileUtil.addLine("------------------------------------------------------");
		for (LinkedHashMap<String, Object> item : this.tsRoleUserMapList) {
			// TS_USER
			fileUtil.addLine("--");
			fileUtil.addLine(SqlUtil.genInsertSQL("TS_ROLE_USER", item, true) + ";");
		}

		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TD_SCHOOL_INFO]");
		fileUtil.addLine("------------------------------------------------------");
		for (LinkedHashMap<String, Object> item : this.schoolInfoMapList) {
			// TD_SCHOOL_INFO
			fileUtil.addLine("--" + item.get("SCHOOL_CHN_NAME") + ":" + item.get("UNIT_INFO_UID"));
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_SCHOOL_INFO", item, true) + ";");
		}

		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TD_SCHOOL_YEAR]");
		fileUtil.addLine("------------------------------------------------------");
		for (LinkedHashMap<String, Object> item : this.schoolYearMapList) {
			// TD_SCHOOL_YEAR
			fileUtil.addLine("--" + item.get("SCHOOL_CHN_NAME"));
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_SCHOOL_YEAR", item, true) + ";");
		}

		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TD_YEAR_OTHER_LM]");
		fileUtil.addLine("------------------------------------------------------");

		for (LinkedHashMap<String, Object> item : this.yearOtherLmMapList) {
			// TD_YEAR_OTHER_LM
			fileUtil.addLine("--" + item.get("NAME"));
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_YEAR_OTHER_LM", item, true) + ";");
		}

		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TD_YEAR_TL_SEED_ADD]");
		fileUtil.addLine("------------------------------------------------------");

		for (LinkedHashMap<String, Object> item : this.seedAddList) {
			// TD_YEAR_TL_SEED_ADD
			fileUtil.addLine("--" + item.get("NAME"));
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_YEAR_TL_SEED_ADD", item, true) + ";");
		}

		fileUtil.addLine();
		fileUtil.addLine("------------------------------------------------------");
		fileUtil.addLine("-- [TD_YEAR_TL_SEED_SELECT]");
		fileUtil.addLine("------------------------------------------------------");
		for (LinkedHashMap<String, Object> item : this.seedSelectList) {
			// TD_YEAR_TL_SEED_SELECT
			fileUtil.addLine("--" + item.get("NAME"));
			fileUtil.addLine(SqlUtil.genInsertSQL("TD_YEAR_TL_SEED_SELECT", item, true) + ";");
			fileUtil.addLine();
		}

		// 寫入檔案
		fileUtil.writeToFile(exportSQLFilePath);
	}

	// ===========================================================================================================================
	// 學校資料副程式
	// ===========================================================================================================================
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

		String SCHOOL_BASE_UID = schoolbase.get("SCHOOL_BASE_UID") + "";
		if (this.checkSchoolBase(schoolbase.get("SCHOOL_ID") + "")) {
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

	// ===========================================================================================================================
	//
	// ===========================================================================================================================
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// 參數設定
		//String configFilePath = "C:/workspace/RbtProject/srcAllen/EvaImport.xml";
		//String importFilePath = "h:/0801/環球科技大學-shuo-OK.xls";
		//String exportSQLFilePath = "h:/0801/SCHOOL_AND_LM.sql";

		String configFilePath = "C:/Users/turbo/workspace/RbtProject/srcAllen/EvaImport.xml";
		String importFilePath = "D:/turbo/資料匯入/ImportFile/學校/台南/嘉南藥理科技大學-hmt-OK.xls";
		String exportSQLFilePath = "D:/turbo/資料匯入/OutputFile/SCHOOL_AND_LM.sql";

		EvaImportYearOtherLm evaImportSchool = new EvaImportYearOtherLm();

		// 讀取後直接，匯出 INSERT 語法檔案
		evaImportSchool.readAndExportSql(configFilePath, importFilePath, exportSQLFilePath);

		// 讀取後直接 insert 到 DB
		// evaImportSchool.readAndInsert(configFilePath, importFilePath);
	}
}
