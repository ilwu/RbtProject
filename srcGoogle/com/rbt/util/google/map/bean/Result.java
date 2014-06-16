package com.rbt.util.google.map.bean;

/**
 * @author Allen
 *
 */
public class Result {

	/**
	 *
	 */
	public Result() {
	}

	/**
	 * 這個字串包含這個位置的人類可讀地址。<br>
	 * 通常，這個地址即等於「郵政地址」，有時郵寄地址會因為國家/地區而不同。<br>
	 * (請註意，因為授權限制，部分國家/地區 (例如英國) 並不允許散佈真實的郵政地址)。<br>
	 * 這個地址一般是由一或多個「地址元件」所組成。舉例來說，
	 * 「111 8th Avenue, New York, NY」這個地址就包含「111」(街道號碼)、
	 * 「8th Avenue」(路線)、「New York」(城市名稱) 和「NY」(美國州名) 等個別地址元件
	 */
	private String formatted_address;

	/**
	 * 是一個陣列，包含上述的個別地址元件。每一個 address_component 通常包含：<br>
	 * types[]，一個指出地址元件的「類型」的陣列。<br>
	 * long_name，Geocoder 所傳回地址元件的完整文字說明或名稱。<br>
	 * short_name，為地址元件的縮短文字名稱 (如果有的話)。例如，Alaska 州的地址元件中 long_name 為「Alaska」，而 short_name 則為 2 個字母的郵政簡碼「AK」。<br>
	 * 請註意，address_components[] 包含的地址元件數量可能多於 formatted_address 中所標示的數量。
	 */
	private AddressComponent[] address_components;

	/**
	 * geometry 包含下列資訊：<br>
	 * location 包含地理編碼的經緯度值。進行一般的地址查閱時，這個欄位通常是最重要的。<br>
	 * location_type 會儲存指定位置的其他相關資料，目前支援的值如下：<br>
	 * "ROOFTOP" 會指出傳回的結果是精準的地理編碼，因為結果中位置資訊的精確範圍已縮小至街道地址。<br>
	 * "RANGE_INTERPOLATED" 表示傳回的結果反映的是插入在兩個精確定點之間 (例如十字路口) 的約略位置 (通常會在街道上)。如果 Geocoder 無法取得街道地址的精確定點地理編碼，就會傳回插入的結果。<br>
	 * "GEOMETRIC_CENTER" 表示傳回的結果是結果的幾何中心，包括折線 (例如街道) 和多邊形 (區域)。<br>
	 * "APPROXIMATE" 表示傳回的結果是約略位置。<br>
	 * viewport 包含建議用來顯示傳回結果的檢視區，檢視區的值兩個經緯度值，分別定義檢視區邊框的 southwest 角和 northeast 角。一般來說，檢視區是指您對使用者顯示結果時，用於結果的邊框。<br>
	 * bounds (選擇性傳回) 會儲存可完全包含傳回結果的邊框。請註意，這些邊界可能與建議的檢視區不同。(舉例來說，舊金山行政區涵蓋 法拉倫島 (英文網頁)，雖然這在技術上是舊金山的一部分，但是可能不會在檢視區中傳回)。<br>
	 * partial_match 表示 Geocoder 並沒傳回與原始要求完全相符的地址，不過有一些部分與要求的地址相符合。您最好對照原始要求，檢查是否有拼寫錯誤和/或不完整的地址。出現部分吻合的情況，通常是因為街道地址不存在於您在要求中傳送的地區裏面。<br>
	 */
	private Geometry geometry;

	/**
	 * 表示 Geocoder 並沒傳回與原始要求完全相符的地址，不過有一些部分與要求的地址相符合。您最好對照原始要求，檢查是否有拼寫錯誤和/或不完整的地址。出現部分吻合的情況，通常是因為街道地址不存在於您在要求中傳送的地區裏面。
	 */
	private boolean partial_match;

