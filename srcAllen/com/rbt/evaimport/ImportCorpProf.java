package com.rbt.evaimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.rbt.util.BeanUtil;
import com.rbt.util.StringUtil;
import com.rbt.util.file.FileUtil;

/**
 * 學校資料處理
 * @author Allen
 */
public class ImportCorpProf extends BaseEvaImport {

	// ==========================================================================
	// 要產生的資料 (INSERT)
	// ==========================================================================
	/**
	 * TD_CORP_YEAR_SCHOOL
	 */
	private List<LinkedHashMap<String, Object>> corpYearSchoolMapList;
	/**
	 * TD_CORP_YEAR_ST
	 */
	private List<LinkedHashMap<String, Object>> yearStMapList;
	/**
	 * TD_CORP_YEAR_INSTITUE
	 */
	private List<LinkedHashMap<String, Object>> yearInstMapList;
	/**
	 * TD_CYS_INSTITUE
	 */
	private List<LinkedHashMap<String, Object>> cysInstMapList;
	/**
	 * TD_CORP_YEAR_WALFARE 計畫申請事業單位年度福利檔
	 */
	private List<LinkedHashMap<String, Object>> yearWalfareMapList;
	/**
	 * TD_CORP_YEAR_WORKPLAN 計畫申請事業單位年度工作崗位檔
	 */
	private List<LinkedHashMap<String, Object>> yearWorkplanMapList;
	/**
	 * TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 高職
	 */
	private List<LinkedHashMap<String, Object>> studyPlanTemplate1MapList;
	/**
	 * TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 二專
	 */
	private List<LinkedHashMap<String, Object>> studyPlanTemplate2MapList;
	/**
	 * TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 二技
	 */
	private List<LinkedHashMap<String, Object>> studyPlanTemplate3MapList;
	/**
	 * TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 四年制
	 */
	private List<LinkedHashMap<String, Object>> studyPlanTemplate5MapList;

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
		// 讀檔 TD_CORP_YEAR_ST
		this.LOG.info("讀取 [ TD_CORP_YEAR_ST ]");
		this.yearStMapList = this.readFile(configFilePath, "TD_CORP_YEAR_ST", importFilePath);

		// 讀檔 TD_CORP_YEAR_SCHOOL
		this.LOG.info("讀取 [ TD_CORP_YEAR_SCHOOL ]");
		this.corpYearSchoolMapList = this.readFile(configFilePath, "TD_CORP_YEAR_SCHOOL", importFilePath);

		// 讀檔 TD_CORP_YEAR_INSTITUE
		this.LOG.info("讀取 [ TD_CORP_YEAR_INSTITUE ]");
		this.yearInstMapList = this.readFile(configFilePath, "TD_CORP_YEAR_INSTITUE", importFilePath);

		// 讀檔 TD_CYS_INSTITUE
		this.LOG.info("讀取 [ TD_CYS_INSTITUE ]");
		this.cysInstMapList = this.readFile(configFilePath, "TD_CYS_INSTITUE", importFilePath);

		// 讀檔 TD_CORP_YEAR_WALFARE 計畫申請事業單位年度福利檔
		this.LOG.info("讀取 [ TD_CORP_YEAR_WALFARE ]");
		this.yearWalfareMapList = this.readFile(configFilePath, "TD_CORP_YEAR_WALFARE", importFilePath);

		// TD_CORP_YEAR_WORKPLAN 計畫申請事業單位年度工作崗位檔
		this.LOG.info("讀取 [ TD_CORP_YEAR_WORKPLAN ]");
		this.yearWorkplanMapList = this.readFile(configFilePath, "TD_CORP_YEAR_WORKPLAN", importFilePath);

		// TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 高職
		this.LOG.info("讀取 [ TD_STUDY_PLAN_TEMPLATE_1 ]");
		this.studyPlanTemplate1MapList = this.readFile(configFilePath, "TD_STUDY_PLAN_TEMPLATE_1", importFilePath);

		// TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 二專
		this.LOG.info("讀取 [ TD_STUDY_PLAN_TEMPLATE_2 ]");
		this.studyPlanTemplate2MapList = this.readFile(configFilePath, "TD_STUDY_PLAN_TEMPLATE_2", importFilePath);

		// TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 二技
		this.LOG.info("讀取 [ TD_STUDY_PLAN_TEMPLATE_3 ]");
		this.studyPlanTemplate3MapList = this.readFile(configFilePath, "TD_STUDY_PLAN_TEMPLATE_3", importFilePath);

		// TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 四年制
		this.LOG.info("讀取 [ TD_STUDY_PLAN_TEMPLATE_5 ]");
		this.studyPlanTemplate5MapList = this.readFile(configFilePath, "TD_STUDY_PLAN_TEMPLATE_5", importFilePath);

		// =====================================
		// 引索
		// =====================================
		// TD_CORP_YEAR_ST
		LinkedHashMap<String, LinkedHashMap<String, Object>> corpYearStMap = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
		// key2
		LinkedHashMap<String, List<String>> corpYearStUidListMap = new LinkedHashMap<String, List<String>>();
		for (LinkedHashMap<String, Object> item : this.yearStMapList) {
			corpYearStMap.put(this.getKey(item), item);

			String key = this.getKeyWithOutName(item);
			List<String> uidList = corpYearStUidListMap.get(key);
			if (uidList == null) {
				uidList = new ArrayList<String>();
				corpYearStUidListMap.put(key, uidList);
			}
			uidList.add(StringUtil.safeTrim(item.get("CORP_YEAR_ST_UID")));
		}

		// TD_CORP_YEAR_SCHOOL
		LinkedHashMap<String, LinkedHashMap<String, Object>> corpYearSchoolMap = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
		for (LinkedHashMap<String, Object> item : this.corpYearSchoolMapList) {
			corpYearSchoolMap.put(this.getKey(item), item);
		}

		// TD_CORP_YEAR_WORKPLAN 計畫申請事業單位年度工作崗位檔
		LinkedHashMap<String, LinkedHashMap<String, Object>> yearWorkPlanMap = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
		for (LinkedHashMap<String, Object> item : this.yearWorkplanMapList) {
			yearWorkPlanMap.put(this.getWorkPlanKey(item), item);
		}

		// =====================================
		// TD_CORP_YEAR_ST 重整
		// =====================================
		for (LinkedHashMap<String, Object> item : this.yearStMapList) {
			// 移除不用 INSERT 欄位
			this.removeUnUse(item);
		}

		// =====================================
		// TD_CORP_YEAR_SCHOOL 重整
		// =====================================
		// TD_CORP_YEAR_SCHOOL
		for (LinkedHashMap<String, Object> item : this.corpYearSchoolMapList) {

			// INITIAL_ALLOWANCE_TYPE
			if ("0".equals(item.get("INITIAL_ALLOWANCE_TYPE"))) {
				// 人員身份需為 3 時
				item.put("INITIAL_ALLOWANCE_TYPE", "1");
			} else if ("2".equals(item.get("INITIAL_ALLOWANCE_TYPE"))) {
				item.put("INITIAL_ALLOWANCE_TYPE", "2");
			}

			// CORP_YEAR_ST_UID
			LinkedHashMap<String, Object> corpYearSt = corpYearStMap.get(this.getKey(item));
			if (corpYearSt == null) {
				System.out.println("CORP_YEAR_ST 已建立之索引");
				for (String key : corpYearStMap.keySet()) {
					System.out.println("[" + key + "]");
				}
				throw new Exception("TD_CORP_YEAR_SCHOOL 參照 CORP_YEAR_ST 時對不上!\r key:[" + this.getKey(item) + "]");
			}
			item.put("CORP_YEAR_ST_UID", corpYearSt.get("CORP_YEAR_ST_UID"));

			// 移除不用 INSERT 欄位
			this.removeUnUse(item);
		}

