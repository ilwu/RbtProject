package com.rbt.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class TextLineFilter {

	/**
	 * 建構子
	 * @param filePath 檔案路徑
	 * @param keyWord 關鍵字
	 * @param isPositive true 留下關鍵字存在的行, false 留下關鍵字以外的行
	 * @param FILE_ENCODEING 檔案編碼
	 * @throws Exception
	 */
	public TextLineFilter(String filePath, String keyWord, boolean isPositive, String FILE_ENCODEING) throws Exception {

		// 整理路徑字串
		filePath = new File(filePath).getPath();

		System.out.println("=============================================================================");
		System.out.println("解析檔案：" + filePath);
		System.out.println("=============================================================================");

		// 讀取檔案
		BufferedReader bufferedReader = null;
		try {

			try {
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), FILE_ENCODEING));
			} catch (UnsupportedEncodingException e) {
				System.out.println("檔案編碼設定錯誤，目前設定為：" + FILE_ENCODEING);
				throw e;
			} catch (FileNotFoundException e) {
				System.out.println("檔案不存在：" + filePath);
				throw e;
			}

			String lineStr = "";
			FileOutputStream fileWriter = new FileOutputStream(filePath + ".result");
			String keyWords[] = keyWord.split(";");
			for (int i = 0; i < keyWords.length; i++) {
				System.out.println(keyWords[i]);
			}
			while ((lineStr = bufferedReader.readLine()) != null) {
				for (int i = 0; i < keyWords.length; i++) {
					if (lineStr.indexOf(keyWords[i]) != -1 && isPositive == true) {
						lineStr += "\r\n";
						fileWriter.write(lineStr.getBytes(FILE_ENCODEING));
					}
					if (lineStr.indexOf(keyWords[i]) == -1 && isPositive == false) {
						lineStr += "\r\n";
						fileWriter.write(lineStr.getBytes(FILE_ENCODEING));
					}
				}
			}
			fileWriter.close();
			System.out.println("=============================================================================");
			System.out.println("處理完成，產生檔案：" + filePath + ".result");
			System.out.println("=============================================================================");
		} finally {
			if(bufferedReader!=null){
				bufferedReader.close();
				bufferedReader = null;
			}
		}
	}

	public static void main(String[] args) {
		try {
			new TextLineFilter("h:/0619/tomcat7-stdout.2013-06-19.log",
					"[userID]", true,
					"BIG5");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
