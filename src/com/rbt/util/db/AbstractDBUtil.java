package com.rbt.util.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.rbt.util.BeanUtil;
import com.rbt.util.StringUtil;

/**
 * DB 物件化操作工具, 抽象化類別
 * @author Allen
 */
public abstract class AbstractDBUtil {

	/**
	 * LOG4j
	 */
	public Logger LOG;

	/**
	 *
	 */
	public AbstractDBUtil() {
		this.LOG = this.getLogger();
	}

	/**
	 * 外部需放入 LOG
	 * @return
	 */
	public abstract Logger getLogger();

	// ==============================================================================================
	// get Connection
	// ==============================================================================================
	/**
	 * 需實做 getConnection 方式
	 * @return
	 * @throws Exception
	 */
	public abstract Connection getConnection() throws Exception;

	// ==============================================================================================
	// QUERY Count
	// ==============================================================================================
	/**
	 * 查詢筆數
	 * @param querySQL 查詢 SQL
	 * @param whereParamValueList 條件欄位 <欄位名稱,參數值>
	 * @return
	 * @throws Exception
	 */
	public int queryCount(String querySQL, List<Object> whereParamValueList) throws Exception {
		return this.queryCount(null, querySQL, whereParamValueList);
	}

	/**
	 * 查詢筆數
	 * @param Connection (傳入 null 時, 會自動 getConnection)
	 * @param querySQL 查詢 SQL
	 * @param whereParamValueList 條件欄位 <欄位名稱,參數值>
	 * @return
	 * @throws Exception
	 */
	public int queryCount(Connection conn, String querySQL, List<Object> whereParamValueList) throws Exception {
		querySQL = "select count(*) as CNT from (" + querySQL + ") a";
		String cnt = "0";
		List<LinkedHashMap<String, Object>> list = this.query(conn, querySQL, whereParamValueList);
		if (list != null && list.size() > 0) {
			cnt = StringUtil.safeTrim(list.get(0).get("CNT"), "0");
		}
		return Integer.parseInt(cnt);
	}

	/**
	 * 查詢單一 table 中資料的筆數
	 * @param tablenName tablen Name
	 * @param whereParamMap 條件欄位 <欄位名稱,參數值>
	 * @return
	 * @throws Exception
	 */
	public int queryCountByTable(String tablenName, LinkedHashMap<String, Object> whereParamMap) throws Exception {
		return this.queryCountByTable(null, tablenName, whereParamMap);
	}

	/**
	 * 查詢單一 table 中資料的筆數
	 * @param conn Connection (傳入 null 時, 會自動 getConnection)
	 * @param tablenName tablen Name
	 * @param whereParamMap 條件欄位 <欄位名稱,參數值>
	 * @return
	 * @throws Exception
	 */
	public int queryCountByTable(Connection conn, String tablenName, LinkedHashMap<String, Object> whereParamMap) throws Exception {

		// select 參數
		List<String> selectColumns = new ArrayList();
		selectColumns.add("count(*) as CNT");

		// 查詢
		String cnt = "0";
		List<LinkedHashMap<String, Object>> list = this.queryByTable(conn, tablenName, selectColumns, whereParamMap);
		if (list != null && list.size() > 0) {
			cnt = StringUtil.safeTrim(list.get(0).get("CNT"), "0");
		}

		return Integer.parseInt(cnt);
	}

	// ==============================================================================================
	// DB By Table
	// ==============================================================================================
	/**
	 * 查詢單一 Table
	 * @param tablenName tablen Name
	 * @param whereParamMap 條件欄位 <欄位名稱,參數值>
	 * @return
	 * @throws Exception
	 */
	public List<LinkedHashMap<String, Object>> queryByTable(
			String tablenName, LinkedHashMap<String, Object> whereParamMap) throws Exception {
		return this.queryByTable(null, tablenName, null, whereParamMap);
	}

