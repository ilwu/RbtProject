package com.rbt.evaimport;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.rbt.util.StringUtil;
import com.rbt.util.db.RbtDbUtilImpl;
import com.rbt.util.file.FileUtil;

/**
 * 學校資料處理
 * @author Allen
 */
public class ImportCorpOtherLm extends BaseEvaImport {

	/**
	 * TD_CORP_BASE 學校基本資料
	 */
	private List<LinkedHashMap<String, Object>> corpBaseMapList;
	/**
	 * TS_USER
	 */
	private List<LinkedHashMap<String, Object>> tsUserMapList;
	/**
	 * TS_ROLE_USER
	 */
	private List<LinkedHashMap<String, Object>> tsRoleUserMapList;
	/**
	 * TD_CORP_INFO
	 */
	private List<LinkedHashMap<String, Object>> corpInfoMapList;
	/**
	 * TD_CORP_YEAR
	 */
	private List<LinkedHashMap<String, Object>> corpYearMapList;
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

		this.corpBaseMapList = new ArrayList<LinkedHashMap<String, Object>>();
		this.tsUserMapList = new ArrayList<LinkedHashMap<String, Object>>();
		this.tsRoleUserMapList = new ArrayList<LinkedHashMap<String, Object>>();
		this.corpInfoMapList = new ArrayList<LinkedHashMap<String, Object>>();
		this.corpYearMapList = new ArrayList<LinkedHashMap<String, Object>>();

		// =====================================
		// 讀檔
		// =====================================
		// 讀檔 學校基本資料
		List<LinkedHashMap<String, Object>> importCorpBaseMapList = this.readFile(configFilePath, "CORP", importFilePath);
		// 讀檔 TD_YEAR_OTHER_LM
		this.yearOtherLmMapList = this.readFile(configFilePath, "TD_YEAR_OTHER_LM", importFilePath);
		// 讀檔 TD_YEAR_TL_SEED_ADD
		this.seedAddList = this.readFile(configFilePath, "TD_YEAR_TL_SEED_ADD", importFilePath);
		// 讀檔 TD_YEAR_TL_SEED_SELECT
		this.seedSelectList = this.readFile(configFilePath, "TD_YEAR_TL_SEED_SELECT", importFilePath);

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
		// TD_CORP_BASE、TS_USER、 TS_ROLE_USER 先行處理
		// =====================================
		for (LinkedHashMap<String, Object> corpbase : importCorpBaseMapList) {
			if (!this.checkCorpBase(corpbase.get("CORP_ID") + "")) {
				// TD_CORP_BASE
				this.corpBaseMapList.add(corpbase);
				// TS_USER
				LinkedHashMap<String, Object> tsUser = this.perpareTsUser(corpbase, this.function, conn);
				this.tsUserMapList.add(tsUser);
				// TS_ROLE_USER
				this.tsRoleUserMapList.add(this.perpareTsRoleUser(corpbase, tsUser.get("USER_UID") + "", this.function, conn));
			}
		}

		// =====================================
		// TD_CORP_INFO 先行處理
		// =====================================
		for (LinkedHashMap<String, Object> corpbase : importCorpBaseMapList) {
			// TD_CORP_INFO
			if (!this.checkCorpInfo(corpbase.get("CORP_ID") + "")) {
				// 5個區都要
				this.corpInfoMapList.add(this.perpareCorpInfo(corpbase, "1", this.function, conn));
				this.corpInfoMapList.add(this.perpareCorpInfo(corpbase, "2", this.function, conn));
				this.corpInfoMapList.add(this.perpareCorpInfo(corpbase, "3", this.function, conn));
				this.corpInfoMapList.add(this.perpareCorpInfo(corpbase, "4", this.function, conn));
				this.corpInfoMapList.add(this.perpareCorpInfo(corpbase, "5", this.function, conn));
			}
		}

		// =====================================
		// TD_CORP_YEAR 先行處理
		// =====================================
		// 並建立以 CORP_ID 為索引的 CORP_YEAR_UID MAP
		LinkedHashMap<String, String> corpYearUidMap = new LinkedHashMap<String, String>();
		for (LinkedHashMap<String, Object> corpbase : importCorpBaseMapList) {
			LinkedHashMap<String, Object> corpYear = this.perpareCorpYear(corpbase, this.function, conn);
			this.corpYearMapList.add(corpYear);
			corpYearUidMap.put(corpYear.get("CORP_ID") + "", corpYear.get("CORP_YEAR_UID") + "");
		}

