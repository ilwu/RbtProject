package com.rbt.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Allen
 */
public class ZipUtil {

	/**
	 * 功能：將傳入對像進行gzip壓縮。
	 *
	 * @param unzip 需要壓縮對像
	 * @return byte[]
	 */
	public static byte[] zip(byte[] unzip) {
		byte[] data = null;
		ByteArrayInputStream bin = null;
		ByteArrayOutputStream bout = null;
		GZIPOutputStream gzout = null;
		try {
			bin = new ByteArrayInputStream(unzip);
			// 建立字節數組輸出流
			bout = new ByteArrayOutputStream();
			// 建立gzip壓縮輸出流
			gzout = new GZIPOutputStream(bout);
			// 建立對像序列化輸出流
			byte[] buf = new byte[1024];
			int num;
			while ((num = bin.read(buf)) != -1) {
				gzout.write(buf, 0, num);
			}
			gzout.close();
			bout.close();
			bin.close();
			// 返回壓縮字節流
			return bout.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (gzout != null)
					gzout.close();
				if (bout != null)
					bout.close();
				if (bin != null)
					bin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * 功能：將傳入對像進行gzip解壓。
	 * @param zip 需要解壓對像
	 * @return 解壓後的資料
	 */
	public static byte[] unzip(byte[] zip) {
		GZIPInputStream gzipInStream = null;
		ByteArrayInputStream bin = null;
		ByteArrayOutputStream bout = null;
		byte[] data = null;
		try {
			bin = new ByteArrayInputStream(zip);
			bout = new ByteArrayOutputStream();
			int sChunk = 10240;
			int length;

			gzipInStream = new GZIPInputStream(bin);

			// sChunk = allocate maxium value
			byte[] buffer = new byte[sChunk];

			// 讀取Zip檔案寫入byte array中
			while ((length = gzipInStream.read(buffer, 0, sChunk)) != -1) {
				bout.write(buffer, 0, length);
			}
			data = bout.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (gzipInStream != null)
					gzipInStream.close();
				if (bout != null)
					bout.close();
				if (bin != null)
					bin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 回傳解壓縮後的結果
		return data;

	}

	/**
	 * 將壓縮後的物件，還原為Object
	 * @param data 需要解壓目標
	 * @return Object
	 */
	public static Object readCompressObject(byte[] data) {
		Object object = null;
		try {
			// 建立字節數組輸入流
			ByteArrayInputStream i = new ByteArrayInputStream(data);
			// 建立gzip解壓輸入流
			GZIPInputStream gzin = new GZIPInputStream(i);
			// 建立對像序列化輸入流
			ObjectInputStream in = new ObjectInputStream(gzin);
			// 按制定類型還原對像
			object = in.readObject();
			i.close();
			gzin.close();
			in.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return object;
	}

	// 壓縮
	public static String compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return StringUtil.toHex(out.toByteArray());
	}

	// 解壓縮
	public static String uncompress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(hexStringToBytes(str));
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return out.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * @param hexString the hex string
	 * @return
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * @param c
	 * @return
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	// 測試方法
	public static void main(String[] args) throws IOException {
		System.out
				.println(uncompress("1F8B0800000000000000533732303430B03032313434363535B430000243751D7584B089B1B19131A6B0A91150028BB0A9B1913944180058A76DF45B000000"));

	}
}