	/**
	 * 查詢單一 Table
	 * @param conn
	 * @param tablenName tablen Name
	 * @param whereParamMap 條件欄位 <欄位名稱,參數值>
	 * @return
	 * @throws Exception
	 */
	public List<LinkedHashMap<String, Object>> queryByTable(
			Connection conn, String tablenName, LinkedHashMap<String, Object> whereParamMap) throws Exception {
		return this.queryByTable(conn, tablenName, null, whereParamMap);
	}

	/**
	 * 查詢單一 Table
	 * @param conn Connection (傳入 null 時, 會自動 getConnection)
	 * @param tablenName tablen Name
	 * @param selectColumns select 欄位List (為空時, select table 所有欄位)
	 * @param whereParamMap 條件欄位 <欄位名稱,參數值>
	 * @return
	 * @throws Exception
	 */
	public List<LinkedHashMap<String, Object>> queryByTable(
			String tablenName, List<String> selectColumns, LinkedHashMap<String, Object> whereParamMap) throws Exception {
		return this.queryByTable(null, tablenName, selectColumns, whereParamMap);
	}

	/**
	 * 查詢單一 Table
	 * @param conn Connection (傳入 null 時, 會自動 getConnection)
	 * @param tablenName tablen Name
	 * @param selectColumns select 欄位List (為空時, select table 所有欄位)
	 * @param whereParamMap 條件欄位 <欄位名稱,參數值>
	 * @return
	 * @throws Exception
	 */
	public List<LinkedHashMap<String, Object>> queryByTable(
			Connection conn, String tablenName, List<String> selectColumns, LinkedHashMap<String, Object> whereParamMap) throws Exception {

		// 產生查詢 SQL
		String queryStr = SqlUtil.genQuerySQL(tablenName, selectColumns, whereParamMap);

		// param Map to list
		List whereParamList = this.trnsMap2List(whereParamMap);

		// 查詢
		return this.query(conn, queryStr, whereParamList);
	}

	// ==============================================================================================
	// DB QUERY
	// ==============================================================================================
	/**
	 * Query by native SQL
	 * @param querySQL
	 * @return List<HashMap<欄位名稱,欄位值>>
	 * @throws Exception
	 */
	public List<LinkedHashMap<String, Object>> query(String querySQL) throws Exception {
		return this.query(null, querySQL, null);
	}

	/**
	 * Query by native SQL
	 * @param querySQL
	 * @param whereValueList
	 * @return List<HashMap<欄位名稱,欄位值>>
	 * @throws Exception
	 */
	public List<LinkedHashMap<String, Object>> query(
			String querySQL, List<Object> whereValueList) throws Exception {
		return this.query(null, querySQL, whereValueList);
	}

	/**
	 * Query by native SQL
	 * @param conn
	 * @param querySQL
	 * @param whereValueList
	 * @return List<HashMap<欄位名稱,欄位值>>
	 * @throws Exception
	 */
	public List<LinkedHashMap<String, Object>> query(
			Connection conn, String querySQL, List<Object> whereValueList) throws Exception {

		// PreparedStatement
		PreparedStatement ps = null;
		// ResultSet
		ResultSet rs = null;
		// 若 Connection 由前端帶進來，方法結束時不 close
		boolean isCloseConn = false;

		try {
			if (conn == null) {
				conn = this.getConnection();
				isCloseConn = true;
			}
			ps = conn.prepareStatement(querySQL);
			this.LOG.debug("SQL:[" + querySQL + "]");

			// 放入參數
			if (whereValueList != null && whereValueList.size() > 0) {
				int parameterIndex = 1;
				for (Object value : whereValueList) {
					this.LOG.debug(parameterIndex + ":[" + value + "]");
					ps.setObject(parameterIndex++, value);
				}
			}

			// 查詢
			rs = ps.executeQuery();
			// 資料轉 Map List
			return this.resultSet2MapList(rs);

		} catch (SQLException e) {
			this.LOG.error("DBQuery SQLException!");
			this.LOG.error("傳入SQL:\n" + querySQL);
			this.LOG.error("傳入參數:\n" + new BeanUtil().showContent(whereValueList));
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (ps != null) {
				ps.close();
				ps = null;
			}
			if (isCloseConn && conn != null) {
				conn.close();
				conn = null;
			}
		}
	}

