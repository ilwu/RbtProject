/**
 *
 */
package com.rbt.cla;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.rbt.util.BeanUtil;
import com.rbt.util.DateUtil;
import com.rbt.util.StringUtil;
import com.rbt.util.db.RbtDbUtilImpl;
import com.rbt.util.exceloperate.ExcelImporter;
import com.rbt.util.exceloperate.bean.impt.config.ImportConfigInfo;
import com.rbt.util.exceloperate.config.ImportConfigReader;

/**
 * 技檢中心臨時證換補發匯入
 * @author Allen
 */
public class TechtramImporter {

	RbtDbUtilImpl dbUtil = null;
	String IP = (InetAddress.getLocalHost().toString()).split("/")[1];

	/**
	 * @throws Exception
	 *
	 */
	public TechtramImporter() throws Exception {
		// 參數設定
		String configFilePath = "h:/換補發匯入/config.xml";
		String importFilePath = "h:/換補發匯入/00-103留用移資訊刪校正缺生效.xls";

		//測試機
		this.dbUtil =
				new RbtDbUtilImpl(
						RbtDbUtilImpl.DRIVER_Oracle,
						"jdbc:oracle:thin:@118.163.126.175:1521:evtadb1",
						"cla",
						"clalabor", 5);

//		this.dbUtil =
//				new RbtDbUtilImpl(
//						RbtDbUtilImpl.DRIVER_Oracle,
//						"jdbc:oracle:thin:@10.100.2.1:1521:LABOR",
//						"cla",
//						"clalabor", 5);

		//===========================================
		//讀取
		//===========================================
		List<LinkedHashMap<String, Object>> importDataList = this.readFile(configFilePath, "techtram", importFilePath);


		//===========================================
		//DB init
		//===========================================

		Connection conn = null;
		try {
			conn = this.dbUtil.getConnection();
			conn.setAutoCommit(false);

			for (LinkedHashMap<String, Object> importDataMap : importDataList) {

				System.out.println(new BeanUtil().showContent(importDataMap));

				//查詢證照資料
				LinkedHashMap<String, Object> userData =
						this.getUserData(conn,
								StringUtil.safeTrim(importDataMap.get("CRDITNO")),
								StringUtil.safeTrim(importDataMap.get("IDNO")));

				//中介紀錄資料
				LinkedHashMap<String, Object> metaData = new LinkedHashMap<String, Object>();

				//insert TechTram
				this.insertTechTram(conn, importDataMap, userData, metaData);
				//insert SendRec
				this.insertSendRec(conn, importDataMap, userData, metaData);

				//若狀態為核准, 需 sUtl_UpdateTechm sUtl_UpdateTechd sUtl_UpdateTechtran
				//只是更新畫面資料
				//updateTECHM
				//this.updateTECHM(conn, importDataMap, userData, metaData);

				this.insertTechtranChk(conn, importDataMap, userData, metaData);
			}

			conn.commit();

		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	private void insertTechTram(
			Connection conn,
			LinkedHashMap<String, Object> importDataMap, LinkedHashMap<String, Object> userData, LinkedHashMap<String, Object> metaData) throws Exception {

		LinkedHashMap<String, Object> insertParam =
				new LinkedHashMap<String, Object>();

		//CASENO	案號
		String CASENO = this.getCaseNo(conn);
		metaData.put("CASENO", CASENO);
		insertParam.put("CASENO", CASENO);
		//APPLYDATE	申請日期
		insertParam.put("APPLYDATE", DateUtil.getDateOfROC(DateUtil.getToday()));
		//APPWAY	申請方式
		insertParam.put("APPWAY", "1");
		//IDNO	身分證號
		insertParam.put("IDNO", StringUtil.safeTrim(importDataMap.get("IDNO")));
		//NAME	姓名
		insertParam.put("NAME", StringUtil.safeTrim(importDataMap.get("NAME")));
		//SEX	性別
		insertParam.put("SEX", StringUtil.safeTrim(userData.get("SEX")));
		//ENNAME	英文姓名
		insertParam.put("ENNAME", StringUtil.safeTrim(userData.get("ENNAME")));
		//BIRDTE	生日
		insertParam.put("BIRDTE", StringUtil.safeTrim(userData.get("BIRDTE")));
		//EDU	學歷
		insertParam.put("EDU", StringUtil.safeTrim(userData.get("EDU")));
		//CZIP	通訊地址郵遞區號
		insertParam.put("CZIP", StringUtil.safeTrim(userData.get("CZIP")));
		//CADR	通訊地址
		insertParam.put("CADR", StringUtil.safeTrim(userData.get("CADR")));
		//FZIP	戶籍地址郵遞區號
		insertParam.put("FZIP", StringUtil.safeTrim(userData.get("FZIP")));
		//FADR	戶籍地址
		insertParam.put("FADR", StringUtil.safeTrim(userData.get("FADR")));
		//OTEL	電話-公
		//HTEL	電話-私
		insertParam.put("HTEL", StringUtil.safeTrim(userData.get("TEL")));
		//MTEL	行動電話
		insertParam.put("MTEL", StringUtil.safeTrim(userData.get("PHONE")));
		//PNO	職類
		insertParam.put("PNO", StringUtil.safeTrim(userData.get("PNO")));
		//EGR	級別
		insertParam.put("EGR", StringUtil.safeTrim(userData.get("EGR")));
		//OCRDITNO	技術士證總編號-原
		insertParam.put("OCRDITNO", StringUtil.safeTrim(userData.get("OCRDITNO")));
		//AREAC	技術士證地區編號
		insertParam.put("AREAC", StringUtil.safeTrim(userData.get("AREAC")));
		//ZONENO	區域流水號
		insertParam.put("ZONENO", StringUtil.safeTrim(userData.get("ZONENO")));
		//ISSUEDATE	發證生效日
		insertParam.put("ISSUEDATE", StringUtil.safeTrim(userData.get("ISSUEDATE")));
		//OPITEMS	術科細項
		insertParam.put("OPITEMS", StringUtil.safeTrim(userData.get("OPITEMS")));
		//INDATE	作業日期
		String INDATE = this.convertROC(StringUtil.safeTrim(importDataMap.get("INDATE")));
		metaData.put("INDATE", INDATE);
		insertParam.put("INDATE", INDATE);
		//CRDITNO	技術士證總編號
		insertParam.put("CRDITNO", StringUtil.safeTrim(importDataMap.get("CRDITNO")));
		//REMARK1	備註
		insertParam.put("REMARK1", StringUtil.safeTrim(importDataMap.get("REMARK1")));
		//STATUS	狀態 (申請中/收件 ,核准 ,不核准)
		insertParam.put("STATUS", "1");
		//REMARK	審核不合格原因
		insertParam.put("REMARK", "");
		//APPFIRST	第一次申請
		//APPKIND	證書/證照
		insertParam.put("APPKIND", "1");
		//ISSUEKIND	申請類別 (2:申請新總編號)
		insertParam.put("ISSUEKIND", "1");
		//APPLIST	換/補發原因
		insertParam.put("APPLIST", "06");
		//APPDESC	換/補發原因說明
		insertParam.put("APPDESC", "");
		//APPPAPERS	應附證件
		insertParam.put("APPPAPERS", "");
		//LICENSENO	印製編號
		insertParam.put("LICENSENO", StringUtil.safeTrim(userData.get("LICENSENO")));
		//TAKESELF	自領
		insertParam.put("TAKESELF", "0");
		//FREE	免費
		insertParam.put("FREE", "0");
		//APPLSEQNO	撥號申請
		//OKBOOKDATE	報送日期
		//PRTDATE	列印日期
		//MATCHDATE	媒合日期
		//STATE	媒合狀態
		insertParam.put("STATE", "");
		//SNO	掛號流水號
		String SNO = this.getSNO(conn);
		metaData.put("SNO", SNO);
		insertParam.put("SNO", SNO);
		//WEBSEQ	媒合條碼
		//MODUSERID	異動者帳號
		insertParam.put("MODUSERID", "SYSADMIN");
		//MODUSERNAME	異動者姓名
		insertParam.put("MODUSERNAME", "臨時證換發轉入");
		//MODIP	異動ip
		insertParam.put("MODIP", this.IP);
		//MODTIME	異動時間
		insertParam.put("MODTIME", DateUtil.getCurrentDateTime(DateUtil.TYPE_DATETIME, DateUtil.STYLE_ROC));
		//SEQ	流水號
		insertParam.put("SEQ", "01");
		//PRT2DATE	繳費單列印日期
		//SEGAMT	繳費單 證照費
		//PNAME	職類名稱
		insertParam.put("PNAME", StringUtil.safeTrim(userData.get("PNO_NAME")));
		//REASONKIND	換發原因
		insertParam.put("REASONKIND", 1);
		//OTHER	(null)
		insertParam.put("OTHER", "留用");
		//EMAIL	(null)
		insertParam.put("EMAIL", StringUtil.safeTrim(importDataMap.get("EMAIL")));
		//OLDOTHER	原發證基本資料誤植原因

		//System.out.println(new BeanUtil().showContent("TECHTRAN" , insertParam));
		this.dbUtil.insert(conn, "TECHTRAN", insertParam);
	}

	/**
	 * @param dateStr
	 * @return
	 * @throws Exception
	 */
	private String convertROC(String dateStr) throws Exception{

		dateStr = StringUtil.safeTrim(dateStr);

		if(dateStr.indexOf("/") == -1){

			if(dateStr.matches("[0-9]{7}")){
				return dateStr;
			}
			throw new Exception("日期不符合 yyyy/mm/dd:[" + dateStr + "]");
		}

		String dateAry[] =  dateStr.split("/");
		String year = "" + (Integer.parseInt(dateAry[0]) - 1911);
		year = StringUtil.padding(year, "0", 3, true);
		String month = "" + (Integer.parseInt(dateAry[1]));
		month = StringUtil.padding(month, "0", 2, true);
		String day = "" + (Integer.parseInt(dateAry[2]));
		day = StringUtil.padding(day, "0", 2, true);

		return year + month + day;
	}

	/**
	 * 寫掛號郵寄檔
	 * @param conn
	 * @param importDataMap
	 * @param userData
	 * @throws Exception
	 */
	private void insertSendRec(
			Connection conn,
			LinkedHashMap<String, Object> importDataMap, LinkedHashMap<String, Object> userData, LinkedHashMap<String, Object> metaData) throws Exception {

		LinkedHashMap<String, Object> insertParam =
				new LinkedHashMap<String, Object>();

		//SNO	流水號
		insertParam.put("SNO", StringUtil.safeTrim(metaData.get("SNO")));
		//PNO	職類
		insertParam.put("PNO", StringUtil.safeTrim(userData.get("PNO")));
		//EGR	級別
		insertParam.put("EGR", StringUtil.safeTrim(userData.get("EGR")));
		//AENO	准考證號
		//IDNO	身份證號
		insertParam.put("IDNO", StringUtil.safeTrim(importDataMap.get("IDNO")));
		//EXAMKIND	作業類別
		insertParam.put("EXAMKIND", "X");
		//ISSUEKIND	檢定類別
		insertParam.put("ISSUEKIND", "4");
		//CRDITNO	技術士證總編號(流水號)
		insertParam.put("CRDITNO", StringUtil.safeTrim(importDataMap.get("CRDITNO")));
		//INKIND	入帳總類
		//MATCHDATE	媒合日期
		//SEGPOST	郵局局號
		//SEGNO	郵局交易序號
		//SEGDT	郵局交易日期
		//SEGAMT	郵局交易金額
		insertParam.put("SEGAMT", "160");
		//TAKESELF	自領
		//SEGMNO	寄件掛號號碼
		//SEGMDT	寄件掛號日期
		//TEKNO	技術士證總編號
		String TEKNO = StringUtil.safeTrim(userData.get("PNO")).substring(0,3) + StringUtil.safeTrim(importDataMap.get("CRDITNO"));
		insertParam.put("TEKNO", TEKNO);
		//ERRDESC	媒合錯誤原因
		//STATE	媒合狀態
		//RALAMT	應繳總金額
		insertParam.put("RALAMT", "0");
		//MODUSERID	異動者帳號
		insertParam.put("MODUSERID", "SYSADMIN");
		//MODUSERNAME	異動者姓名
		insertParam.put("MODUSERNAME", "臨時證換發轉入");
		//MODIP	異動ip
		insertParam.put("MODIP", this.IP);
		//MODTIME	異動時間
		insertParam.put("MODTIME", DateUtil.getCurrentDateTime(DateUtil.TYPE_DATETIME, DateUtil.STYLE_ROC));
		//ERRCODE	錯誤代碼
		//WEBSEQ	網路申請流水號
		//POSTID	郵局代碼
		this.dbUtil.insert(conn, "SENDREC", insertParam);
	}


	/**
	 * 寫掛號郵寄檔
	 * @param conn
	 * @param importDataMap
	 * @param userData
	 * @throws Exception
	 */
	private void insertTechtranChk(
			Connection conn,
			LinkedHashMap<String, Object> importDataMap, LinkedHashMap<String, Object> userData, LinkedHashMap<String, Object> metaData) throws Exception {

		LinkedHashMap<String, Object> insertParam =
				new LinkedHashMap<String, Object>();

		//CASENO
		insertParam.put("CASENO", StringUtil.safeTrim(metaData.get("CASENO")));
		//INDATE (非免費取前三碼)
		insertParam.put("INDATE", StringUtil.safeTrim(metaData.get("INDATE")).substring(0,3));
		//PAYNUMBER
		insertParam.put("PAYNUMBER", StringUtil.safeTrim(metaData.get("SNO")));
		//PAYMONEY
		insertParam.put("PAYMONEY", 160);
		//MODUSERID	異動者帳號
		insertParam.put("MODUSERID", "SYSADMIN");
		//MODUSERNAME	異動者姓名
		insertParam.put("MODUSERNAME", "臨時證換發轉入");
		//MODIP	異動ip
		insertParam.put("MODIP", this.IP);
		//MODTIME	異動時間
		insertParam.put("MODTIME", DateUtil.getCurrentDateTime(DateUtil.TYPE_DATETIME, DateUtil.STYLE_ROC));
		//PNO
		insertParam.put("PNO", StringUtil.safeTrim(userData.get("PNO")));
		//EGR
		insertParam.put("EGR", StringUtil.safeTrim(userData.get("EGR")));
		//CRDITNO
		insertParam.put("CRDITNO", StringUtil.safeTrim(importDataMap.get("CRDITNO")));

		//System.out.println(new BeanUtil().showContent("TECHTRAN_CHK" , insertParam));
		this.dbUtil.insert(conn, "TECHTRAN_CHK", insertParam);
	}

	//	private void updateTECHM(
	//			Connection conn,
	//			LinkedHashMap<String, Object> importDataMap,
	//			LinkedHashMap<String, Object> userData,
	//			LinkedHashMap<String, Object> metaData) throws Exception {
	//
	//		//只是 update 畫面資料, 目前無須修改
	//	}

	/**
	 * 掛號流水號
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private String getSNO(Connection conn) throws Exception {

		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("SELECT Nvl(Max(SNO), '0') + 1 AS MAXSNO ");
		sqlStr.append("FROM   SENDREC ");

		String caseNum = StringUtil.safeTrim(this.dbUtil.query(conn, sqlStr.toString(), null).get(0).get("MAXSNO"));

		return caseNum;
	}

	/**
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private String getCaseNo(Connection conn) throws Exception {

		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("SELECT (Nvl(Max(CASENO), ?) + 1) AS MAXCASENO ");
		sqlStr.append("FROM   TECHTRAN ");
		sqlStr.append("WHERE  CASENO LIKE ? ");
		sqlStr.append("	   AND APPLYDATE > '0990101' ");

		//系統時間
		String currRocDate = DateUtil.getDateOfROC(DateUtil.getToday());

		List<Object> whereValueList = new ArrayList<Object>();
		whereValueList.add(currRocDate + "000");
		whereValueList.add(currRocDate + "%");

		String caseNum = StringUtil.safeTrim(this.dbUtil.query(conn, sqlStr.toString(), whereValueList).get(0).get("MAXCASENO"));

		return StringUtil.padding(caseNum, "0", 10, true);
	}

	/**
	 * @param conn
	 * @param pno
	 * @param egr
	 * @param crditNo
	 * @param idno
	 * @return
	 * @throws Exception
	 */
	private LinkedHashMap<String, Object> getUserData(Connection conn,	String crditNo, String idno) throws Exception {

		StringBuffer  sqlStr = new StringBuffer();
		sqlStr.append("SELECT TECHM.NAME, ");
		sqlStr.append("	   TECHM.ENNAME, ");
		sqlStr.append("	   TECHM.BIRDTE, ");
		sqlStr.append("	   TECHM.SEX, ");
		sqlStr.append("	   TECHM.EDU, ");
		sqlStr.append("	   TECHM.TEL, ");
		sqlStr.append("	   TECHM.PHONE, ");
		sqlStr.append("	   TECHM.FZIP, ");
		sqlStr.append("	   TECHM.FADR, ");
		sqlStr.append("	   TECHM.CZIP, ");
		sqlStr.append("	   TECHM.CADR, ");
		sqlStr.append("	   TECHD.AREAC, ");
		sqlStr.append("	   TECHD.ZONENO, ");
		sqlStr.append("	   TECHD.ISSUEDATE, ");
		sqlStr.append("	   TECHD.OPITEMS, ");
		sqlStr.append("	   TECHD.EFFYN, ");
		sqlStr.append("	   TECHD.MEMO, ");
		sqlStr.append("	   TECHM.PAUSE, ");
		sqlStr.append("	   TECHD.EXAMKIND, ");
		sqlStr.append("	   TECHD.PNO, ");
		sqlStr.append("	   TECHD.EGR, ");
		sqlStr.append("	   PROF.NAME AS PNO_NAME, ");
		sqlStr.append("	   TECHD.CRDITNO, ");
		sqlStr.append("	   TECHTRAN.OCRDITNO, ");
		sqlStr.append("	   TECHTRAN.LICENSENO ");

		sqlStr.append("FROM   #TABLE# ");
		sqlStr.append("	   LEFT JOIN TECHM ");
		sqlStr.append("			  ON TECHD.IDNO = TECHM.IDNO ");
		sqlStr.append("				 AND TECHD.NAME = TECHM.NAME ");
		sqlStr.append("	   LEFT JOIN PROF ");
		sqlStr.append("			  ON PROF.PNO = TECHD.PNO ");
		sqlStr.append("	   LEFT JOIN TECHTRAN ");
		sqlStr.append("			  ON TECHTRAN.IDNO = TECHM.IDNO ");
		sqlStr.append("WHERE  TECHD.idno = ? ");
		sqlStr.append("		AND  TECHD.CRDITNO = ? ");



		List<Object> whereValueList = new ArrayList<Object>();
		StringBuffer  allSqlStr = new StringBuffer();

		allSqlStr.append(sqlStr.toString().replaceAll("#TABLE#", "TECHD"));
		whereValueList.add(idno);
		whereValueList.add(crditNo);

		allSqlStr.append(" UNION ");

		allSqlStr.append(sqlStr.toString().replaceAll("#TABLE#", "TECHD_DU TECHD"));
		whereValueList.add(idno);
		whereValueList.add(crditNo);

		// for 另外撈資料, 測試用
				RbtDbUtilImpl rbtDbUtilImpl = new RbtDbUtilImpl(
						RbtDbUtilImpl.DRIVER_Oracle,
						"jdbc:oracle:thin:@10.100.2.1:1521:LABOR",
						"cla",
						"clalabor", 5);

				List<LinkedHashMap<String, Object>> resultList =
						rbtDbUtilImpl.query(allSqlStr.toString(), whereValueList);

//		List<LinkedHashMap<String, Object>> resultList =
//				this.dbUtil.query(allSqlStr.toString(), whereValueList);

		if (resultList.size() == 0) {
			throw new Exception("找不到證照資料! 身份證字號:[" + idno + "], 證照號碼:[" + crditNo + "]");
		}

		return resultList.get(0);
	}

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
			conn = this.dbUtil.getConnection();

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
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//System.out.println("10000美容".substring(5));
		new TechtramImporter();
	}
}
