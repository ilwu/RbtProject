/**
 *
 */
package com.rbt.util.readfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Allen Wu
 *         解析 CSV 檔案
 */
public class ParsingCSVFile {

	/**
	 * 分隔符號
	 */
	private static String SEPARATE = ",";
	/**
	 * 輸入檔案編碼
	 */
	private String FILE_ENCODEING = "MS950";

	/**
	 * 解析 CSV 檔案
	 * @param filePath
	 * @return List行 <List欄<String>>
	 * @throws Exception
	 */
	public List<List<String>> parsing(String filePath) throws Exception {

		// 整理路徑字串
		filePath = new File(filePath).getPath();

		System.out.println("=============================================================================");
		System.out.println("解析檔案：" + filePath);
		System.out.println("=============================================================================");

		// 讀取檔案
		BufferedReader bufferedReader = null;
		try {

			try {
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), this.FILE_ENCODEING));
			} catch (UnsupportedEncodingException e) {
				System.out.println("檔案編碼設定錯誤，目前設定為：" + this.FILE_ENCODEING);
				throw e;
			} catch (FileNotFoundException e) {
				System.out.println("檔案不存在：" + filePath);
				throw e;
			}

			List<List<String>> dataList = new ArrayList<List<String>>();

			String lineStr = "";
			int lineCount = 0;
			while ((lineStr = bufferedReader.readLine()) != null) {
				lineCount++;

				if ("".equals(lineStr)) {
					System.out.println(lineCount + " 行為空白行");
					continue;
				}

				// 分割行
				String[] parsingDataArray = this.split(lineStr);

				// 寫入欄位
				List<String> lineDataList = new ArrayList<String>();
				for (int i = 0; i < parsingDataArray.length; i++) {
					parsingDataArray[i] = parsingDataArray[i].trim();
					lineDataList.add(parsingDataArray[i]);
				}
				dataList.add(lineDataList);
			}

			System.out.println("有" + dataList.size() + "行資料");
			return dataList;
		} finally {
			if(bufferedReader!=null){
				bufferedReader.close();
				bufferedReader=null;
			}
		}

	}

	/**
	 * split 依據 csv 格式的定義分割欄位
	 * @param str
	 * @return
	 */
	private String[] split(String str) {

		if (str == null || str.length() < 1)
			return new String[0];

		// 切成單字，(處理後頭尾index 會固定為空)
		String[] charAry = str.split("");

		List resultList = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		boolean isSPMode = false;
		for (int i = 1; i < charAry.length; i++) {

			// 『""』視為一個
			if ("\"".equals(charAry[i]) && i < charAry.length - 1 && "\"".equals(charAry[i + 1])) {
				sb.append(charAry[i]);
				i++;
				continue;
			}

			// 搜尋 為 『,"』的字串
			if (!isSPMode && i < charAry.length - 1 && SEPARATE.equals(charAry[i]) && "\"".equals(charAry[i + 1])) {
				// 跳過『,"』
				i++;
				// 遇到『遇分割字符不分割』模式開啟
				isSPMode = true;
				// 將 StringBuffer toString 後存入 List，並清空 StringBuffer
				resultList.add(sb.toString());
				sb = new StringBuffer();
				continue;
			}

			// 當『遇分割字符不分割』模式開啟時，檢核是否碰到 『",』的字串
			if (isSPMode && i < charAry.length - 1 && "\"".equals(charAry[i]) && SEPARATE.equals(charAry[i + 1])) {
				// 跳過『",』
				i++;
				// 遇到『遇分割字符不分割』模式關閉
				isSPMode = false;
				// 將 StringBuffer toString 後存入 List，並清空 StringBuffer
				resultList.add(sb.toString());
				sb = new StringBuffer();
				continue;
			}

			// 當『遇分割字符不分割』模式開啟，且為最後一字時，若為『"』則跳過
			if (isSPMode && i == charAry.length - 1 && "\"".equals(charAry[i])) {
				continue;
			}

			// 當非『遇分割字符不分割』模式時，遇到分割符號則換列
			if (!isSPMode && SEPARATE.equals(charAry[i])) {
				// 將 StringBuffer toString 後存入 List，並清空 StringBuffer
				resultList.add(sb.toString());
				sb = new StringBuffer();
				continue;
			}

			sb.append(charAry[i]);
		}
		// add 最後一組
		resultList.add(sb.toString());
		return (String[]) resultList.toArray(new String[0]);
	}

	/**
	 * 物件 SEPARATE 的 getter
	 * @return SEPARATE
	 */
	public static String getSEPARATE() {
		return SEPARATE;
	}

	/**
	 * 物件 SEPARATE 的 setter
	 * @param separate SEPARATE
	 */
	public static void setSEPARATE(String separate) {
		SEPARATE = separate;
	}

	/**
	 * 物件 FILE_ENCODEING 的 getter
	 * @return FILE_ENCODEING
	 */
	public String getFILE_ENCODEING() {
		return this.FILE_ENCODEING;
	}

	/**
	 * 物件 FILE_ENCODEING 的 setter
	 * @param file_encodeing FILE_ENCODEING
	 */
	public void setFILE_ENCODEING(String file_encodeing) {
		this.FILE_ENCODEING = file_encodeing;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "欄位名稱,型  態,大 小,起位,\"說明,\"\",\"\"\"\"  123\",12";

		String[] sa = new ParsingCSVFile().split(str);
		for (int i = 0; i < sa.length; i++) {
			System.out.println("[" + sa[i] + "]");
		}

	}
}