package test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;






public class test {

	public test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		StringBuffer queryStr = new StringBuffer();
		queryStr.append("SELECT Count(*) AS cnt ");
		queryStr.append("FROM   mailassign ");
		queryStr.append("       LEFT JOIN newmail ");
		queryStr.append("              ON mailassign.msgid = newmail.msgid ");
		queryStr.append("WHERE  NOT ( mailassign.isapproved = 1 ) ");
		queryStr.append("       AND mailassign.isrejected = 0 ");
		queryStr.append("       AND mailassign.isdeleted = 0 ");
		queryStr.append("       AND newmail.isdeleted = 0 ");
		queryStr.append("       AND newmail.msgattr3 = '-1' ");
		queryStr.append("       AND newmail.msgattr4 = '-1' ");
		queryStr.append("       AND mailassign.msgstate = 0 ");
		System.out.println(queryStr.toString());

	}

}
