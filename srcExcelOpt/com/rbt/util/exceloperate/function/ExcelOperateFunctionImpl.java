package com.rbt.util.exceloperate.function;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rbt.util.StringUtil;
import com.rbt.util.exceloperate.exception.ExcelOperateException;

/**
 * @author Allen
 */
public class ExcelOperateFunctionImpl extends AbstractExcelOperateFunction{

	/* (non-Javadoc)
	 * @see com.rbt.util.exceloperate.function.AbstractExcelOperateFunction#process(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Map, java.sql.Connection)
	 */
	public String process(
			String method,
			String keyName,
			String funcParam,
			String value,
			Map<String, Object> rowDataMap,
			Connection conn) throws ExcelOperateException {

		//依據 method 參數, 進行處理
		if("SEQ".equalsIgnoreCase(method)){
			//產生序號
			return this.genSeq(funcParam);

		}else if("CURR_ROC_DATE".equalsIgnoreCase(method)){
			//取得目前的民國年日期
			return this.getCurrRocDate();
		}
		throw new ExcelOperateException("Excel 處理錯誤, [" + this.getClass().getName() + "] 未設定 method :[" + method + "]");
	}

	/**
	 * 取得目前的民國年日期
	 * @return yyy年 MM月 dd日
	 */
	private String getCurrRocDate(){
		Date date = new Date();
		int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
		String md = new SimpleDateFormat("年 MM月 dd日").format(date);
		return (year-1911) + md;
	}

	/**
	 * genSeq
	 */
	public HashMap<String, Integer> seqMap;
	/**
	 * @param funcParam
	 * @return
	 */
	private String genSeq(String sqlSetID){
		//從1開始
		int seq = 1;
		//未傳入 sqlSetID 時，用預設值
		if(StringUtil.isEmpty(sqlSetID)){
			sqlSetID = "#DEFUALT_FUNC_PARAM#";
		}
		if(this.seqMap==null){
			this.seqMap = new HashMap<String, Integer>();
		}
		if(this.seqMap.containsKey(sqlSetID)){
			seq = this.seqMap.get(sqlSetID) + 1;
		}
		//記錄到存放的 Set
		this.seqMap.put(sqlSetID, seq);
		return seq+"";
	}
}