		// =====================================
		// TD_CORP_YEAR_INSTITUE 重整
		// =====================================
		for (LinkedHashMap<String, Object> item : this.yearInstMapList) {
			// CORP_YEAR_SCHOOL_UID
			item.put("CORP_YEAR_SCHOOL_UID", corpYearSchoolMap.get(this.getKey(item)).get("CORP_YEAR_SCHOOL_UID"));
			// 移除不用 INSERT 欄位
			item.remove("CORP_ID");
			item.remove("ES_TYPE");
			item.remove("ST_TYPE");
		}

		// =====================================
		// TD_CYS_INSTITUE 重整
		// =====================================
		for (LinkedHashMap<String, Object> item : this.cysInstMapList) {
			// CORP_YEAR_SCHOOL_UID
			item.put("CORP_YEAR_SCHOOL_UID", corpYearSchoolMap.get(this.getKey(item)).get("CORP_YEAR_SCHOOL_UID"));
			// 移除欄位
			item.remove("CORP_ID");
			item.remove("ES_TYPE");
			item.remove("ST_TYPE");
			item.remove("INSTITUE_CHN_NAME");
		}

		// =====================================
		// TD_CORP_YEAR_WALFARE 重整
		// =====================================
		for (LinkedHashMap<String, Object> item : this.yearWalfareMapList) {
			// 反轉設定值
			// 宿舍標誌
			this.reversalFlag("HOSTEL_FLAG", item);
			// 提供伙食標誌
			this.reversalFlag("FOOD_FLAG", item);
			// 免費伙食標誌
			this.reversalFlag("FOOD_FREE_FLAG", item);
			// 提供交通車標誌
			this.reversalFlag("TRAFFIC_FLAG", item);
			// 免費交通車標誌
			this.reversalFlag("TRAFFIC_FREE_FLAG", item);
			// 績效獎標誌
			this.reversalFlag("KPI_AWARD_FLAG", item);
			// 年終獎標誌
			this.reversalFlag("YEAR_AWARD_FLAG", item);
			// 學校輔助標誌
			this.reversalFlag("SCHOOL_ASSIT_FLAG", item);
			// 其他費用補助\福利
			if (StringUtil.notEmpty(StringUtil.safeTrim(item.get("OTHER_ASSIST_DESC")))) {
				String OTHER_ASSIST = StringUtil.safeTrim(item.get("OTHER_ASSIST"));
				OTHER_ASSIST += "\r\n" + StringUtil.safeTrim(item.get("OTHER_ASSIST_DESC"));
				item.put("OTHER_ASSIST", OTHER_ASSIST);
			}

			item.remove("OTHER_ASSIST_DESC");
		}

		// =====================================
		// TD_CORP_YEAR_WORKPLAN 重整
		// =====================================
		for (LinkedHashMap<String, Object> item : this.yearWorkplanMapList) {
			// 移除不用 INSERT 欄位
			this.removeUnUse(item);
		}

		// =====================================
		// TD_STUDY_PLAN_TEMPLATE - 1.高職 重整
		// =====================================
		HashSet<String> uidSet = new HashSet();
		this.LOG.info("TD_STUDY_PLAN_TEMPLATE1 調整");
		this.studyPlanTemplate1MapList = this.prepareStudyPlanTempLate(this.studyPlanTemplate1MapList, corpYearStUidListMap, yearWorkPlanMap);
		this.checkUidDuplicate(this.studyPlanTemplate1MapList, uidSet);

		// =====================================
		// TD_STUDY_PLAN_TEMPLATE - 2.二專 重整
		// =====================================
		this.LOG.info("TD_STUDY_PLAN_TEMPLATE2 調整");
		this.studyPlanTemplate2MapList = this.prepareStudyPlanTempLate(this.studyPlanTemplate2MapList, corpYearStUidListMap, yearWorkPlanMap);
		this.checkUidDuplicate(this.studyPlanTemplate2MapList, uidSet);