	/**
	 * 查詢傳入的 SQL 中, 第一個欄位值
	 * @param conn
	 * @param querySQL
	 * @param whereValueList
	 * @return
	 * @throws Exception
	 */
	public String queryFirstValue(Connection conn, String querySQL, List<Object> whereValueList) throws Exception {
		// 查詢
		List<LinkedHashMap<String, Object>> list = this.query(conn, querySQL, whereValueList);

		if (StringUtil.isEmpty(list)) {
			return "";
		}

		LinkedHashMap<String, Object> dataMap = list.get(0);
		for (Object value : dataMap.values()) {
			return StringUtil.safeTrim(value);
		}
		return "";
	}

	/**
	 * ResultSet 轉 MapList (傳入之 resultSet 請自行關閉)
	 * @param rs ResultSet
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public List<LinkedHashMap<String, Object>> resultSet2MapList(ResultSet rs) throws SQLException, IOException {

		List<LinkedHashMap<String, Object>> resultList = new ArrayList();

		if (rs == null) {
			return resultList;
		}

		// Get result set meta data
		ResultSetMetaData rsmd = rs.getMetaData();
		int numColumns = rsmd.getColumnCount();

		while (rs.next()) {
			LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>();
			// Get the column names
			for (int i = 1; i < numColumns + 1; i++) {
				String columnName = rsmd.getColumnName(i);
				// -9:nvarchar
				// -1 text
				if (rsmd.getColumnType(i) == Types.VARCHAR || rsmd.getColumnType(i) == -9 || rsmd.getColumnType(i) == Types.LONGVARCHAR) {
					String str = rs.getString(columnName);
					// 為字串時去空白
					if (str == null) {
						resultMap.put(columnName, "");
					} else {
						resultMap.put(columnName, str.trim());
					}
				} else if (rsmd.getColumnType(i) == Types.CLOB) {
					Clob clob = rs.getClob(columnName);
					StringBuffer strOut = new StringBuffer();
					if (clob != null && clob.getCharacterStream() != null) {
						BufferedReader bufferedReader = new BufferedReader(clob.getCharacterStream());
						String aux = "";
						while ((aux = bufferedReader.readLine()) != null) {
							strOut.append(aux);
							strOut.append(System.getProperty("line.separator"));
						}
					}
					resultMap.put(columnName, strOut.toString().trim());
				} else {
					resultMap.put(columnName, rs.getObject(columnName));
				}
			}
			resultList.add(resultMap);
		}
		return resultList;
	}

	// ==============================================================================================
	// DB QUERY by key
	// ==============================================================================================
	/**
	 * QUERY by key
	 * @param queryStr SQL String
	 * @param key Key value
	 * @return LinkedHashMap<Select,value>
	 * @throws Exception
	 */
	public LinkedHashMap<String, Object> queryByKey(String queryStr, Object key) throws Exception {
		return this.queryByKey(null, queryStr, key);
	}

	/**
	 * QUERY by key
	 * @param conn Connection
	 * @param queryStr SQL String
	 * @param key Key value
	 * @return LinkedHashMap<Select,value>
	 * @throws Exception
	 */
	public LinkedHashMap<String, Object> queryByKey(Connection conn, String queryStr, Object key) throws Exception {
		// 查詢參數
		List param = new ArrayList();
		if (key != null && !"".equals(key.toString())) {
			param.add(key);
		}

		// 查詢
		List<LinkedHashMap<String, Object>> list = this.query(conn, queryStr, param);
		// 無資料時 return null
		if (list.size() == 0) {
			return null;
		}
		if (list.size() > 1) {
			this.LOG.error("SQL:[" + queryStr + "]");
			this.LOG.error("PARAM:[" + queryStr + "]");
			throw new Exception("開發階段錯誤，傳入的不是 key 值! (資料有多筆)");
		}
		return list.get(0);
	}

