package com.rbt.util.google.map.bean;

/**
 * @author Allen
 *
 */
public class Geometry {

	public Geometry() {
	}

	/**
	 * 包含地理編碼的經緯度值。進行一般的地址查閱時，這個欄位通常是最重要的
	 */
	private Location location;

	/**
	 * 會儲存指定位置的其他相關資料，目前支援的值如下：
	 *
	 * "ROOFTOP" 會指出傳回的結果是精準的地理編碼，因為結果中位置資訊的精確範圍已縮小至街道地址。
	 * "RANGE_INTERPOLATED" 表示傳回的結果反映的是插入在兩個精確定點之間 (例如十字路口) 的約略位置 (通常會在街道上)。如果 Geocoder 無法取得街道地址的精確定點地理編碼，就會傳回插入的結果。
	 * "GEOMETRIC_CENTER" 表示傳回的結果是結果的幾何中心，包括折線 (例如街道) 和多邊形 (區域)。
	 * "APPROXIMATE" 表示傳回的結果是約略位置。
	 */
	private String location_type;
	/**
	 * 包含建議用來顯示傳回結果的檢視區，檢視區的值兩個經緯度值，分別定義檢視區邊框的 southwest 角和 northeast 角。一般來說，檢視區是指您對使用者顯示結果時，用於結果的邊框。
	 */
	private ViewPort viewport;

	/**
	 * (選擇性傳回) 會儲存可完全包含傳回結果的邊框。請註意，這些邊界可能與建議的檢視區不同。(舉例來說，舊金山行政區涵蓋 法拉倫島 (英文網頁)，雖然這在技術上是舊金山的一部分，但是可能不會在檢視區中傳回)。
	 */
	private ViewPort bounds;

	// ==================================================================
	// gatter & setter
	// ==================================================================

	/**
	 * 包含地理編碼的經緯度值。進行一般的地址查閱時，這個欄位通常是最重要的
	 * @return Location
	 */
	public Location getLocation() {
		return this.location;
	}

	/**
	 * set location
	 * @param location Location
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
	/**
	 * 會儲存指定位置的其他相關資料，目前支援的值如下：
	 *
	 * "ROOFTOP" 會指出傳回的結果是精準的地理編碼，因為結果中位置資訊的精確範圍已縮小至街道地址。
	 * "RANGE_INTERPOLATED" 表示傳回的結果反映的是插入在兩個精確定點之間 (例如十字路口) 的約略位置 (通常會在街道上)。如果 Geocoder 無法取得街道地址的精確定點地理編碼，就會傳回插入的結果。
	 * "GEOMETRIC_CENTER" 表示傳回的結果是結果的幾何中心，包括折線 (例如街道) 和多邊形 (區域)。
	 * "APPROXIMATE" 表示傳回的結果是約略位置。
	 * @return location_type
	 */
	public String getLocation_type() {
		return this.location_type;
	}

	/**
	 * set location_type
	 * @param location_type location_type
	 */
	public void setLocation_type(String location_type) {
		this.location_type = location_type;
	}

	/**
	 * 包含建議用來顯示傳回結果的檢視區，檢視區的值兩個經緯度值，分別定義檢視區邊框的 southwest 角和 northeast 角。一般來說，檢視區是指您對使用者顯示結果時，用於結果的邊框。
	 * @return ViewPort
	 */
	public ViewPort getViewport() {
		return this.viewport;
	}

	public void setViewport(ViewPort viewport) {
		this.viewport = viewport;
	}

	/**
	 * (選擇性傳回) 會儲存可完全包含傳回結果的邊框。請註意，這些邊界可能與建議的檢視區不同。(舉例來說，舊金山行政區涵蓋 法拉倫島 (英文網頁)，雖然這在技術上是舊金山的一部分，但是可能不會在檢視區中傳回)。
	 * @return the bounds
	 */
	public ViewPort getBounds() {
		return this.bounds;
	}

	/**
	 * @param bounds the bounds to set
	 */
	public void setBounds(ViewPort bounds) {
		this.bounds = bounds;
	}




}