	/**
	 * 傳回結果內的 types[] 陣列會指出「地址類型」。<br>
	 * 這些類型也可能透過 address_components[] 陣列傳回，以指出特定地址元件的類型。Geocoder 中的地址能夠擁有多個類型；<br>
	 * 我們可以將這些類型視為「標記」。例如，許多城市都會加上 political 和 locality 類型標記。<br>
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

	// ==================================================================
	// gatter & setter
	// ==================================================================
	public String getFormatted_address() {
		return this.formatted_address;
	}

	/**
	 * 這個字串包含這個位置的人類可讀地址。<br>
	 * 通常，這個地址即等於「郵政地址」，有時郵寄地址會因為國家/地區而不同。<br>
	 * (請註意，因為授權限制，部分國家/地區 (例如英國) 並不允許散佈真實的郵政地址)。<br>
	 * 這個地址一般是由一或多個「地址元件」所組成。舉例來說，
	 * 「111 8th Avenue, New York, NY」這個地址就包含「111」(街道號碼)、
	 * 「8th Avenue」(路線)、「New York」(城市名稱) 和「NY」(美國州名) 等個別地址元件
	 */
	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}

	/**
	 * geometry 包含下列資訊：<br>
	 * location 包含地理編碼的經緯度值。進行一般的地址查閱時，這個欄位通常是最重要的。<br>
	 * location_type 會儲存指定位置的其他相關資料，目前支援的值如下：<br>
	 * "ROOFTOP" 會指出傳回的結果是精準的地理編碼，因為結果中位置資訊的精確範圍已縮小至街道地址。<br>
	 * "RANGE_INTERPOLATED" 表示傳回的結果反映的是插入在兩個精確定點之間 (例如十字路口) 的約略位置 (通常會在街道上)。如果 Geocoder 無法取得街道地址的精確定點地理編碼，就會傳回插入的結果。<br>
	 * "GEOMETRIC_CENTER" 表示傳回的結果是結果的幾何中心，包括折線 (例如街道) 和多邊形 (區域)。<br>
	 * "APPROXIMATE" 表示傳回的結果是約略位置。<br>
	 * viewport 包含建議用來顯示傳回結果的檢視區，檢視區的值兩個經緯度值，分別定義檢視區邊框的 southwest 角和 northeast 角。一般來說，檢視區是指您對使用者顯示結果時，用於結果的邊框。<br>
	 * bounds (選擇性傳回) 會儲存可完全包含傳回結果的邊框。請註意，這些邊界可能與建議的檢視區不同。(舉例來說，舊金山行政區涵蓋 法拉倫島 (英文網頁)，雖然這在技術上是舊金山的一部分，但是可能不會在檢視區中傳回)。<br>
	 * partial_match 表示 Geocoder 並沒傳回與原始要求完全相符的地址，不過有一些部分與要求的地址相符合。您最好對照原始要求，檢查是否有拼寫錯誤和/或不完整的地址。出現部分吻合的情況，通常是因為街道地址不存在於您在要求中傳送的地區裏面。<br>
	 * @return Geometry
	 */
	public Geometry getGeometry() {
		return this.geometry;
	}

	/**
	 * set Geometry
	 * @param geometry Geometry
	 */
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	/**
	 * 表示 Geocoder 並沒傳回與原始要求完全相符的地址，不過有一些部分與要求的地址相符合。您最好對照原始要求，檢查是否有拼寫錯誤和/或不完整的地址。出現部分吻合的情況，通常是因為街道地址不存在於您在要求中傳送的地區裏面。
	 * @return ture or false
	 */
	public boolean getPartial_match() {
		return this.partial_match;
	}

	/**
	 * 表示 Geocoder 並沒傳回與原始要求完全相符的地址，不過有一些部分與要求的地址相符合。您最好對照原始要求，檢查是否有拼寫錯誤和/或不完整的地址。出現部分吻合的情況，通常是因為街道地址不存在於您在要求中傳送的地區裏面。
	 * @return ture or false
	 */
	public boolean isPartial_match() {
		return this.partial_match;
	}

	public void setPartial_match(boolean partial_match) {
		this.partial_match = partial_match;
	}
	/**
	 * 傳回結果內的 types[] 陣列會指出「地址類型」。<br>
	 * 這些類型也可能透過 address_components[] 陣列傳回，以指出特定地址元件的類型。Geocoder 中的地址能夠擁有多個類型；<br>
	 * 我們可以將這些類型視為「標記」。例如，許多城市都會加上 political 和 locality 類型標記。<br>
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
	 * @return types
	 */
	public String[] getTypes() {
		return this.types;
	}

	/**
	 * set types
	 * @param types types
	 */
	public void setTypes(String[] types) {
		this.types = types;
	}

	/**
	 * 是一個陣列，包含上述的個別地址元件。每一個 address_component 通常包含：<br>
	 * types[]，一個指出地址元件的「類型」的陣列。<br>
	 * long_name，Geocoder 所傳回地址元件的完整文字說明或名稱。<br>
	 * short_name，為地址元件的縮短文字名稱 (如果有的話)。例如，Alaska 州的地址元件中 long_name 為「Alaska」，而 short_name 則為 2 個字母的郵政簡碼「AK」。<br>
	 * 請註意，address_components[] 包含的地址元件數量可能多於 formatted_address 中所標示的數量。
	 * @return AddressComponent[]
	 */
	public AddressComponent[] getAddress_components() {
		return this.address_components;
	}

	/**
	 * set AddressComponent[]
	 * @param address_components AddressComponent[]
	 */
	public void setAddress_components(AddressComponent[] address_components) {
		this.address_components = address_components;
	}

}