	// ==============================================================================================
	// DB INSER
	// ==============================================================================================
	/**
	 * 公用DB Insert
	 * @param tablenName table 名稱
	 * @param param 欄位HashMap
	 * @return
	 * @throws Exception
	 */
	public int insert(String tablenName, LinkedHashMap<String, Object> param) throws Exception {
		return this.insert(null, tablenName, param);
	}

	/**
	 * 公用DB Insert
	 * @param conn Connection
	 * @param tablenName table 名稱
	 * @param param 欄位HashMap
	 * @return
	 * @throws Exception
	 */
	public int insert(Connection conn, String tablenName, LinkedHashMap<String, Object> param) throws Exception {

		// PreparedStatement
		PreparedStatement ps = null;
		// 若 Connection 由前端帶進來，方法結束時不 close
		boolean isCloseConn = false;

		String sql = "";

		try {
			if (conn == null) {
				conn = this.getConnection();
				isCloseConn = true;
			}
			// 產生Insert SQL 並初始化 prepareStatement
			sql = SqlUtil.genInsertSQL(tablenName, param);
			ps = conn.prepareStatement(sql);
			this.getLogger().debug("\nINSERT SQL:[" + sql + "]");

			// 放入參數
			if (param != null) {
				int parameterIndex = 1;
				for (String columnName : param.keySet()) {
					this.LOG.debug(parameterIndex + " " + columnName + ":[" + param.get(columnName) + "]");
					if (param.get(columnName) == null) {
						ps.setObject(parameterIndex++, "");
					} else {
						ps.setObject(parameterIndex++, param.get(columnName));
					}
				}
			}

			// 執行
			int rowCount = ps.executeUpdate();
			this.LOG.info("INSERT [" + tablenName + "] :" + rowCount + "筆");
			return rowCount;
		} catch (SQLException e) {
			this.LOG.error("DBUtil SQLException!");
			this.LOG.error("SQL:\n" + sql);
			this.LOG.error("傳入參數:\n" + new BeanUtil().showContent(param));
			throw e;
		} finally {
			if (ps != null) {
				ps.close();
				ps = null;
			}
			if (isCloseConn && conn != null) {
				conn.close();
				conn = null;
			}
		}
	}

