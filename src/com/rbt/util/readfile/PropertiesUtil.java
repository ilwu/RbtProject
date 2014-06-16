/**
 *
 */
package com.rbt.util.readfile;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import com.rbt.util.StringUtil;

/**
 *
 * @author Allen Wu
 */
public class PropertiesUtil {

	/**
	 * 建構子
	 */
	public PropertiesUtil(){
	}

	/**
	 * 讀取 properties 檔案
	 * @param propertiesPath properties 路徑
	 * @return HashMap <String Key, String Value>
	 */
	public HashMap<String, String> read(String propertiesPath){

		//讀取 properties
		return this.readProperties(propertiesPath);
	}

	/**
	 * 讀取 與傳入class同package且同檔名的 properties 檔案
	 * @param cls class
	 * @return HashMap <String Key, String Value>
	 */
	public HashMap<String, String> read(Class<?> cls){

		//兜組參數檔案路徑
		String propertiesPath = cls.getName();
		propertiesPath = propertiesPath.replace(".", File.separator);
		//propertiesPath = propertiesPath.substring(propertiesPath.indexOf(File.separator)+1);

		//讀取 properties
		return this.readProperties(propertiesPath);
	}

	/**
	 * 讀取 properties
	 * @param propertiesPath properties 路徑
	 * @return HashMap <String Key, String Value>
	 */
	private HashMap<String, String> readProperties(String propertiesPath){

		HashMap<String, String> param = new HashMap<String, String>();

		System.out.println("=============================================================================");
		System.out.println("" +
				"取得參數檔 " +
				propertiesPath.substring(propertiesPath.lastIndexOf(File.separator) + 1)
				+ " 內容");
		System.out.println("=============================================================================");

		//開啟參數檔
		ResourceBundle resourceBundle = ResourceBundle.getBundle(propertiesPath);
		Enumeration<String> enumeration = resourceBundle.getKeys();

		//讀取參數並放入 MAP
		while(enumeration.hasMoreElements()){
			String key = enumeration.nextElement();
			param.put(key, resourceBundle.getString(key));
		}

		//印出所有參數
		Iterator<String> iterator = param.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			System.out.println(StringUtil.padding(key, " ", 20, false) + "= " + param.get(key));
		}

		System.out.println("\n讀取完成!\n" );
		return param;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
