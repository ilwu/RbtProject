package com.rbt.util.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import com.rbt.util.StringUtil;
import com.rbt.util.file.FileUtil;

/**
 * DB Utility
 * @author Allen Wu
 */
public class TableUtil {

	/**
	 * PROPERTIES_FILE_NAME
	 */
	private final String PROPERTIES_FILE_NAME = "TableUtil";

	public static final String DRIVER_DB2 = "COM.ibm.db2.jdbc.net.DB2Driver";
	public static final String DRIVER_MySQL = "com.mysql.jdbc.Driver";
	public static final String DRIVER_Oracle = "oracle.jdbc.driver.OracleDriver";
	public static final String DRIVER_SQLServer = "net.sourceforge.jtds.jdbc.Driver";

	private String driver; // JDBC Driver
	private String url;
	private String user;
	private String passwd;
	private int maxConnection; // 連接池中最大Connection數目
	private List<Connection> connections;

	private String databaseType;// 資料庫 TYPE

	/**
	 * 建構子 (讀取 PROPERTIES)
	 * @throws IOException 定義檔不存在時拋出
	 * @throws ClassNotFoundException JDBC 驅動程式不存在時拋出
	 */
	public TableUtil() throws IOException, ClassNotFoundException {

		// 取得參數檔案路徑
		String propertiesPath = this.getClass().getName();
		propertiesPath = propertiesPath.substring(0, propertiesPath.lastIndexOf('.') + 1);
		propertiesPath = propertiesPath.replace(".", File.separator) + this.PROPERTIES_FILE_NAME;

		// 開啟參數檔
		System.out.println("讀取 " + propertiesPath + ".properties\n");
		ResourceBundle resourceBundle = ResourceBundle.getBundle(propertiesPath);

		// 讀取參數
		this.driver = resourceBundle.getString("driver");
		this.url = resourceBundle.getString("url");
		this.user = resourceBundle.getString("user");
		this.passwd = resourceBundle.getString("password");
		this.maxConnection = Integer.parseInt(resourceBundle.getString("maxConnection"));

		// 載入 JDBC 驅動程式
		Class.forName(this.driver);

		this.connections = new ArrayList<Connection>();
	}

	/**
	 * 建構子
	 * @param driver JDBC 驅動程式 class name
	 * @param url 連接 URL
	 * @param user 使用者名稱
	 * @param passwd 使用者密碼
	 * @param maxConnection 連接池中最大Connection數目
	 * @throws ClassNotFoundException JDBC 驅動程式不存在時拋出
	 */
	public TableUtil(String driver, String url, String user, String passwd, int maxConnection) throws ClassNotFoundException {

		// 取得參數
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.passwd = passwd;
		this.maxConnection = maxConnection;

		// 載入 JDBC 驅動程式
		Class.forName(this.driver);

		this.connections = new ArrayList<Connection>();
	}

	/**
	 * @return Get Connection
	 * @throws SQLException
	 */
	public synchronized Connection getConnection() throws SQLException {
		if (this.connections.size() == 0) {
			return DriverManager.getConnection(this.url, this.user, this.passwd);
		}
		int lastIndex = this.connections.size() - 1;
		return this.connections.remove(lastIndex);
	}

	/**
	 * Close Connection
	 * @param conn Connection
	 * @throws SQLException
	 */
	public synchronized void closeConnection(Connection conn) throws SQLException {
		if (this.connections.size() == this.maxConnection) {
			conn.close();
		}
		else {
			this.connections.add(conn);
		}
	}

	/**
	 * 取得 DATABASE 中所有的 TABLE NAME
	 * @param database database name
	 * @return List <String TABLE NAME>
	 * @throws SQLException 執行錯誤時拋出 SQLException
	 */
	public List<String> getAllTableName(String database) throws SQLException {

		Connection conn = null;
		PreparedStatement ps;
		ResultSet result;
		List<String> resultList = new ArrayList<String>();

		// 取得資料庫類型
		this.parsingDatabaseType();

		// 依據 database type 選則 SQL
		String sql = "";

		// DB2
		if ("db2".equals(this.databaseType)) {
			sql = "SELECT NAME from SYSIBM.SYSTABLES where CREATOR=?";
		}
		// MySQL
		if ("mysql".equals(this.databaseType)) {
			sql = "SELECT TABLE_NAME FROM information_schema.TABLES Where TABLE_SCHEMA=?";
		}

		// Oracle
		if ("oracle".equals(this.databaseType)) {
			sql = "SELECT OBJECT_NAME FROM USER_OBJECTS where OBJECT_TYPE=? ORDER BY OBJECT_NAME";
			database = "TABLE";
		}

		// SQL Server
		if ("jtds".equals(this.databaseType) || "sqlserver".equals(this.databaseType)) {
			sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.Tables order by TABLE_NAME";
		}

		// 判斷未實作 sql
		if ("".equals(sql)) {
			new Exception("未實作此類型資料庫SQL! (" + this.databaseType + ")");
		}

		try {
			// 取得 Connection
			conn = this.getConnection();

			// prepareStatement
			ps = conn.prepareStatement(sql);
			if (sql.indexOf("?") != -1) {
				ps.setString(1, database);
			}

			// Query
			result = ps.executeQuery();

			while (result.next()) {
				resultList.add(result.getString(1));
			}

		} finally {
			if (conn != null)
				this.closeConnection(conn);
		}

		return resultList;
	}