	/**
	 * 批次 Insert
	 * @param conn Connection
	 * @param tableName Table 名稱
	 * @param dataMapList 欄位資料集 (MAP) 的 List
	 * @param dNotDelete false:進行 delete-insert
	 * @throws Exception
	 */
	public void batchInsertDataMap(Connection conn, String tableName, List<LinkedHashMap<String, Object>> dataMapList, boolean dNotDelete) throws Exception {

		if (dataMapList == null || dataMapList.size() == 0) {
			this.LOG.info("無須異動資料");
			return;
		}

		// =======================================================================
		// 刪除 table 所有資料
		// =======================================================================
		if (!dNotDelete) {
			this.LOG.info("開始刪除 " + tableName + " 檔資料");
			this.excute(conn, "DELETE FROM " + tableName, null);
		}

		// =======================================================================
		// 開始 INSERT
		// =======================================================================
		this.LOG.debug("開始 insert (" + dataMapList.size() + " 筆)");

		StringBuffer sqlStr = new StringBuffer();
		List<Object> paramList = new ArrayList();
		int processTime = 1;
		int waitProcess = 0;
		// 2100為 PreparedStatement 參數上限, 故除以參數個數後，可得最多一次可以執行幾筆
		// (ex: 一筆參數 5 個, 2000/5 =400 , 所以一次超過400筆時參數會爆)
		int maxWaitProcess = (2000 / dataMapList.get(0).keySet().size());
		this.LOG.debug("每次執行最大筆數[" + maxWaitProcess + "]!");

		boolean isCloseConn = false;

		try {

			if (conn == null) {
				conn = this.getConnection();
				conn.setAutoCommit(false);
				isCloseConn = true;
			}

			for (Map<String, Object> dataMap : dataMapList) {

				// 等待執行筆數+1
				waitProcess++;

				// 產生 insert SQL並收集參數 (每次都重新組SQL的原因是怕 map 中參數的位置不一樣)
				sqlStr.append(this.ganInsertSQL(tableName, dataMap, paramList));

				if (waitProcess == maxWaitProcess) {
					this.LOG.debug("第[" + processTime + "]執行!");
					this.excute(conn, sqlStr.toString(), paramList);

					// 重置
					sqlStr = new StringBuffer();
					paramList.clear();
					processTime++;
					waitProcess = 0;
				}
			}
			// 剩餘的資料
			this.LOG.debug("第[" + processTime + "]執行!");
			this.excute(conn, sqlStr.toString(), paramList);

			// commit
			if (isCloseConn) {
				conn.commit();
			}
		} catch (Exception e) {
			// rollback
			if (isCloseConn) {
				conn.rollback();
			}
			this.LOG.error("執行的SQL:\n[" + sqlStr.toString() + "]");
			this.LOG.error("參數:\n" + new BeanUtil().showContent(paramList));
			throw e;
		} finally {
			if (isCloseConn && conn != null) {
				conn.close();
				conn = null;
			}
		}
	}

	/**
	 * @param tableName
	 * @param dataMap
	 * @param paramList
	 * @return
	 * @throws Exception
	 */
	private String ganInsertSQL(String tableName, Map<String, Object> dataMap, List<Object> paramList) throws Exception {
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("INSERT INTO " + tableName + " )VALUES(");
		if (dataMap == null || dataMap.keySet().size() == 0) {
			throw new Exception("傳入Map 為空");
		}

		Iterator<String> it = dataMap.keySet().iterator();
		String columnStr = "";
		String qmStr = "";

		while (it.hasNext()) {
			String key = it.next();
			qmStr += ",?";
			columnStr += "," + key;
			paramList.add(dataMap.get(key));
		}
		return "INSERT INTO " + tableName + "(" + columnStr.substring(1) + ")VALUES(" + qmStr.substring(1) + ");";
	}

	// ==============================================================================================
	// DB UPDATE
	// ==============================================================================================
	/**
	 * 公用DB Update
	 * @param tablenName table 名稱
	 * @param setParamMap update 欄位 LinkedHashMap
	 * @param whereParamMap where 欄位 LinkedHashMap
	 * @return
	 * @throws Exception
	 */
	public int updateByTable(String tablenName, LinkedHashMap<String, Object> setParamMap, LinkedHashMap<String, Object> whereParamMap) throws Exception {
		return this.updateByTable(null, tablenName, setParamMap, whereParamMap);
	}

	/**
	 * 公用DB Update
	 * @param conn Connection
	 * @param tablenName table 名稱
	 * @param setParamMap update 欄位 LinkedHashMap
	 * @param whereParamMap where 欄位 LinkedHashMap
	 * @return
	 * @throws Exception
	 */
	public int updateByTable(Connection conn, String tablenName, LinkedHashMap<String, Object> setParamMap, LinkedHashMap<String, Object> whereParamMap) throws Exception {

		// 兜組SQL
		String updateSql = SqlUtil.genUpdateSQL(tablenName, setParamMap, whereParamMap);
		// param 集合
		List<Object> param = new ArrayList();

		// 放入異動參數
		if (setParamMap != null) {
			for (String columnName : setParamMap.keySet()) {
				this.LOG.debug("set " + columnName + ":[" + setParamMap.get(columnName) + "]");
				param.add(setParamMap.get(columnName));
			}
		}
		// 放入條件參數
		if (whereParamMap != null) {
			for (String columnName : whereParamMap.keySet()) {
				this.LOG.debug("where " + columnName + ":[" + whereParamMap.get(columnName) + "]");
				param.add(whereParamMap.get(columnName));
			}
		}

		return this.update(conn, updateSql, param);
	}

