package com.rbt.util;

import sun.misc.*;
import javax.crypto.*;

import java.security.*;
import javax.crypto.spec.*;

public class AES {

	/**
	 * @param k
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public static String getEncrypt(String k, String p) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128, new SecureRandom(k.getBytes()));
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypt = cipher.doFinal(p.getBytes());
		String result =  new BASE64Encoder().encodeBuffer(encrypt);

		//去折行
		result = result.replaceAll("\r", "");
		result = result.replaceAll("\n", "");

		return result;
	}

	/**
	 * @param k2
	 * @param base64
	 * @return
	 * @throws Exception
	 */
	public static String getDecrypt(String k2, String base64) throws Exception {
		byte[] b = new BASE64Decoder().decodeBuffer(base64);
		KeyGenerator kgen2 = KeyGenerator.getInstance("AES");
		kgen2.init(128, new SecureRandom(k2.getBytes()));
		SecretKey skey2 = kgen2.generateKey();
		byte[] raw2 = skey2.getEncoded();
		SecretKeySpec skeySpec2 = new SecretKeySpec(raw2, "AES");
		Cipher cipher2 = Cipher.getInstance("AES");
		cipher2.init(Cipher.DECRYPT_MODE, skeySpec2);
		byte[] decrypt = cipher2.doFinal(b);
		return new String(decrypt);
	}

	/**
	 * @param k
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public static String getHttpEncrypt(String k, String p) throws Exception {
		String en = getEncrypt(k, p);
		//return URLEncoder.encode(en, "UTF-8");
		return replaceToLink(en);
	}


	/**
	 * @param k
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public static String getHttpDecrypt(String k, String p) throws Exception {
		//String de = URLDecoder.decode(p, "UTF-8");
		String de = replaceToParam(p);
		System.out.println("de:[" + de + "]");
		return getDecrypt(k, de);
	}


//	1.	+	URL 中+號表示空格	%2B
//	2.	空格	URL中的空格可以用+號或者編碼	%20
//	3.	/	分隔目錄和子目錄	%2F
//	4.	?	分隔實際的 URL 和參數	%3F
//	5.	%	指定特殊字符	%25
//	6.	#	表示書籤	%23
//	7.	&	URL 中指定的參數間的分隔符	%26
//	8.	=	URL 中指定參數的值

	private static final String[] trnsCharAry = { "\\+"," ", "/", "\\?", "%", "#", "&", "=" };

	private static String replaceToLink(String str) {
		for (String trnsChar : trnsCharAry) {
			str = str.replaceAll(trnsChar + "", "@@" + (int) trnsChar.toCharArray()[0] + "@@");
		}
		return str;
	}

	private static String replaceToParam(String str) {
		for (String trnsChar : trnsCharAry) {
			str = str.replaceAll("@@" + (int) trnsChar.toCharArray()[0] + "@@", trnsChar + "");
		}
		return str;
	}

	public static void main(String[] args) throws Exception {
		String src = "{\"msgState\":\"11\",\"msgId\":\"\",\"msgNo\":\"MOF20130813011\"}";
		String en = AES.getEncrypt("12140827", src);
		System.out.println(en);
		String de = AES.getHttpDecrypt("12140827", "tnbmGnoUTUw0xk0rrZjIlBz@@47@@MnyWf9Co7xBH0nPgxfcCR@@47@@Ulmvm05XYEE2N3r2IikigF2doOGW3glfhFwfEN3A@@61@@@@61@@");
		System.out.println(de);

	}

	public static void main1(String[] args) throws Exception {
		String k = "1234";
		String plain = "測試";
		// 加密
		String en = AES.getHttpEncrypt(k, plain);
		String encryptd = new String(en);

		System.out.println("\nget密文:" + encryptd);

		// 解密
		String de = AES.getHttpDecrypt(k, encryptd);
		String decryptd = new String(de);
		System.out.println("\nget明文:" + decryptd);
	}

}
