/**
 *
 */
package com.rbt.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Allen Wu
 *
 */
public class FileUtil {

	/**
	 * StringBuffer
	 */
	private StringBuffer stringBuffer;
	/**
	 * 斷行符號 (windows 是 "\r\n" Unix 是 "\n")
	 */
	private String BROKEN_LINE_SYMBOL = "\n";
	/**
	 * 寫出檔案是否為 是否寫入UTF8 BOM 使用
	 */
	private boolean useUTF8BOM = false;
	/**
	 * 輸出檔案編碼
	 */
	private String encoding = "utf8";
	/**
	 * 行縮排前綴字串
	 */
	private String indentPrefix = "";

	/**
	 * 建構子
	 */
	public FileUtil() {
		this.stringBuffer = new StringBuffer();
	}

	// =========================================================================================
	// 檔案寫入
	// =========================================================================================
	/**
	 * 加入字串
	 * @param str 欲加入的字串
	 */
	public void addStr(String str) {
		this.stringBuffer.append(str);
	}

	/**
	 * 加入字串
	 * @param obj 欲加入的字串
	 */
	public void addStr(Object obj) {
		this.stringBuffer.append(this.indentPrefix + obj.toString());
	}

	/**
	 * 加入字串 (自動在字串後面加入斷行符號)
	 * @param str 欲加入的字串
	 */
	public void addLine(String str) {
		this.stringBuffer.append(this.indentPrefix + String.valueOf(str + this.BROKEN_LINE_SYMBOL));
	}

	/**
	 * 加入字串 (自動在字串後面加入斷行符號)
	 * @param obj 欲加入的字串
	 */
	public void addLine(Object obj) {
		this.stringBuffer.append(this.indentPrefix + obj.toString() + this.BROKEN_LINE_SYMBOL);
	}

	/**
	 * 加入斷行符號
	 */
	public void addLine() {
		this.stringBuffer.append(this.BROKEN_LINE_SYMBOL);
	}

	/**
	 * 取得目前文字內容
	 * @return String
	 */
	public String getContent() {
		return this.stringBuffer.toString();
	}

	/**
	 * 刪除目前所有內容，取代成輸入的字串
	 * @param str
	 */
	public void setContent(String str) {
		this.stringBuffer.delete(0, this.stringBuffer.length());
		str = this.addIndent(str);
		this.stringBuffer.append(str);
	}

	/**
	 * 對字串增加縮排
	 * @param str
	 * @return
	 */
	private String addIndent(String str) {
		return this.addIndent(str, 1);
	}

	/**
	 * 對字串增加縮排 (預設給斷行符號)
	 * @param str 傳入字串
	 * @param prefixTabAmount 縮排 tab 數量
	 * @return 縮排後的字串
	 */
	public String addIndent(String str, int prefixTabAmount) {
		return this.addIndent(str, prefixTabAmount, true);
	}

	/**
	 * 對字串增加縮排
	 * @param str 傳入字串
	 * @param prefixTabAmount 在字串前加入的 TAB 數量
	 * @param addLineBreak 是否在最後加入斷行
	 * @return 縮排後的字串
	 */
	public String addIndent(String str, int prefixTabAmount, boolean addLineBreak) {

		String prefixTab = "";

		// 縮排字元
		for (int i = 0; i < prefixTabAmount; i++) {
			prefixTab += "	";
		}

		if (str == null || "".equals(str))
			return prefixTab;

		StringReader stringReader = new StringReader(str);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		List list = new ArrayList();
		// 逐行讀取

		String lineStr = "";
		while (true) {

			try {
				lineStr = bufferedReader.readLine();
			} catch (IOException e) {
				lineStr = null;
				e.printStackTrace();
			}
			if (lineStr == null)
				break;
			list.add(lineStr);
		}

		String strAry[] = (String[]) list.toArray(new String[0]);
		String result = "";

		String LineBreakSymbol = "";
		if (addLineBreak) {
			if (str.indexOf("\r\n") > 0) {
				LineBreakSymbol = "\r\n";
			} else {
				LineBreakSymbol = "\n";
			}
		}

		for (int i = 0; i < strAry.length; i++) {
			result += prefixTab + strAry[i] + LineBreakSymbol;
		}

		return result;
	}

	/**
	 * 在 addLine 時縮排
	 * @param prefixTabAmount 在字串前加入的 TAB 數量
	 */
	public void setAddLineIndent(int prefixTabAmount) {
		for (int i = 0; i < prefixTabAmount; i++) {
			this.indentPrefix += "	";
		}
	}

