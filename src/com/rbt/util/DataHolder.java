package com.rbt.util;
import java.util.HashMap;

/**
 * ThreadLocal資料儲存物件<br>
 * 因Application Server上面有thread pooling機制，導致不同的程式有可能重複使用到相同的
 * thread，造成程式中宣告為ThreadLocal的變數資料錯亂，故改為以此物件替代ThreadLocal，
 * 並搭配Filter機制於每次request結束時，清空每一個thread的ThreadLocal變數，避免資料發
 * 生錯亂。
 */
public class DataHolder {
	/**
	 *
	 */
	protected static ThreadLocal tLocal = new ThreadLocal(){
		@Override
		protected synchronized Object initialValue(){
			return new HashMap();
		}
	};

	/**
	 * 取得ThreadLocal HashMap物件
	 * @return ThreadLocal HashMap物件
	 */
	public static HashMap getThreadLocalMap() {
		return (HashMap)tLocal.get();
	}

	/**
	 * 依據key值取得ThreadLocal HashMap物件中的資料
	 * @param key 參數key值
	 * @return 參數資料物件
	 */
	public static Object getThreadLocalData(String key) {
		return ((HashMap)tLocal.get()).get(key);
	}

	/**
	 * 依據key值新增或異動ThreadLocal HashMap物件中的資料
	 * @param key 參數key值
	 * @param obj 參數資料物件
	 */
	public static void putThreadLocalData(String key, Object obj) {
		((HashMap)tLocal.get()).put(key, obj);
	}

	/**
	 * 依據key值移除ThreadLocal HashMap物件中的資料
	 * @param key 參數key值
	 */
	public static void removeThreadLocalData(String key) {
		((HashMap)tLocal.get()).remove(key);
	}

	/**
	 * 清空ThreadLocal HashMap物件中所有的資料
	 */
	public static void clearThreadLocalData() {
		((HashMap)tLocal.get()).clear();
	}
}
