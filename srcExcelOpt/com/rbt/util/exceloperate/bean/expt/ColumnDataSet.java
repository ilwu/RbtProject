package com.rbt.util.exceloperate.bean.expt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rbt.util.BeanUtil;

public class ColumnDataSet {

	// =====================================================
	// 資料儲存區
	// =====================================================
	private Map<String, Object> columnDataMap = new LinkedHashMap<String, Object>();
	private Map<String, List<ColumnDataSet>> arrayDataMap = new LinkedHashMap<String, List<ColumnDataSet>>();
	private Map<String, ColumnDataSet> singleDataMap = new LinkedHashMap<String, ColumnDataSet>();

	// =====================================================
	// columnDataMap
	// =====================================================
	/**
	 * @return the columnDataMap
	 */
	public Object getColumn(String key) {
		return this.columnDataMap.get(key);
	}
	/**
	 * @param columnDataMap the columnDataMap to set
	 */
	public void setColumn(String key, Object Object) {
		this.columnDataMap.put(key, Object);
	}
	// =====================================================
	// arrayDataMap
	// =====================================================
	/**
	 * @return the ArrayData List<ColumnDataSet>>
	 */
	public List<ColumnDataSet> getArray(String dataId) {
		return this.arrayDataMap.get(dataId);
	}
	/**
	 * @param arrayDataList the arrayDataList to set
	 */
	public void setArrayColumnDataSet(String dataId, List<ColumnDataSet> arrayDataList) {
		this.arrayDataMap.put(dataId, arrayDataList);
	}

	/**
	 * 無子欄位特殊設定時, 可直接將 List<Map<String,Object>> 型態資料放入, 會自動轉為 List<ColumnDataSet>
	 * @param dataId
	 * @param dataMapList
	 */
	public void setArray(String dataId, List<Map<String,Object>> dataMapList) {
		List<ColumnDataSet> detailDataSetList = new ArrayList<ColumnDataSet>();
		for (Map<String, Object> columnData : dataMapList) {
			ColumnDataSet columnDataSet = new ColumnDataSet();
			columnDataSet.setColumnDataMap(columnData);
			detailDataSetList.add(columnDataSet);
		}
		this.arrayDataMap.put(dataId, detailDataSetList);
	}

	// =====================================================
	// singleDataMap
	// =====================================================
	/**
	 * @return the singleDataMap
	 */
	public ColumnDataSet getSingle(String dataId) {
		return this.singleDataMap.get(dataId);
	}
	/**
	 * @param singleDataMap the singleDataMap to set
	 */
	public void setSingle(String dataId, ColumnDataSet singleData) {
		this.singleDataMap.put(dataId, singleData);
	}
	/**
	 * @param singleDataMap the singleDataMap to set
	 */
	public void setSingle(String dataId, Map<String, Object> singleData) {
		ColumnDataSet columnDataSet = new ColumnDataSet();
		columnDataSet.setColumnDataMap(singleData);
		this.singleDataMap.put(dataId, columnDataSet);
	}


	// =====================================================
	//
	// =====================================================
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return new BeanUtil().showContent(this);
	}

	// =====================================================
	// getter && setter
	// =====================================================
	/**
	 * @return the columnDataMap
	 */
	public Map<String, Object> getColumnDataMap() {
		return this.columnDataMap;
	}
	/**
	 * @param columnDataMap the columnDataMap to set
	 */
	public void setColumnDataMap(Map<String, Object> columnDataMap) {
		this.columnDataMap = columnDataMap;
	}
	/**
	 * @return the arrayDataMap
	 */
	public Map<String, List<ColumnDataSet>> getArrayDataMap() {
		return this.arrayDataMap;
	}
	/**
	 * @param arrayDataMap the arrayDataMap to set
	 */
	public void setArrayDataMap(Map<String, List<ColumnDataSet>> arrayDataMap) {
		this.arrayDataMap = arrayDataMap;
	}
	/**
	 * @return the singleDataMap
	 */
	public Map<String, ColumnDataSet> getSingleDataMap() {
		return this.singleDataMap;
	}
	/**
	 * @param singleDataMap the singleDataMap to set
	 */
	public void setSingleDataMap(Map<String, ColumnDataSet> singleDataMap) {
		this.singleDataMap = singleDataMap;
	}

}
