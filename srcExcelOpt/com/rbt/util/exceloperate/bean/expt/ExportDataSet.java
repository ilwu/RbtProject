package com.rbt.util.exceloperate.bean.expt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rbt.util.BeanUtil;

/**
 * 匯出的資料
 * @author Allen
 */
public class ExportDataSet {

	// =====================================================
	// 資料儲存區
	// =====================================================
	/**
	 * context data
	 */
	private Map<String, Map<String, Object>> contextDataMap =
			new HashMap<String, Map<String, Object>>();

	/**
	 * detail Data
	 */
	private Map<String, List<ColumnDataSet>> detailDataMap =
			new HashMap<String, List<ColumnDataSet>>();

	// =====================================================
	// contextDataMap
	// =====================================================
	/**
	 * 放入 context data
	 * @param dataId dataId
	 * @param dataMap context data map
	 */
	public void setContext(String dataId, Map<String, Object> dataMap){
		this.contextDataMap.put(dataId, dataMap);
	}

	/**
	 * 取得 context data
	 * @param dataId dataId
	 * @return
	 */
	public Map<String, Object> getContext(String dataId){
		return this.contextDataMap.get(dataId);
	}

	// =====================================================
	// detailDataMap
	// =====================================================
	/**
	 * 放入 ColumnDataSet List
	 * @param dataId dataId
	 * @param detailDataSet ColumnDataSet
	 */
	public void setDetailDataSet(String dataId, List<ColumnDataSet> detailDataSetList) {
		this.detailDataMap.put(dataId, detailDataSetList);
	}

	/**
	 * 無子欄位特殊設定時, 可直接將 List<Map<String,Object>> 型態資料放入, 會自動轉為 List[ColumnDataSet]
	 * @param dataId
	 * @param dataMapList
	 */
	public void setDetail(String dataId, List<Map<String,Object>> dataMapList) {
		List<ColumnDataSet> detailDataSetList = new ArrayList<ColumnDataSet>();
		for (Map<String, Object> columnData : dataMapList) {
			ColumnDataSet columnDataSet = new ColumnDataSet();
			columnDataSet.setColumnDataMap(columnData);
			detailDataSetList.add(columnDataSet);
		}
		this.detailDataMap.put(dataId, detailDataSetList);
	}

	/**
	 * 無子欄位特殊設定時, 可直接將 List<Map<String,Object>> 型態資料放入, 會自動轉為 List[ColumnDataSet]
	 * @param dataId
	 * @param values
	 */
	public void setDetail(String dataId, Collection<LinkedHashMap<String, Object>> values) {
		List<ColumnDataSet> detailDataSetList = new ArrayList<ColumnDataSet>();
		for (LinkedHashMap<String, Object> columnData : values) {
			ColumnDataSet columnDataSet = new ColumnDataSet();
			columnDataSet.setColumnDataMap(columnData);
			detailDataSetList.add(columnDataSet);
		}
		this.detailDataMap.put(dataId, detailDataSetList);
	}

	/**
	 * 取得 ColumnDataSet List
	 * @param dataId dataId
	 * @return
	 */
	public List<ColumnDataSet> getDetail(String dataId) {
		return this.detailDataMap.get(dataId);
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

	/**
	 * @return the contextDataMap
	 */
	public Map<String, Map<String, Object>> getContextDataMap() {
		return this.contextDataMap;
	}

	/**
	 * @param contextDataMap the contextDataMap to set
	 */
	public void setContextDataMap(Map<String, Map<String, Object>> contextDataMap) {
		this.contextDataMap = contextDataMap;
	}

	/**
	 * @return the detailDataMap
	 */
	public Map<String, List<ColumnDataSet>> getDetailDataMap() {
		return this.detailDataMap;
	}

	/**
	 * @param detailDataMap the detailDataMap to set
	 */
	public void setDetailDataMap(Map<String, List<ColumnDataSet>> detailDataMap) {
		this.detailDataMap = detailDataMap;
	}
}
