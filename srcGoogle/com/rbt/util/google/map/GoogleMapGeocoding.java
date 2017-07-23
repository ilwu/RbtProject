/**
 *
 */
package com.rbt.util.google.map;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rbt.util.google.map.bean.GeoModel;

import net.sf.json.JSONObject;

/**
 * 傳入地址，抓取 Google 地理資訊<br>
 * 需要的程式庫：<br>
 * commons-beanutils-1.8.3.jar<br>
 * commons-collections-3.2.1.jar<br>
 * commons-lang-2.6.jar<br>
 * commons-logging-1.1.1.jar<br>
 * ezmorph-1.0.6.jar<br>
 * json-lib-2.3-jdk15.jar<br>
 *
 * @author Allen Wu
 */
public class GoogleMapGeocoding {

	/**
	 * Logging utility
	 */
	protected Log LOG = LogFactory.getLog(getClass().getName());

	/**
	 * 查詢網址
	 */
	private static final String GEO_SERVICE_URL = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&language=zh-TW&region=tw&address=";

	/**
	 *
	 */
	public GoogleMapGeocoding() {
	}

	/**
	 * 執行
	 * @param qryGeoModels
	 * @throws IOException
	 */
	public void process(GeoModel[] qryGeoModels) throws IOException {

		HttpURLConnection httpURLConnection = null;
		BufferedReader bufferedReader = null;
		String address = "";
		try {
			for (GeoModel geoModel : qryGeoModels) {
				// 取得查詢地址
				address = geoModel.getQryAddress();
				if (address == null || "".equals(address.trim())) {
					continue;
				}

				StringBuffer addressSB = new StringBuffer();
				for (char chr : address.toCharArray()) {
					if (chr != ' ' && chr != ',' && chr != '<' && chr != '>' && chr != '?') {
						addressSB.append(chr);
					}
				}
				address = addressSB.toString().replaceAll("　", "");

				this.LOG.info("查詢地址:[" + address + "]");

				// 查詢
				//URL url = new URL(GEO_SERVICE_URL + address);
				URL url = new URL(GEO_SERVICE_URL + java.net.URLEncoder.encode(address,"UTF-8"));
				httpURLConnection = (HttpURLConnection) url.openConnection();
				bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
				String str = "";
				StringBuffer sb = new StringBuffer();
				while (null != ((str = bufferedReader.readLine()))) {
					sb.append(str + "\r\n");
				}

				// 解析回傳的 JSON 格式資料
				GeoModel model = (GeoModel) JSONObject.toBean(JSONObject.fromObject(sb.toString()), GeoModel.class);

				// copy
				geoModel.setResults(model.getResults());
				geoModel.setStatus(model.getStatus());
				geoModel.setResultJSON(sb.toString());
			}
		} catch (Exception e) {
			this.LOG.error("[" + address + "]處理失敗! (Pass)");
			this.LOG.error(this.getExceptionStackTrace(e));
			return;
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}

	/**
	 * 將ExceptionStackTrace轉為字串
	 * @param e Throwable
	 * @return
	 */
	private String getExceptionStackTrace(Throwable e) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
		e.printStackTrace(new PrintStream(byteArrayOutputStream));
		return new String(byteArrayOutputStream.toByteArray());
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {

		String qryAddress = "臺北市內湖區新湖二路289號";

		// for test
		GeoModel geoModel = new GeoModel();
		geoModel.setQryAddress(qryAddress);

		// 查詢
		new GoogleMapGeocoding().process(new GeoModel[] { geoModel });

		System.out.println(geoModel.getResultJSON());
		System.out.println("======================");
		System.out.println("lat:[" + geoModel.getLocation().getLat() + "]");
		System.out.println("Lng:[" + geoModel.getLocation().getLng() + "]");
		System.out.println("Formatted_address:[" + geoModel.getFormattedAddress() + "]");

	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static void main1(String[] args) throws UnsupportedEncodingException, IOException {

		String qryAddress = args[0];

		// for test
		GeoModel geoModel = new GeoModel();
		geoModel.setQryAddress(qryAddress);

		// 查詢
		new GoogleMapGeocoding().process(new GeoModel[] { geoModel });

		System.out.println(geoModel.getResultJSON());
		System.out.println("======================");
		System.out.println("lat:[" + geoModel.getLocation().getLat() + "]");
		System.out.println("Lng:[" + geoModel.getLocation().getLng() + "]");
		System.out.println("Formatted_address:[" + geoModel.getFormattedAddress() + "]");

	}
}
