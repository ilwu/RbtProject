package com.rbt.util.google.map.bean;

/**
 * @author Allen
 *
 */
public class GeoModel {

	public GeoModel() {
	}
	// ==================================================================
	// 查詢用
	// ==================================================================
	/**
	 * 查詢條件：地址
	 */
	private String qryAddress;
	/**
	 * 回傳結果的 JSON 格式字串
	 */
	private String resultJSON;

	// ==================================================================
	// 自設方法
	// ==================================================================
	/**
	 * 取得回傳座標
	 * @return Location
	 */
	public Location getLocation(){
		Result result = this.getStreet_addressResult();
		if(result==null){
			return null;
		}
		return result.getGeometry().getLocation();
	}

	/**
	 * 取得回傳格式化地址
	 * @return 格式化地址
	 */
	public String getFormattedAddress(){
		Result result = this.getStreet_addressResult();
		if(result==null){
			return null;
		}return result.getFormatted_address();
	}

	/**
	 * 抓回傳結果第一個結果類型為 street_address, 若無，否則回第一個結果
	 * @return
	 */
	private Result getStreet_addressResult(){
		if(!"OK".equals(this.status) || this.results==null || this.results.length==0){
			return null;
		}
		for (Result result : this.results) {
			for (String type : result.getTypes()) {
				if("street_address".equals(type)){
					return result;
				}
			}
		}
		return this.results[0];
	}


	// ==================================================================
	// 參數
	// ==================================================================
	/**
	 * 包含一個地理編碼地址資訊和幾何圖形資訊的陣列。
	 */
	private Result[] results;

	/**
	 * 「地理編碼」回應物件中的 "status" 欄位包含要求的狀態，同時可能包含除錯資訊，以協助您在「地理編碼」服務失敗時追蹤原因。<br>
	 * "status" 欄位可能包含下列值：<br>
	 * "OK" 表示沒有發生任何錯誤；地址的剖析已成功完成且至少傳回一個地理編碼。<br>
	 * "ZERO_RESULTS" 表示地理編碼成功，但是並未傳回任何結果。如果 Geocoder 收到的是遠處的 latlng 或是不存在的 address，就有可能發生這種情況。<br>
	 * "OVER_QUERY_LIMIT" 表示您已超過配額。<br>
	 * "REQUEST_DENIED" 表示您的要求已遭拒絕，通常是因為缺少 sensor 參數。<br>
	 * "INVALID_REQUEST" 通常表示查詢 (address 或 latlng) 遺失。<br>
	 */
	private String status;

	// ==================================================================
	// gatter & setter
	// ==================================================================
	/**
	 * 「地理編碼」回應物件中的 "status" 欄位包含要求的狀態，同時可能包含除錯資訊，以協助您在「地理編碼」服務失敗時追蹤原因。<br>
	 * "status" 欄位可能包含下列值：<br>
	 * "OK" 表示沒有發生任何錯誤；地址的剖析已成功完成且至少傳回一個地理編碼。<br>
	 * "ZERO_RESULTS" 表示地理編碼成功，但是並未傳回任何結果。如果 Geocoder 收到的是遠處的 latlng 或是不存在的 address，就有可能發生這種情況。<br>
	 * "OVER_QUERY_LIMIT" 表示您已超過配額。<br>
	 * "REQUEST_DENIED" 表示您的要求已遭拒絕，通常是因為缺少 sensor 參數。<br>
	 * "INVALID_REQUEST" 通常表示查詢 (address 或 latlng) 遺失。<br>
	 * @return status
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * set status
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 包含一個地理編碼地址資訊和幾何圖形資訊的陣列。
	 * @return results
	 */
	public Result[] getResults() {
		return this.results;
	}

	/**
	 * set results
	 * @param results results
	 */
	public void setResults(Result[] results) {
		this.results = results;
	}

	/**
	 * @return the qryAddress
	 */
	public String getQryAddress() {
		return this.qryAddress;
	}

	/**
	 * @param qryAddress the qryAddress to set
	 */
	public void setQryAddress(String qryAddress) {
		this.qryAddress = qryAddress;
	}

	/**
	 * @return the resultJSON
	 */
	public String getResultJSON() {
		return this.resultJSON;
	}

	/**
	 * @param resultJSON the resultJSON to set
	 */
	public void setResultJSON(String resultJSON) {
		this.resultJSON = resultJSON;
	}

}