		for (Entry<String, String> entry : corpYearUidMap.entrySet()) {
			System.out.print("key:[" + entry.getKey() + "] value:" + entry.getValue());

		}
		// System.out.println(new BeanUtil().showContent(importCorpBaseMapList));
		// System.out.println(new BeanUtil().showContent(this.corpYearMapList));

		// =====================================
		// TD_YEAR_OTHER_LM 先行處理
		// =====================================
		for (LinkedHashMap<String, Object> dateMap : this.yearOtherLmMapList) {
			// MAPPING_UID (CORP_YEAR_UID)
			String CORP_ID = StringUtil.safeTrim(dateMap.get("SCHOOL_ID"));
			String MAPPING_UID = StringUtil.safeTrim(corpYearUidMap.get(CORP_ID));
			if (StringUtil.isEmpty(MAPPING_UID)) {
				throw new Exception("TD_YEAR_OTHER_LM 的 CORP_ID 無法參照 學校基本資料匯入資料! CORP_ID:[" + CORP_ID + "]");
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
			// MAPPING_UID (CORP_YEAR_UID)
			String CORP_ID = StringUtil.safeTrim(dateMap.get("SCHOOL_ID"));
			String MAPPING_UID = StringUtil.safeTrim(corpYearUidMap.get(CORP_ID));
			if (StringUtil.isEmpty(MAPPING_UID)) {
				throw new Exception("TD_YEAR_OTHER_LM 的 CORP_ID 無法參照 事業單位基本資料匯入資料! CORP_ID:[" + CORP_ID + "]");
			}
			dateMap.put("MAPPING_UID", MAPPING_UID);

			// STATION_TYPE
			String STATION_TYPE = StringUtil.safeTrim(dateMap.get("STATION_TYPE"));
			String name = StringUtil.safeTrim(dateMap.get("NAME"));
			String idNum = StringUtil.safeTrim(dateMap.get("IDCARD_NUM"));

			if ("3".equals(STATION_TYPE)) {
				// 人員身份需為 3 時
				dateMap.put("STATION_UID", "03");

			} else if (!this.checkTdSeedIsExist(conn, name, idNum)) {
				// 人員身份為 1.2 時 且存在於 TD_SEED
				if ("1".equals(STATION_TYPE)) {
					System.out.println("STEP1");
					dateMap.put("STATION_UID", "01");
				} else if ("2".equals(STATION_TYPE)) {
					System.out.println("STEP2");
					dateMap.put("STATION_UID", "02");
				} else {
					dateMap.put("STATION_UID", "03");
				}
			} else {
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
			// MAPPING_UID (CORP_YEAR_UID)
			String CORP_ID = StringUtil.safeTrim(dateMap.get("SCHOOL_ID"));
			String MAPPING_UID = StringUtil.safeTrim(corpYearUidMap.get(CORP_ID));
			if (StringUtil.isEmpty(MAPPING_UID)) {
				throw new Exception("TD_YEAR_OTHER_LM 的 CORP_ID 無法參照 事業單位基本資料匯入資料! CORP_ID:[" + CORP_ID + "]");
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
			String name = StringUtil.safeTrim(dateMap.get("NAME"));
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
	}

	private boolean checkTdSeedIsExist(Connection conn, String name, String idNum) throws ClassNotFoundException, IOException, Exception {

		StringBuffer varname1 = new StringBuffer();
		varname1.append("SELECT DISTINCT a.IDCARD_NUM ");
		varname1.append("FROM   TD_SEED a ");
		varname1.append("	   join TD_SEED_APPLY b ");
		varname1.append("		 ON a.IDCARD_NUM = b.IDCARD_NUM ");
		varname1.append("WHERE  ( b.CHN_NAME = '" + name + "' ");
		varname1.append("		  OR a.IDCARD_NUM = '" + idNum + "' ) ");

		return new RbtDbUtilImpl().query(conn, varname1.toString(), null).size() > 0;
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

		// TD_CORP_BASE
		this.prepareInsertSQL(fileUtil, "TD_CORP_BASE", "CORP_CHN_NAME", this.corpBaseMapList);

		// TS_USER
		this.prepareInsertSQL(fileUtil, "TS_USER", "CHN_NAME", this.tsUserMapList);

		// TS_ROLE_USER
		this.prepareInsertSQL(fileUtil, "TS_ROLE_USER", "", this.tsRoleUserMapList);

		// TD_CORP_INFO
		this.prepareInsertSQL(fileUtil, "TD_CORP_INFO", "CORP_CHN_NAME", this.corpInfoMapList);

		// TD_CORP_YEAR
		this.prepareInsertSQL(fileUtil, "TD_CORP_YEAR", "CORP_CHN_NAME", this.corpYearMapList);

		// TD_YEAR_OTHER_LM
		this.prepareInsertSQL(fileUtil, "TD_YEAR_OTHER_LM", "NAME", this.yearOtherLmMapList);

		// TD_YEAR_TL_SEED_ADD
		this.prepareInsertSQL(fileUtil, "TD_YEAR_TL_SEED_ADD", "NAME", this.seedAddList);

		// TD_YEAR_TL_SEED_SELECT
		this.prepareInsertSQL(fileUtil, "TD_YEAR_TL_SEED_SELECT", "NAME", this.seedSelectList);

		// 寫入檔案
		fileUtil.writeToFile(exportSQLFilePath);
	}

	// ===========================================================================================================================
	// 學校資料副程式
	// ===========================================================================================================================
	/**
	 * @param CORP_ID
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean checkCorpBase(String CORP_ID) throws ClassNotFoundException, IOException, Exception {
		return (new RbtDbUtilImpl().query("SELECT 1 FROM TD_CORP_BASE WHERE CORP_ID='" + CORP_ID + "'").size() > 0);
	}

	/**
	 * @param USER_ID
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean checkTsUser(String CROP_ID) throws ClassNotFoundException, IOException, Exception {
		return (new RbtDbUtilImpl().query("SELECT 1 FROM TS_USER WHERE USER_ID='" + CROP_ID + "'").size() > 0);
	}

	/**
	 * @param SCHOOL_ID
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean checkCorpInfo(String CORP_ID) throws ClassNotFoundException, IOException, Exception {
		return (new RbtDbUtilImpl().query("SELECT 1 FROM TD_CORP_INFO WHERE CORP_ID='" + CORP_ID + "'").size() > 0);
	}

	/**
	 * 準備 TS_USER 資料
	 * @param corpbase
	 * @return
	 */
	public LinkedHashMap<String, Object> perpareTsUser(LinkedHashMap<String, Object> corpbase, EvaImportFunctionImpl func, Connection conn) {

		LinkedHashMap<String, Object> tsUser = new LinkedHashMap<String, Object>();

		tsUser.put("USER_UID", func.genUID("[DATE0-14][R][NUM5]"));
		tsUser.put("USER_TYPE", "20");
		tsUser.put("USER_ID", corpbase.get("CORP_ID"));
		tsUser.put("PASSWORD", corpbase.get("CORP_ID"));
		tsUser.put("CHN_NAME", corpbase.get("CORP_CHN_NAME"));
		tsUser.put("ENG_NAME", corpbase.get("CORP_ENG_NAME"));
		tsUser.put("UNIT_TYPE", "2");
		tsUser.put("UNIT_UID", corpbase.get("CORP_BASE_UID"));
		tsUser.put("UNIT_NAME", corpbase.get("CORP_CHN_NAME"));
		tsUser.put("CON_CITY_UID", corpbase.get("CITY_UID"));
		tsUser.put("CON_TOWN_UID", corpbase.get("TOWN_UID"));
		tsUser.put("CON_POST_ID", corpbase.get("POST_ID"));
		tsUser.put("CON_ADDRESS", corpbase.get("CHN_ADDRESS"));
		tsUser.put("LOCKED", "1");
		tsUser.put("START_DATE", corpbase.get("APPLY_DATE"));
		tsUser.put("END_DATE", "2020-12-31");
		tsUser.put("USER_STATUS", "1");
		tsUser.put("ADD_UID", corpbase.get("ADD_UID"));
		tsUser.put("ADD_DATE", corpbase.get("ADD_DATE"));
		tsUser.put("ADD_TIME", corpbase.get("ADD_TIME"));
		tsUser.put("LUPD_UID", corpbase.get("LUPD_UID"));
		tsUser.put("LUPD_DATE", corpbase.get("LUPD_DATE"));
		tsUser.put("LUPD_TIME", corpbase.get("LUPD_TIME"));
		tsUser.put("UNIT_INFO_UID", corpbase.get("UNIT_INFO_UID"));
		tsUser.put("ISWORK", "Y");
		return tsUser;
	}

	/**
	 * 準備 TS_USER 資料
	 * @param corpbase
	 * @return
	 */
	public LinkedHashMap<String, Object> perpareTsRoleUser(LinkedHashMap<String, Object> corpbase, String USER_UID, EvaImportFunctionImpl func, Connection conn) {

		LinkedHashMap<String, Object> tsUser = new LinkedHashMap<String, Object>();
		tsUser.put("ROLE_USER_UID", func.genUID("[DATE0-14][R][NUM5]"));
		tsUser.put("USER_UID", USER_UID);
		tsUser.put("ROLE_UID", "20100805070957500001");
		tsUser.put("ADD_UID", corpbase.get("ADD_UID"));
		tsUser.put("ADD_DATE", corpbase.get("ADD_DATE"));
		tsUser.put("ADD_TIME", corpbase.get("ADD_TIME"));
		tsUser.put("LUPD_UID", corpbase.get("LUPD_UID"));
		tsUser.put("LUPD_DATE", corpbase.get("LUPD_DATE"));
		tsUser.put("LUPD_TIME", corpbase.get("LUPD_TIME"));
		return tsUser;
	}

	/**
	 * TD_CORP_INFO
	 * @param corpbase
	 * @param UNIT_INFO_UID
	 * @param func
	 * @param conn
	 * @return
	 */
	public LinkedHashMap<String, Object> perpareCorpInfo(
			LinkedHashMap<String, Object> corpbase, String UNIT_INFO_UID,
			EvaImportFunctionImpl func, Connection conn) {

		LinkedHashMap<String, Object> corpInfo = new LinkedHashMap<String, Object>();

		corpInfo.put("CORP_INFO_UID", func.genUID("[DATE0-14][R][NUM5]"));
		corpInfo.put("CORP_BASE_UID", corpbase.get("CORP_BASE_UID"));
		corpInfo.put("CORP_ID", corpbase.get("CORP_ID"));
		corpInfo.put("CORP_CHN_NAME", corpbase.get("CORP_CHN_NAME"));
		corpInfo.put("CORP_ENG_NAME", corpbase.get("CORP_ENG_NAME"));
		corpInfo.put("CITY_UID", corpbase.get("CITY_UID"));
		corpInfo.put("TOWN_UID", corpbase.get("TOWN_UID"));
		corpInfo.put("POST_ID", corpbase.get("POST_ID"));
		corpInfo.put("CHN_ADDRESS", corpbase.get("CHN_ADDRESS"));
		corpInfo.put("ENG_ADDRESS", corpbase.get("ENG_ADDRESS"));
		corpInfo.put("OFFICAL_WEBSET", corpbase.get("OFFICAL_WEBSET"));
		corpInfo.put("IS_APPLY_TOGETHER", "0");
		corpInfo.put("CAPTIAL_AMT", corpbase.get("CAPTIAL_AMT"));
		corpInfo.put("ON_DUTY_MAN", corpbase.get("ON_DUTY_MAN"));
		corpInfo.put("INSURE_EMPLOYEE", corpbase.get("INSURE_EMPLOYEE"));
		corpInfo.put("INSURE_DATE", corpbase.get("INSURE_DATE"));
		corpInfo.put("ST_GROUP_UID", "1");
		corpInfo.put("LM_NAME", corpbase.get("LM_NAME"));
		corpInfo.put("LM_TITLE", corpbase.get("LM_TITLE"));
		corpInfo.put("LM_PHONE", corpbase.get("LM_PHONE"));
		corpInfo.put("LM_BRANCH_PHONE", corpbase.get("LM_BRANCH_PHONE"));
		corpInfo.put("LM_MOBILE_PHONE", corpbase.get("LM_MOBILE_PHONE"));
		corpInfo.put("LM_FAX", corpbase.get("LM_FAX"));
		corpInfo.put("LM_EMAIL", corpbase.get("LM_EMAIL"));
		corpInfo.put("BELONG_UNIT", corpbase.get("BELONG_UNIT"));
		corpInfo.put("TL_NAME", corpbase.get("TL_NAME"));
		corpInfo.put("TL_PHONE", corpbase.get("TL_PHONE"));
		corpInfo.put("TL_BRANCH_PHONE", corpbase.get("TL_BRANCH_PHONE"));
		corpInfo.put("ADD_UID", corpbase.get("ADD_UID"));
		corpInfo.put("ADD_DATE", corpbase.get("ADD_DATE"));
		corpInfo.put("ADD_TIME", corpbase.get("ADD_TIME"));
		corpInfo.put("LUPD_UID", corpbase.get("LUPD_UID"));
		corpInfo.put("LUPD_DATE", corpbase.get("LUPD_DATE"));
		corpInfo.put("LUPD_TIME", corpbase.get("LUPD_TIME"));
		corpInfo.put("UNIT_INFO_UID", UNIT_INFO_UID);
		corpInfo.put("PRODUCT_SERVICE", corpbase.get("PRODUCT_SERVICE"));
		corpInfo.put("DEAL_IN_AREA", corpbase.get("DEAL_IN_AREA"));
		corpInfo.put("RESUME_CHN", corpbase.get("RESUME_CHN"));
		corpInfo.put("RESUME_ENG", corpbase.get("RESUME_ENG"));
		return corpInfo;
	}

	/**
	 * TD_CORP_INFO
	 * @param schoolbase
	 * @param UNIT_INFO_UID
	 * @param func
	 * @param conn
	 * @return
	 * @throws Exception
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public LinkedHashMap<String, Object> perpareCorpYear(
			LinkedHashMap<String, Object> corpbase,
			EvaImportFunctionImpl func, Connection conn) throws ClassNotFoundException, IOException, Exception {

		LinkedHashMap<String, Object> corpYear = new LinkedHashMap<String, Object>();

		corpYear.put("CORP_YEAR_UID", func.genUID("[DATE0-14][R][NUM5]"));

		String CORP_BASE_UID = corpbase.get("CORP_BASE_UID") + "";
		if (this.checkCorpBase(corpbase.get("CORP_ID") + "")) {
			CORP_BASE_UID = func.select(
					"CORP_BASE_UID",
					"select corp_base_uid from td_corp_base where corp_id ='" + corpbase.get("CORP_ID") + "'",
					null, conn);
		}

		corpYear.put("CORP_BASE_UID", CORP_BASE_UID);
		corpYear.put("CLASS_TYPE_UID", "20120911003905200001"); // 年度代碼
		corpYear.put("CORP_ID", corpbase.get("CORP_ID"));
		corpYear.put("CORP_CHN_NAME", corpbase.get("CORP_CHN_NAME"));
		corpYear.put("CORP_ENG_NAME", this.trnsSpace(corpbase.get("CORP_ENG_NAME")));
		corpYear.put("CITY_UID", corpbase.get("CITY_UID"));
		corpYear.put("TOWN_UID", corpbase.get("TOWN_UID"));
		corpYear.put("POST_ID", corpbase.get("POST_ID"));
		corpYear.put("CHN_ADDRESS", corpbase.get("CHN_ADDRESS"));
		corpYear.put("ENG_ADDRESS", corpbase.get("ENG_ADDRESS"));
		corpYear.put("OFFICAL_WEBSET", corpbase.get("OFFICAL_WEBSET"));
		corpYear.put("PRODUCT_SERVICE", corpbase.get("PRODUCT_SERVICE"));
		corpYear.put("DEAL_IN_AREA", corpbase.get("DEAL_IN_AREA"));

		if ("1".equals(corpbase.get("IS_APPLY_TOGETHER"))) {
			// 人員身份需為 3 時
			corpYear.put("IS_APPLY_TOGETHER", "0");

		} else {
			corpYear.put("IS_APPLY_TOGETHER", "1");
		}

		corpYear.put("CAPTIAL_AMT", corpbase.get("CAPTIAL_AMT"));
		corpYear.put("ON_DUTY_MAN", corpbase.get("ON_DUTY_MAN"));
		corpYear.put("INSURE_EMPLOYEE", corpbase.get("INSURE_EMPLOYEE"));
		corpYear.put("INSURE_DATE", corpbase.get("INSURE_DATE"));
		corpYear.put("ST_GROUP_UID", "1");
		corpYear.put("LM_NAME", this.trnsSpace(corpbase.get("LM_NAME")));
		corpYear.put("LM_TITLE", corpbase.get("LM_TITLE"));
		corpYear.put("LM_PHONE", this.trnsSpace(corpbase.get("LM_PHONE")));
		corpYear.put("LM_BRANCH_PHONE", corpbase.get("LM_BRANCH_PHONE"));
		corpYear.put("LM_MOBILE_PHONE", corpbase.get("LM_MOBILE_PHONE"));
		corpYear.put("LM_FAX", this.trnsSpace(corpbase.get("LM_FAX")));
		corpYear.put("LM_EMAIL", this.trnsSpace(corpbase.get("LM_EMAIL")));
		corpYear.put("BELONG_UNIT", corpbase.get("BELONG_UNIT"));
		corpYear.put("TL_NAME", this.trnsSpace(this.trnsSpace(this.trnsSpace(corpbase.get("TL_NAME")))));
		corpYear.put("TL_PHONE", this.trnsSpace(this.trnsSpace(corpbase.get("TL_PHONE"))));
		corpYear.put("TL_BRANCH_PHONE", corpbase.get("TL_BRANCH_PHONE"));
		corpYear.put("RESUME_CHN", corpbase.get("RESUME_CHN"));
		corpYear.put("RESUME_ENG", corpbase.get("RESUME_ENG"));
		corpYear.put("MEMO", corpbase.get("MEMO"));
		corpYear.put("FREEZE_FLAG", "0");
		corpYear.put("APPLY_UID", corpbase.get("APPLY_UID"));
		corpYear.put("APPLY_DATE", corpbase.get("APPLY_DATE"));
		corpYear.put("APPLY_TIME", corpbase.get("APPLY_TIME"));
		corpYear.put("AUTH_UID", corpbase.get("ASSIGN_UID"));
		corpYear.put("AUTH_DATE", corpbase.get("APPLY_DATE"));
		corpYear.put("AUTH_TIME", corpbase.get("APPLY_TIME"));
		corpYear.put("STATUS", "9");
		corpYear.put("ADD_UID", corpbase.get("ADD_UID"));
		corpYear.put("ADD_DATE", corpbase.get("ADD_DATE"));
		corpYear.put("ADD_TIME", corpbase.get("ADD_TIME"));
		corpYear.put("LUPD_UID", corpbase.get("LUPD_UID"));
		corpYear.put("LUPD_DATE", corpbase.get("LUPD_DATE"));
		corpYear.put("LUPD_TIME", corpbase.get("LUPD_TIME"));
		// AUTH_MEMO
		corpYear.put("APPLY_PASS_FLAG", "0");
		corpYear.put("INTRO_PASS_FLAG", "0");
		corpYear.put("ARP_PASS_FLAG", "0");
		corpYear.put("STUDY_PLAN_PASS_FLAG", "0");
		corpYear.put("UNIT_INFO_UID", corpbase.get("UNIT_INFO_UID"));
		corpYear.put("ASSIGN_DATE", corpbase.get("ASSIGN_DATE"));
		corpYear.put("ASSIGN_TIME", corpbase.get("ASSIGN_TIME"));
		corpYear.put("ASSIGN_UID", corpbase.get("ASSIGN_UID"));
		// corpYear.put("RETURN_DATE", schoolbase.get("XXXXXXXXXXXXXXXXXXXXXXXXXXX"));

		return corpYear;
	}

	// ===========================================================================================================================
	//
	// ===========================================================================================================================
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// 參數設定 eva
		String configFilePath = "C:/Users/turbo/workspace/RbtProject/srcAllen/EvaImport.xml";
		String importFilePath = "D:/turbo/資料匯入/ImportFile/事業/";
		String exportSQLFilePath = "D:/turbo/資料匯入/OutputFile/事業/";

		// allen
		configFilePath = "C:/workspace/RbtProject/srcAllen/EvaImport.xml";
		importFilePath = "h:/0801/";
		exportSQLFilePath = "h:/0801/sql/";

		// 讀取後直接，匯出 INSERT 語法檔案
		// ImportCorpLm.readAndExportSql(configFilePath, importFilePath, exportSQLFilePath);

		ImportCorpOtherLm importCorpLm = new ImportCorpOtherLm();
		// importCorpLm.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-三商行.xls", exportSQLFilePath + "桃園/桃訓-三商行/1CORP_AND_LM.sql");
		// importCorpLm.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-王品.xls", exportSQLFilePath + "桃園/桃訓-王品/1CORP_AND_LM.sql");
		// importCorpLm.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-宇辰光電.xls", exportSQLFilePath + "桃園/桃訓-宇辰光電/1CORP_AND_LM.sql");
		// importCorpLm.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-怡和餐飲股份有限公司.xls", exportSQLFilePath + "桃園/桃訓-怡和餐飲股份有限公司/1CORP_AND_LM.sql");
		// importCorpLm.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-昇恒昌.xls", exportSQLFilePath + "桃園/桃訓-昇恒昌/1CORP_AND_LM.sql");
		// importCorpLm.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-麥當勞.xls", exportSQLFilePath + "桃園/桃訓-麥當勞/1CORP_AND_LM.sql");
		importCorpLm.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-富利.xls", exportSQLFilePath + "桃園/桃訓-富利/1CORP_AND_LM.sql");

		// 讀取後直接 insert 到 DB
		// evaImportSchool.readAndInsert(configFilePath, importFilePath);
	}
}
