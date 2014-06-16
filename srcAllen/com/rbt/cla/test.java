package com.rbt.cla;

import java.sql.Connection;
import java.sql.SQLException;

import com.rbt.util.BeanUtil;
import com.rbt.util.db.RbtDbUtilImpl;

public class test {

	public test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		RbtDbUtilImpl dbUtilImpl = new RbtDbUtilImpl(
				RbtDbUtilImpl.DRIVER_Oracle,
				"jdbc:oracle:thin:@118.163.126.175:1521:evtadb1",
				"cla",
				"clalabor", 5);

		Connection conn = dbUtilImpl.getConnection();
		System.out.println(new BeanUtil().showContent(dbUtilImpl.query(conn, "select * from dual"  , null)));
	}

}