	/**
	 * 使用一般SQL進行 UPDATE
	 * @param conn
	 * @param updateSql
	 * @param paramValueList
	 * @return
	 * @throws Exception
	 */
	public int update(Connection conn, String updateSql, List<Object> paramValueList) throws Exception {

		// PreparedStatement
		PreparedStatement ps = null;
		// 若 Connection 由前端帶進來，方法結束時不 close
		boolean isCloseConn = false;

		try {
			if (conn == null) {
				conn = this.getConnection();
				isCloseConn = true;
			}

			// 將參數值為 null 對應到的 ? 字串改為 null, 並移除該參數
			updateSql = this.procNullParam(updateSql, paramValueList);

			ps = conn.prepareStatement(updateSql);
			this.LOG.debug("\nUPDATE SQL:[" + updateSql + "]");

			// 放入異動參數
			for (int i = 0; paramValueList != null && i < paramValueList.size(); i++) {
				// this.LOG.debug((i+1) + ":[" + param.get(i) + "]");
				ps.setObject((i + 1), paramValueList.get(i));
			}

			// 執行
			int rowCount = ps.executeUpdate();
			this.LOG.info("UPDATE [" + updateSql + "] :" + rowCount + "筆");
			return rowCount;
		} catch (Exception e) {
			this.LOG.error("updateSql:[" + updateSql + "]");
			if (paramValueList != null) {
				for (Object object : paramValueList) {
					if (object == null) {
						this.LOG.error("null");
					} else {
						this.LOG.error("[" + StringUtil.safeTrim(object) + "]");
					}
				}
			}
			throw e;
		} finally {
			if (ps != null) {
				ps.close();
				ps = null;
			}
			if (isCloseConn && conn != null) {
				conn.close();
				conn = null;
			}
		}
	}

	/**
	 * 將參數值為 null 對應到的 ? 字串改為 null, 並移除該參數
	 * @param sql
	 * @param param
	 * @return
	 */
	private String procNullParam(String sql, List<Object> param) {
		// ==================================
		// 未傳入參數直接 return;
		// ==================================
		if (param == null || param.size() == 0) {
			return sql;
		}
		// ==================================
		// 無null參數值接 return
		// ==================================
		boolean procflag = false;
		for (Object object : param) {
			if (object == null) {
				procflag = true;
				break;
			}
		}

		if (!procflag) {
			return sql;
		}

		// ==================================
		// 依據 "?" 位置切割 SQL 字串
		// ==================================
		// 避免最後一個字元為?號時，會損失最後一組資料
		if (sql.endsWith("?")) {
			sql += " ";
		}
		String[] partSQL = sql.split("\\?");

		StringBuffer newSQL = new StringBuffer();
		newSQL.append(partSQL[0]);

		// ==================================
		// 將 null 參數對應到的 ? 字串改為 null, 並移除該參數
		// ==================================
		List<Object> newParam = new ArrayList();
		for (int i = 1; i < partSQL.length; i++) {
			// 避免param.get(i) 超出 index
			if (i < param.size() && param.get(i - 1) == null) {
				newSQL.append(" null ");
			} else {
				newSQL.append(" ? ");
				newParam.add(param.get(i - 1));
			}
			newSQL.append(partSQL[i]);
		}

		param.clear();
		for (Object object : newParam) {
			param.add(object);
		}
		return newSQL.toString();
	}