	/**
	 * 將內容寫入檔案
	 * @param fileFullPath 完整路徑 + 檔案名稱
	 * @throws Exception
	 */
	public void writeToFile(String fileFullPath) throws Exception {

		FileOutputStream fos = null;

		try {
			// 檢路徑
			new PathUtil().chkAndCreateDir(new File(fileFullPath).getParent());

			// 建立檔案
			fos = new FileOutputStream(fileFullPath);
			// 印出訊息
			System.out.println("寫入 " + fileFullPath);

			if (this.useUTF8BOM) {
				// 寫入 UTF8 HEADER
				fos.write(new byte[] { -17, -69, -65 });
				// 寫入檔案
				fos.write(this.stringBuffer.toString().getBytes("UTF-8"));
			} else {
				// 寫入檔案
				fos.write(this.stringBuffer.toString().getBytes(this.encoding));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			// close
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 將內容寫入檔案 (包含建立路徑功能)
	 * @param path 檔案路徑
	 * @param fileName 檔案名稱
	 * @throws Exception
	 */
	public void writeToFile(String path, String fileName) throws Exception {

		FileOutputStream fos = null;

		try {
			// 檢查並建立路徑
			new PathUtil().chkAndCreateDir(path);
			// 建立檔案
			fos = new FileOutputStream(path + File.separator + fileName);
			// 印出訊息
			System.out.println("寫入 " + new File(path + File.separator + fileName).getPath());

			if (this.useUTF8BOM) {
				// 寫入 UTF8 HEADER
				fos.write(new byte[] { -17, -69, -65 });
				// 寫入檔案
				fos.write(this.stringBuffer.toString().getBytes("UTF-8"));
			} else {
				// 寫入檔案
				fos.write(this.stringBuffer.toString().getBytes(this.encoding));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			// close
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 將傳入之文字，寫入檔案 (編碼請使用 setEncoding 設定)
	 * @param content
	 * @param path
	 * @param fileName
	 * @throws Exception
	 */
	public void writeToFile(String content, String path, String fileName) throws Exception {
		this.writeToFile(content.getBytes(this.encoding), path, fileName);
	}

	/**
	 * 將傳入之byte[]寫入檔案 (包含建立路徑功能)
	 * @param data data (byte[])
	 * @param path 檔案路徑
	 * @param fileName 檔案名稱
	 * @throws Exception
	 */
	public void writeToFile(byte[] data, String path, String fileName) throws Exception {

		FileOutputStream fos = null;

		try {
			// 檢查並建立路徑
			new PathUtil().chkAndCreateDir(path);
			// 建立檔案
			fos = new FileOutputStream(path + File.separator + fileName);
			// 印出訊息
			System.out.println("寫入 " + new File(path + File.separator + fileName).getPath());
			// 直接寫入Byte資料
			fos.write(data);

		} catch (Exception e) {
			throw e;
		} finally {
			// close
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 將傳入之inputStream寫入檔案 (包含建立路徑功能)
	 * @param inputStream inputStream
	 * @param filePath 檔案路徑
	 * @param fileName 檔案名稱
	 * @throws Exception
	 */
	public void writeToFile(InputStream inputStream, String filePath, String fileName) throws Exception {

		FileOutputStream fos = null;

		try {
			// 檢查並建立路徑
			new PathUtil().chkAndCreateDir(filePath);
			// 建立檔案
			fos = new FileOutputStream(filePath + File.separator + fileName);
			// 印出訊息
			System.out.println("寫入 " + new File(filePath + File.separator + fileName).getPath());
			// 直接寫入Byte資料
			byte[] buffer = new byte[10248];
			int len = -1;
			while ((len = inputStream.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
		} finally {
			// close
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 取得輸出檔案編碼
	 * @return encoding
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * 設定輸出檔案編碼 (預設 UTF-8)
	 * @param encoding encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * 設定輸出檔案的斷行符號，預設為『\n』(windows 為 \r\n)
	 * @param bROKENLINESYMBOL BROKEN_LINE_SYMBOL
	 */
	public void setBROKEN_LINE_SYMBOL(String bROKENLINESYMBOL) {
		this.BROKEN_LINE_SYMBOL = bROKENLINESYMBOL;
	}

	/**
	 * 設定輸出檔案是否為有BOM的UTF8檔案
	 * @param useUTF8BOM useUTF8BOM
	 */
	public void setUseUTF8BOM(boolean useUTF8BOM) {
		if (useUTF8BOM){
			System.out.println("目前輸出檔案設定編碼為:" + this.encoding + ",設定此項目為true後，編碼設定將不生效");
		}
		this.useUTF8BOM = useUTF8BOM;
	}

	// =========================================================================================
	// 其他工具
	// =========================================================================================
	/**
	 * 擷取完整路徑中的檔案名稱
	 * @param filePath 檔案完整路徑
	 * @return 檔案名稱
	 */
	public String getFileName(String filePath) {
		File file = new File(filePath);
		filePath = file.getPath();
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 讀取檔案
	 * @param filePath
	 * @return 檔案內容
	 * @throws IOException
	 */
	public String readFileContent(String filePath) throws IOException {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(filePath);
			byte[] szData = new byte[fileInputStream.available()];
			fileInputStream.read(szData);
			return new String(szData);
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
				fileInputStream = null;
			}
		}
	}

	/**
	 * 讀取檔案
	 * @param filePath 完整檔案路徑
	 * @param fileEncodeing 檔案編碼
	 * @return 依行放入String[]
	 * @throws IOException
	 */
	public String[] readFileContent(String filePath, String fileEncodeing) throws IOException {

		List resultList = new ArrayList();

		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		FileInputStream fileInputStream = null;

		try {
			// 讀取檔案
			fileInputStream = new FileInputStream(filePath);
			inputStreamReader = new InputStreamReader(fileInputStream, fileEncodeing);
			bufferedReader = new BufferedReader(inputStreamReader);

			// 逐行讀取
			String lineStr = "";
			while ((lineStr = bufferedReader.readLine()) != null) {
				resultList.add(lineStr);
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("檔案編碼設定錯誤，目前設定為：" + fileEncodeing);
			throw e;
		} catch (FileNotFoundException e) {
			System.out.println("檔案不存在：" + filePath);
			throw e;
		} finally {
			if (bufferedReader != null)
				bufferedReader.close();
			if (inputStreamReader != null)
				inputStreamReader.close();
			if (fileInputStream != null)
				fileInputStream.close();
		}

		return (String[]) resultList.toArray(new String[0]);
	}

	/**
	 * 讀取檔案並回傳內容
	 * @param filePath
	 * @param fileEncodeing
	 * @return
	 * @throws IOException
	 */
	public String readFileText(String filePath, String fileEncodeing) throws IOException {
		String[] lines = this.readFileContent(filePath, fileEncodeing);
		StringBuffer sb = new StringBuffer();

		for (String str : lines) {
			sb.append(str);
			sb.append(this.BROKEN_LINE_SYMBOL);
		}
		return sb.toString();
	}

	/**
	 * 讀取跟目錄下的檔案
	 * @param filename 檔案名稱
	 * @param fileEncodeing 檔案編碼
	 * @return 檔案 array
	 * @throws IOException
	 */
	public String[] readClassRootFileContent(String filename, String fileEncodeing) throws IOException {
		String filePath = this.getClassRootPath() + filename;
		return this.readFileContent(filePath, fileEncodeing);
	}

	/**
	 * 取得 class 根目錄
	 * @return class 根目錄字串
	 */
	public String getClassRootPath() {
		return this.getClass().getClassLoader().getResource(".").getPath();
	}

	/**
	 * 檢查是否為合法檔名
	 * @param fileName 完整檔案名稱
	 * @return ture or false
	 */
	public boolean isAllowFileName(String fileName) {
		fileName = "C:/" + fileName;
		try {
			this.writeToFile(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("檔名不合法");
			return false;
		}
		new File(fileName).deleteOnExit();
		return true;
	}

	// =========================================================================================
	// Append
	// =========================================================================================
	/**
	 * @param filePath
	 * @param fileName
	 * @param content
	 * @param encodeing
	 */
	public void fileAppend(String filePath, String fileName, String content, String encodeing) {

		BufferedWriter bufferedWriter = null;

		try {
			// 檢查並建立路徑
			new PathUtil().chkAndCreateDir(filePath);
			// 建立BufferedWriter
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + fileName, true), encodeing));

			// 寫入資料
			bufferedWriter.write(content);
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bufferedWriter = null;
		}
	}

	// =========================================================================================
	// 測試
	// =========================================================================================
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		FileUtil fu = new FileUtil();
		String str = "TEST1\n	TEST2\n		TEST3\n		TEST4\n	TEST5\nTEST6\n";
		fu.setAddLineIndent(1);
		fu.setContent(str);
		System.out.println(fu.getContent());
	}
}
