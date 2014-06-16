package com.rbt.util.google.map.bean;

/**
 * AddressComponent
 * @author Allen
 */
public class AddressComponent {

	/**
	 *
	 */
	public AddressComponent() {
	}

	// ==================================================================
	// 參數
	// ==================================================================
	/**
	 * 一個指出地址元件的「類型」的陣列
	 * street_address 會指出精確的街道地址。<br>
	 * route 會指出具名的路線 (例如「US 101」)。<br>
	 * intersection 會指出主要的十字路口，通常包含兩條主要道路。<br>
	 * political 會指出政治實體。通常，這個類型會顯示某些公家機關建築物的多邊形。<br>
	 * country 會指出國家/地區政治實體，且通常是 Geocoder 所能傳回的最高順位類型。<br>
	 * administrative_area_level_1 會指出國家/地區等級底下的第一順位公家實體。在美國，這類機關等級是州；並非所有國家/地區都會顯示這類機關等級。<br>
	 * administrative_area_level_2 會指出國家/地區等級底下的第二順位公家實體。在美國，這類機關等級是郡。並非所有國家/地區都會顯示這類機關等級。<br>
	 * administrative_area_level_3 會指出國家/地區等級底下的第三順位公家實體。這個類型代表的是小型公家部門。並非所有國家/地區都會顯示這類機關等級。<br>
	 * colloquial_area 會指出實體的常用替代名稱。<br>
	 * locality 會指出自治城市或鄉鎮的政治實體。<br>
	 * sublocality 會指出城市等級底下的第一順位公家實體。<br>
	 * neighborhood 會指出具名的社區。<br>
	 * premise 會指出具名的位置，通常是著名的建築物或建築物群<br>
	 * subpremise 會指出具名位置底下的第一順位實體，通常是著名建築物群中的單一建築物。<br>
	 * postal_code 會指出國家/地區郵政地址所使用的郵遞區號。<br>
	 * natural_feature 會指出著名的自然景點。<br>
	 * airport 會指出機場。<br>
	 * park 會指出具名的公園。<br>
	 * point_of_interest 會指出具名的名勝地點。通常這些「名勝地點 (POI)」是指不適合歸到其他分類的當地重要景點，如「帝國大廈」或「自由女神像」。<br>
	 */
	private String[] types;

	/**
	 * Geocoder 所傳回地址元件的完整文字說明或名稱
	 */
	private String long_name;

	/**
	 * 為地址元件的縮短文字名稱 (如果有的話)。例如，Alaska 州的地址元件中 long_name 為「Alaska」，而 short_name 則為 2 個字母的郵政簡碼「AK」。
	 */
	private String short_name;

	// ==================================================================
	// gatter & setter
	// ==================================================================
	/**
	 * Geocoder 所傳回地址元件的完整文字說明或名稱
	 * @return long_name
	 */
	public String getLong_name() {
		return this.long_name;
	}

	/**
	 * @param long_name long_name
	 */
	public void setLong_name(String long_name) {
		this.long_name = long_name;
	}

	/**
	 * 為地址元件的縮短文字名稱 (如果有的話)。例如，Alaska 州的地址元件中 long_name 為「Alaska」，而 short_name 則為 2 個字母的郵政簡碼「AK」。
	 * @return short_name
	 */
	public String getShort_name() {
		return this.short_name;
	}

	/**
	 * @param short_name short_name
	 */
	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

	/**
	 * 一個指出地址元件的「類型」的陣列
	 * street_address 會指出精確的街道地址。<br>
	 * route 會指出具名的路線 (例如「US 101」)。<br>
	 * intersection 會指出主要的十字路口，通常包含兩條主要道路。<br>
	 * political 會指出政治實體。通常，這個類型會顯示某些公家機關建築物的多邊形。<br>
	 * country 會指出國家/地區政治實體，且通常是 Geocoder 所能傳回的最高順位類型。<br>
	 * administrative_area_level_1 會指出國家/地區等級底下的第一順位公家實體。在美國，這類機關等級是州；並非所有國家/地區都會顯示這類機關等級。<br>
	 * administrative_area_level_2 會指出國家/地區等級底下的第二順位公家實體。在美國，這類機關等級是郡。並非所有國家/地區都會顯示這類機關等級。<br>
	 * administrative_area_level_3 會指出國家/地區等級底下的第三順位公家實體。這個類型代表的是小型公家部門。並非所有國家/地區都會顯示這類機關等級。<br>
	 * colloquial_area 會指出實體的常用替代名稱。<br>
	 * locality 會指出自治城市或鄉鎮的政治實體。<br>
	 * sublocality 會指出城市等級底下的第一順位公家實體。<br>
	 * neighborhood 會指出具名的社區。<br>
	 * premise 會指出具名的位置，通常是著名的建築物或建築物群<br>
	 * subpremise 會指出具名位置底下的第一順位實體，通常是著名建築物群中的單一建築物。<br>
	 * postal_code 會指出國家/地區郵政地址所使用的郵遞區號。<br>
	 * natural_feature 會指出著名的自然景點。<br>
	 * airport 會指出機場。<br>
	 * park 會指出具名的公園。<br>
	 * point_of_interest 會指出具名的名勝地點。通常這些「名勝地點 (POI)」是指不適合歸到其他分類的當地重要景點，如「帝國大廈」或「自由女神像」。<br>
	 * @return type array
	 */
	public String[] getTypes() {
		return this.types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

}