		// =====================================
		// TD_STUDY_PLAN_TEMPLATE - 3.二技 重整
		// =====================================
		this.LOG.info("TD_STUDY_PLAN_TEMPLATE3 調整");
		this.studyPlanTemplate3MapList = this.prepareStudyPlanTempLate(this.studyPlanTemplate3MapList, corpYearStUidListMap, yearWorkPlanMap);
		this.checkUidDuplicate(this.studyPlanTemplate3MapList, uidSet);

		// =====================================
		// TD_STUDY_PLAN_TEMPLATE - 5.四年制 重整
		// =====================================
		this.LOG.info("TD_STUDY_PLAN_TEMPLATE5 調整");
		this.studyPlanTemplate5MapList = this.prepareStudyPlanTempLate(this.studyPlanTemplate5MapList, corpYearStUidListMap, yearWorkPlanMap);
		this.checkUidDuplicate(this.studyPlanTemplate5MapList, uidSet);


	}

	private void checkUidDuplicate(List<LinkedHashMap<String, Object>> dataList, HashSet<String> uidSet) throws Exception {

		for (LinkedHashMap<String, Object> item : dataList) {
			String TEMPLATE_UID = StringUtil.safeTrim(item.get("TEMPLATE_UID"));
			if (uidSet.contains(TEMPLATE_UID)) {
				throw new Exception("TEMPLATE_UID 重複:" + TEMPLATE_UID);
			}
			uidSet.add(TEMPLATE_UID);
		}
	}

	/**
	 * @param dataMapList
	 * @param corpYearStMap
	 * @throws Exception
	 */
	private List<LinkedHashMap<String, Object>> prepareStudyPlanTempLate(
			List<LinkedHashMap<String, Object>> dataMapList,
			LinkedHashMap<String, List<String>> corpYearStUidListMap,
			LinkedHashMap<String, LinkedHashMap<String, Object>> yearWorkPlanMap
			) throws Exception {

		//
		List<LinkedHashMap<String, Object>> newDataMapList = new ArrayList<LinkedHashMap<String, Object>>();

		// TEMPLATE_GROUP_UID Map
		HashMap<String, String> groupUidMap = new HashMap<String, String>();

		for (LinkedHashMap<String, Object> item : dataMapList) {

			// ===============================
			// 未寫統編時，跳過
			// ===============================
			if (StringUtil.isEmpty(item.get("CORP_ID"))) {
				continue;
			}
			// ===============================
			// TEMPLATE_GROUP_UID
			// ===============================
			// 兜組識別key
			String groupUidKey =
					StringUtil.safeTrim(item.get("CORP_ID")) +
							StringUtil.safeTrim(item.get("ST_TYPE")) +
							StringUtil.safeTrim(item.get("TEMPLATE_TYPE"));

			// 取得已存在的 TEMPLATE_GROUP_UID
			String groupUid = groupUidMap.get(groupUidKey);
			// 不存在時產生一組新的
			if (StringUtil.isEmpty(groupUid)) {
				groupUid = this.function.genUID("[DATE0-14][R][NUM5]");
				groupUidMap.put(groupUidKey, groupUid);
			}
			// put
			item.put("TEMPLATE_GROUP_UID", groupUid);

			// ===============================
			// CORP_YEAR_WORKPLAN_UID
			// ===============================
			LinkedHashMap<String, Object> yearWorkPlan = yearWorkPlanMap.get(this.getWorkPlanKey(item));
			if (yearWorkPlan == null) {
				this.LOG.info("TD_CORP_YEAR_WORKPLAN 已建立之索引");
				for (String key : yearWorkPlanMap.keySet()) {
					this.LOG.info("[" + key + "]");
				}
				throw new Exception("TD_STUDY_PLAN_TEMPLATE 參照 TD_CORP_YEAR_WORKPLAN 時對不上!\r key:[" + this.getWorkPlanKey(item) + "]");
			}
			item.put("CORP_YEAR_WORKPLAN_UID", yearWorkPlan.get("CORP_YEAR_WORKPLAN_UID"));

			// ===============================
			// CORP_YEAR_ST_UID
			// ===============================
			// 取得對應的 uid list
			List<String> corpYearStUidList = corpYearStUidListMap.get(this.getKeyWithOutName(item));
			if (StringUtil.isEmpty(corpYearStUidList)) {
				this.LOG.info(new BeanUtil().showContent("CORP_YEAR_ST 已建立之索引", corpYearStUidList));
				throw new Exception("TD_STUDY_PLAN_TEMPLATE 參照 CORP_YEAR_ST 時對不上!\r key:[" + this.getKey(item) + "]");
			}

			// 移除不用 INSERT 欄位
			this.removeUnUse(item);
			item.remove("WORKPLAN_NAME");

			for (String corpYearStUid : corpYearStUidList) {
				// 複製一個新物件
				LinkedHashMap<String, Object> newItem = new LinkedHashMap<String, Object>();
				for (Entry<String, Object> entry : item.entrySet()) {
					newItem.put(entry.getKey(), entry.getValue());
				}

				// 放入 CORP_YEAR_ST_UID
				newItem.put("CORP_YEAR_ST_UID", corpYearStUid);

				// 重新 gan TEMPLATE_UID
				newItem.put("TEMPLATE_UID", this.function.genUID("[DATE0-18][NUM2]"));

				// 將新物件放入list
				newDataMapList.add(newItem);
			}
		}
		return newDataMapList;
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

		// TD_CORP_YEAR_SCHOOL
		this.prepareInsertSQL(fileUtil, "TD_CORP_YEAR_ST", "", this.yearStMapList);

		// TD_CORP_YEAR_SCHOOL
		this.prepareInsertSQL(fileUtil, "TD_CORP_YEAR_SCHOOL", "", this.corpYearSchoolMapList);

		// TD_CORP_YEAR_INSTITUE
		this.prepareInsertSQL(fileUtil, "TD_CORP_YEAR_INSTITUE", "", this.yearInstMapList);

		// TD_CYS_INSTITUE
		this.prepareInsertSQL(fileUtil, "TD_CYS_INSTITUE", "", this.cysInstMapList);

		// TD_CYS_INSTITUE
		this.prepareInsertSQL(fileUtil, "TD_CORP_YEAR_WALFARE", "", this.yearWalfareMapList);

		// TD_CORP_YEAR_WORKPLAN 計畫申請事業單位年度工作崗位檔
		this.prepareInsertSQL(fileUtil, "TD_CORP_YEAR_WORKPLAN", "", this.yearWorkplanMapList);

		// TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 高職
		this.prepareInsertSQL(fileUtil, "TD_STUDY_PLAN_TEMPLATE", "", this.studyPlanTemplate1MapList);

		// TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 二專
		this.prepareInsertSQL(fileUtil, "TD_STUDY_PLAN_TEMPLATE", "", this.studyPlanTemplate2MapList);

		// TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 二技
		this.prepareInsertSQL(fileUtil, "TD_STUDY_PLAN_TEMPLATE", "", this.studyPlanTemplate3MapList);

		// TD_STUDY_PLAN_TEMPLATE 訓練生輪調計畫範本設定檔 - 四年制
		this.prepareInsertSQL(fileUtil, "TD_STUDY_PLAN_TEMPLATE", "", this.studyPlanTemplate5MapList);

		// =================================================
		// 寫入檔案
		// =================================================
		fileUtil.writeToFile(exportSQLFilePath);
	}

	/**
	 * 取得資料對照欄位值
	 * @param item
	 * @return
	 */
	private String getKey(LinkedHashMap<String, Object> item) {
		String key = StringUtil.safeTrim(item.get("UNIT_INFO_UID")) + "_";
		key += StringUtil.safeTrim(item.get("CORP_ID")) + "_";
		key += StringUtil.safeTrim(item.get("ES_TYPE")) + "_";
		key += StringUtil.safeTrim(item.get("ST_TYPE")) + "_";
		key += StringUtil.safeTrim(item.get("INSTITUE_CHN_NAME")) + "_";
		return key;
	}

	/**
	 * 取得資料對照欄位值
	 * @param item
	 * @return
	 */
	private String getKeyWithOutName(LinkedHashMap<String, Object> item) {
		String key = StringUtil.safeTrim(item.get("UNIT_INFO_UID")) + "_";
		key += StringUtil.safeTrim(item.get("CORP_ID")) + "_";
		key += StringUtil.safeTrim(item.get("ES_TYPE")) + "_";
		key += StringUtil.safeTrim(item.get("ST_TYPE"));
		return key;
	}

	/**
	 * 取得資料對照欄位值
	 * @param item
	 * @return
	 */
	private String getWorkPlanKey(LinkedHashMap<String, Object> item) {
		String key = StringUtil.safeTrim(item.get("UNIT_INFO_UID")) + "_";
		key += StringUtil.safeTrim(item.get("CORP_ID")) + "_";
		key += StringUtil.safeTrim(item.get("ST_TYPE")) + "_";
		key += StringUtil.safeTrim(item.get("WORKPLAN_NAME"));
		return key;
	}

	/**
	 * 移除不用 INSERT 欄位
	 * @param item
	 */
	private LinkedHashMap<String, Object> removeUnUse(LinkedHashMap<String, Object> item) {
		// 移除欄位
		item.remove("UNIT_INFO_UID");
		item.remove("CORP_ID");
		item.remove("ES_TYPE");
		item.remove("ST_TYPE");
		item.remove("INSTITUE_CHN_NAME");

		return item;
	}

	/**
	 * 反轉設定值
	 * @param item
	 */
	private void reversalFlag(String paramName, LinkedHashMap<String, Object> item) {
		// 預設為否
		String flag = "0";
		// 反轉值
		if ("0".equals(StringUtil.safeTrim(item.get(paramName)))) {
			flag = "1";
		}
		// 放回設定值
		item.put(paramName, flag);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// 參數設定 eva
		String configFilePath = "C:/Users/turbo/workspace/RbtProject/srcAllen/EvaImport.xml";
		String importFilePath = "D:/turbo/資料匯入/ImportFile/事業/";
		String exportSQLFilePath = "D:/turbo/資料匯入/OutputFile/事業/";

		// 參數設定 allen
		configFilePath = "C:/workspace/RbtProject/srcAllen/EvaImport.xml";
		importFilePath = "h:/0801/";
		exportSQLFilePath = "h:/0801/CORP_PROF.sql";

		// 讀取後直接，匯出 INSERT 語法檔案
		// new ImportCorpProf().readAndExportSql(configFilePath, importFilePath, exportSQLFilePath);

		ImportCorpProf importCorpProf = new ImportCorpProf();

//		importCorpProf.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-三商行.xls", exportSQLFilePath + "桃園/2-CORP_PROF(桃訓-三商行).sql");
//		importCorpProf.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-王品.xls", exportSQLFilePath + "桃園/2-CORP_PROF(桃訓-王品).sql");
//		importCorpProf.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-宇辰光電.xls", exportSQLFilePath + "桃園/2-CORP_PROF(桃訓-宇辰光電).sql");
//		importCorpProf.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-怡和餐飲股份有限公司.xls", exportSQLFilePath + "桃園/2-CORP_PROF(桃訓-怡和餐飲股份有限公司).sql");
//		importCorpProf.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-昇恒昌.xls", exportSQLFilePath + "桃園/2-CORP_PROF(桃訓-昇恒昌).sql");
//		importCorpProf.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-麥當勞.xls", exportSQLFilePath + "桃園/2-CORP_PROF(桃訓-麥當勞).sql");
		importCorpProf.readAndExportSql(configFilePath, importFilePath + "桃園/桃訓-富利.xls", exportSQLFilePath + "桃園/2-CORP_PROF(桃訓-富利).sql");
	}
}