	/**
	 * 解析 URL 取得資料庫類型
	 */
	private void parsingDatabaseType() {

		// 解析 database type
		String urlSpilt[] = this.url.split(":");
		if (urlSpilt.length < 4) {
			new Exception("url format error! connot find database type! (url=" + this.url + ")");
		}
		this.databaseType = urlSpilt[1];
		System.out.println("databaseType:" + this.databaseType);
	}

	/**
	 * 取得某一 database 中，所有的欄位註解
	 * @param database
	 * @param tableNames
	 * @return
	 * @throws Exception
	 */
	public String getAllTableColumnComments(String database, String tableNames) throws Exception {

		FileUtil fu = new FileUtil();

		// ===================================================
		// 依據不同資料兜組 SQL
		// ===================================================
		StringBuffer sqlStr = new StringBuffer();

		if (this.driver.equals(TableUtil.DRIVER_Oracle)) {
			sqlStr.append("SELECT * ");
			sqlStr.append("FROM   ALL_COL_COMMENTS ");
			sqlStr.append("WHERE  TABLE_NAME = ? ");
		} else {
			throw new Exception("目前只支援 Orcale");
		}

		// ===================================================
		// 取得 Table Name
		// ===================================================
		List<String> tableList = null;
		if (StringUtil.isEmpty(tableNames)) {
			// 未指定table時，取得所有table
			tableList = this.getAllTableName(database);
		} else {
			// 指定時，放入 list
			tableList = new ArrayList<String>();
			String[] tableAry = tableNames.split(",");
			for (String tableName : tableAry) {
				tableList.add(StringUtil.safeTrim(tableName).toUpperCase());
			}
		}

		// ===================================================
		// 查詢
		// ===================================================
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet result = null;

		try {
			// 取得 Connection
			conn = this.getConnection();

			HashMap<String, String> tableCommentsMap = this.getTableCommemts(conn);

			for (String tableName : tableList) {
				System.out.println("processing " + tableName + " ...");
				// prepareStatement
				ps = conn.prepareStatement(sqlStr.toString());
				ps.setString(1, tableName);
				// Query
				result = ps.executeQuery();

				// 兜組
				fu.addLine("/*==============================================================*/");
				fu.addLine("/* Table: " + tableName + " */");
				fu.addLine("/*==============================================================*/");

				fu.addStr("COMMENT ON TABLE ");
				fu.addStr(database + ".");
				fu.addStr(tableName);
				fu.addStr(" IS ");
				fu.addStr("'" + StringUtil.safeTrim(tableCommentsMap.get(tableName.toUpperCase())) + "';");
				fu.addLine();

				while (result.next()) {
					String TABLE_NAME = StringUtil.safeTrim(result.getString("TABLE_NAME"));
					String COLUMN_NAME = StringUtil.safeTrim(result.getString("COLUMN_NAME"));
					String COMMENTS = StringUtil.safeTrim(result.getString("COMMENTS"));

					fu.addStr("COMMENT ON COLUMN ");
					fu.addStr(database + ".");
					fu.addStr(TABLE_NAME + ".");
					fu.addStr(COLUMN_NAME);
					fu.addStr(" IS ");
					fu.addStr("'" + COMMENTS + "';");
					fu.addLine();
				}

				fu.addLine();
				result.close();
				ps.close();
			}


		} finally {
			if (conn != null)
				this.closeConnection(conn);
		}

		// ===================================================
		// 回傳
		// ===================================================
		return fu.getContent();
	}

	private HashMap<String, String> getTableCommemts(Connection conn) throws SQLException{

		StringBuffer  sqlStr = new StringBuffer();
		sqlStr.append("SELECT table_name, ");
		sqlStr.append("	   comments ");
		sqlStr.append("FROM   sys.user_tab_comments ");
		sqlStr.append("WHERE  table_type = 'TABLE' ");
		sqlStr.append("ORDER  BY TABLE_NAME ");

		PreparedStatement ps = conn.prepareStatement(sqlStr.toString());
		ResultSet result = ps.executeQuery();

		HashMap<String, String> commentsMap = new HashMap();
		while (result.next()) {
			commentsMap.put(StringUtil.safeTrim(result.getString("TABLE_NAME")).toUpperCase(), StringUtil.safeTrim(result.getString("COMMENTS")));
		}
		return commentsMap;

	}

	/**
	 * @param args
	 */
	public static void mainx(String[] args) {
		try {
			TableUtil tableUtil = new TableUtil(
					TableUtil.DRIVER_Oracle,
					"jdbc:oracle:thin:@192.168.1.201:1521:orcl",
					"cla",
					"clalabor",
					5
					);

			// TableUtil dbUtil = new TableUtil();
			List<String> list = tableUtil.getAllTableName("CLA");

			for (int i = 0; i < list.size(); i++) {
				System.out.println("drop table \"CLA\".\"" + list.get(i) + "\";");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		try {
			TableUtil tableUtil = new TableUtil(
					TableUtil.DRIVER_Oracle,
					"jdbc:oracle:thin:@10.100.2.1:1521:LABOR",
					"tes",
					"teslabor",
					5
					);

			System.out.println(tableUtil.getAllTableColumnComments("TES", null));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
