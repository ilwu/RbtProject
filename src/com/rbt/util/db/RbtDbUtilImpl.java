package com.rbt.util.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


/**
 * DB Utility
 * @author Allen Wu
 */
public class RbtDbUtilImpl extends AbstractDBUtil{

	public static final String DRIVER_DB2		= "COM.ibm.db2.jdbc.net.DB2Driver";
	public static final String DRIVER_MySQL		= "com.mysql.jdbc.Driver";
	public static final String DRIVER_Oracle	= "oracle.jdbc.driver.OracleDriver";
	public static final String DRIVER_SQLServer	= "net.sourceforge.jtds.jdbc.Driver";

	private String driver;		//JDBC Driver
	private String url;
	private String user;
	private String passwd;
	private int maxConnection; 	// 連接池中最大Connection數目
	private List<Connection> connections;

	private String databaseType;//資料庫 TYPE



	/**
	 * 建構子 (讀取 PROPERTIES)
	 * @throws IOException				定義檔不存在時拋出
	 * @throws ClassNotFoundException	JDBC 驅動程式不存在時拋出
	 */
	public RbtDbUtilImpl() throws IOException, ClassNotFoundException {


		//=================================================================================
		//讀與 class 興同路徑、名稱的設定檔 (已廢棄)
		//=================================================================================
		//取得參數檔案路徑
		//String propertiesPath = this.getClass().getName();
		//propertiesPath = propertiesPath.substring(0,propertiesPath.lastIndexOf('.')+1);
		//propertiesPath = propertiesPath.replace(".", File.separator) + this.PROPERTIES_FILE_NAME;

		//開啟參數檔
		//System.out.println("讀取 " + propertiesPath + ".properties\n" );
		//ResourceBundle resourceBundle = ResourceBundle.getBundle(propertiesPath);

		//=================================================================================
		//讀 class 根目錄下的 DBUtil.properties
		//=================================================================================
		ResourceBundle resourceBundle =
				ResourceBundle.getBundle("DBUtil", Locale.getDefault());


		//讀取參數
		this.driver	= resourceBundle.getString("driver");
		this.url	= resourceBundle.getString("url");
		this.user	= resourceBundle.getString("user");
		this.passwd = resourceBundle.getString("password");
		this.maxConnection = Integer.parseInt(resourceBundle.getString("maxConnection"));

		//載入 JDBC 驅動程式
		Class.forName(this.driver);

		this.connections = new ArrayList<Connection>();
	}

	/**
	 * 建構子
	 * @param driver		JDBC 驅動程式 class name
	 * @param url			連接 URL
	 * @param user			使用者名稱
	 * @param passwd		使用者密碼
	 * @param maxConnection	連接池中最大Connection數目
	 * @throws ClassNotFoundException	JDBC 驅動程式不存在時拋出
	 */
	public RbtDbUtilImpl(String driver, String url, String user, String passwd, int maxConnection) throws ClassNotFoundException {

		//取得參數
		this.driver	= driver;
		this.url	= url;
		this.user	= user;
		this.passwd = passwd;
		this.maxConnection = maxConnection;

		//載入 JDBC 驅動程式
		Class.forName(this.driver);

		this.connections = new ArrayList<Connection>();
	}

	/* (non-Javadoc)
	 * @see com.rbt.util.db.AbstractDBUtil#getConnection()
	 */
	public synchronized Connection getConnection() throws SQLException {
		if(this.connections.size() == 0) {
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
		if(this.connections.size() == this.maxConnection) {
			conn.close();
		}
		else {
			this.connections.add(conn);
		}
	}



	/**
	 * 取得 DATABASE 中所有的 TABLE NAME
	 * @param database	database name
	 * @return	List <String TABLE NAME>
	 * @throws SQLException 執行錯誤時拋出 SQLException
	 */
	public List<String> getAllTableName(String database) throws SQLException{

		Connection			conn =null;
		PreparedStatement	ps;
		ResultSet			result;
		List<String>		resultList = new ArrayList<String>();

		//取得資料庫類型
		this.parsingDatabaseType();

		//依據 database type 選則 SQL
		String sql = "";

		//DB2
		if("db2".equals(this.databaseType)){
			sql = "SELECT NAME from SYSIBM.SYSTABLES where CREATOR=?";
		}
		//MySQL
		if("mysql".equals(this.databaseType)){
			sql = "SELECT TABLE_NAME FROM information_schema.TABLES Where TABLE_SCHEMA=?";
		}

		//Oracle
		if("oracle".equals(this.databaseType)){
			sql = "SELECT OBJECT_NAME FROM USER_OBJECTS where OBJECT_TYPE=?";
			database = "TABLE";
		}

		//SQL Server
		if("jtds".equals(this.databaseType) || "sqlserver".equals(this.databaseType)){
			sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.Tables order by TABLE_NAME";
		}

		//判斷未實作 sql
		if("".equals(sql)){
			new Exception("未實作此類型資料庫SQL! (" + this.databaseType + ")");
		}

		try{
			//取得 Connection
			conn = this.getConnection();

			//prepareStatement
			ps = conn.prepareStatement(sql);
			if(sql.indexOf("?")!=-1){
				ps.setString(1, database);
			}

			//Query
			result = ps.executeQuery();

			while(result.next()){
				resultList.add(result.getString(1));
			}

		}finally{
			if (conn != null) this.closeConnection(conn);
		}

		return resultList;
	}

	/**
	 * 解析 URL 取得資料庫類型
	 */
	private void parsingDatabaseType(){

		//解析 database type
		String urlSpilt[] = this.url.split(":");
		if(urlSpilt.length<4){
			new Exception("url format error! connot find database type! (url=" + this.url + ")");
		}
		this.databaseType = urlSpilt[1];
		System.out.println("databaseType:" + this.databaseType);
	}



	/* (non-Javadoc)
	 * @see com.rbt.util.db.AbstractDBUtil#getLogger()
	 */
	public Logger getLogger() {
		return Logger.getLogger(this.getClass());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
//			RbtDbUtilImpl dbUtil = new RbtDbUtilImpl(
//					RbtDbUtilImpl.DRIVER_SQLServer,
//					"jdbc:jtds:sqlserver://192.168.0.118:1433/MRAC",
//					"sa",
//					"fubon",
//					5
//					);

			RbtDbUtilImpl rbtDbUtilImpl = new RbtDbUtilImpl();
			List<String>  list = rbtDbUtilImpl.getAllTableName("oracle");


			for(int i=0;i<list.size();i++){
				System.out.println(list.get(i));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
