/**
 *
 */
package com.rbt.util.readfile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import com.rbt.util.StringUtil;
import com.rbt.util.file.PathUtil;

import allen.story.Replace16k;

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
		System.out.println(propertiesPath);
		System.out.println("=============================================================================");

		//開啟參數檔
		//ResourceBundle resourceBundle = ResourceBundle.getBundle(propertiesPath);
		ResourceBundle resourceBundle = ResourceBundle.getBundle(propertiesPath, Locale.TAIWAN);
		Enumeration<String> enumeration = resourceBundle.getKeys();

		//讀取參數並放入 MAP
		while(enumeration.hasMoreElements()){
			String key = enumeration.nextElement();
			//解決空白會造成 key 錯亂 (properties 中, key 裡面的半形空白以全行代替)
			String tureKey = key.replaceAll("　", " ");

			//param.put(tureKey, resourceBundle.getString(key));
			try {
				String tureKeyC = new String(tureKey.getBytes("ISO-8859-1"), "UTF-8");
				String cont = new String(resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
				
				if("zi".equals(tureKey)){
					System.out.println("==========1:" + cont);
				}
				
				//處理key有空白部分判斷錯誤 ex:nao = dai=腦袋
				if(cont.indexOf('=')>0){
					String[] ary = cont.split("=");
					tureKeyC += " " + ary[0];
					cont = ary[1];
				}
				
				if("zi".equals(tureKeyC)){
					System.out.println("==========2");
				}

				param.put(tureKeyC, cont);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
	 * 取得和 jar 檔案相同目錄的 properties 檔案
	 * @param resourceFileName
	 * @return
	 * @throws MalformedURLException
	 */
	public ResourceBundle loadRootConfig(String resourceFileName) throws MalformedURLException{
		Locale currentLocale = Locale.getDefault();
		File resourceFile = new File(new PathUtil().getCurrentDirectory());
		URL resourceUrl = resourceFile.toURI().toURL();
		URL[] urls = { resourceUrl };
		ClassLoader loader = new URLClassLoader(urls);
		return ResourceBundle.getBundle(resourceFileName, currentLocale, loader);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PropertiesUtil().read(Replace16k.class);
	}
}