	// ==============================================================================================
	// DB DELETE
	// ==============================================================================================
	/**
	 * 公用DB Delete
	 * @param tablenName table 名稱
	 * @param whereParamMap update 欄位 LinkedHashMap
	 * @return
	 * @throws Exception
	 */
	public int deleteByTable(String tablenName, LinkedHashMap<String, Object> whereParamMap) throws Exception {
		return this.deleteByTable(null, tablenName, whereParamMap);
	}

	/**
	 * 公用DB Delete
	 * @param conn Connection
	 * @param tablenName table 名稱
	 * @param whereParamMap update 欄位 LinkedHashMap
	 * @return
	 * @throws Exception
	 */
	public int deleteByTable(Connection conn, String tablenName, LinkedHashMap<String, Object> whereParamMap) throws Exception {

		// PreparedStatement
		PreparedStatement ps = null;
		// 若 Connection 由前端帶進來，方法結束時不 close
		boolean isCloseConn = false;

		try {
			if (conn == null) {
				conn = this.getConnection();
				isCloseConn = true;
			}
			// 產生Delete SQL 並初始化 prepareStatement
			String sql = SqlUtil.genDeleteSQL(tablenName, whereParamMap);
			ps = conn.prepareStatement(sql);
			this.LOG.debug("\nDELETE SQL:[" + sql + "]");

			// 放入參數
			if (whereParamMap != null) {
				int parameterIndex = 1;
				for (String columnName : whereParamMap.keySet()) {
					this.LOG.debug(parameterIndex + " " + columnName + ":[" + whereParamMap.get(columnName) + "]");
					ps.setObject(parameterIndex++, whereParamMap.get(columnName));
				}
			}

			// 執行
			int rowCount = ps.executeUpdate();
			this.LOG.info("DELETE [" + tablenName + "] :" + rowCount + "筆");
			return rowCount;
		} finally {
			if (ps != null) {
				ps.close();
				ps = null;
			}
			if (isCloseConn && conn != null) {
				conn.close();
				conn = null;
			}
		}
	}

	// ==============================================================================================
	// DB EXECUTE
	// ==============================================================================================
	/**
	 * 執行 executeUpdate , 同 update()
	 * @param conn
	 * @param executeSql
	 * @param paramValueList
	 * @return
	 * @throws Exception
	 */
	public int excute(Connection conn, String executeSql, List<Object> paramValueList) throws Exception {
		return this.update(conn, executeSql, paramValueList);
	}

	// ==============================================================================================
	// 是否存在
	// ==============================================================================================
	/**
	 * 依據傳入的TABLE 名稱和參數條件，檢核是否筆數大於0
	 * @param conn
	 * @param tablenName table 名稱
	 * @param whereParamMap 條件欄位參數
	 * @return
	 * @throws Exception
	 */
	public boolean isExist(Connection conn, String tablenName, LinkedHashMap<String, Object> whereParamMap) throws Exception {
		// 查詢
		List<LinkedHashMap<String, Object>> list = this.queryByTable(tablenName, whereParamMap);

		return (list != null && list.size() > 0);
	}

	/**
	 * 依據傳入的 sql 查詢筆數是否大於0
	 * @param conn
	 * @param querySQL 查詢SQL
	 * @param whereValueList 參數 list
	 * @return
	 * @throws Exception
	 */
	public boolean isExist(Connection conn, String querySQL, List<Object> whereValueList) throws Exception {
		// 查詢
		List<LinkedHashMap<String, Object>> list = this.query(conn, querySQL, whereValueList);

		return (list != null && list.size() > 0);
	}

	// ==============================================================================================
	// 其他
	// ==============================================================================================
	/**
	 * 將 LinkedHashMap 值轉為 List
	 * @param map LinkedHashMap
	 * @return
	 */
	private List<Object> trnsMap2List(LinkedHashMap<String, Object> map) {
		// param Map to list
		List<Object> list = new ArrayList();

		if (map != null) {
			for (Entry<String, Object> entry : map.entrySet()) {
				list.add(entry.getValue());
			}
		}
		return list;
	}
}
