package com.rbt.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

import com.rbt.exception.UtilException;
import com.rbt.util.file.FileUtil;

/**
 * BeanUtil
 * @author Allen Wu
 */
public class BeanUtil {

	/**
	 * LOG4j
	 */
	private static Logger LOG = Logger.getLogger(BeanUtil.class);

	// ====================================================================================
	// 欄位命名法
	// ====================================================================================
	/**
	 * 將字串轉換成駝峰式命名法，但首字為大寫
	 *
	 * @param str
	 *            輸入字串
	 * @return String
	 */
	public static synchronized String prepareToFirstUpperCase(String str) {

		str = str.trim();

		if (str.length() == 0) {
			return "";
		}
		if (str.length() == 1) {
			return str.toUpperCase();
		}
		String[] strAry = new String[] { str };

		// 檢查是否含有 "_" 字元
		for (int i = 0; i < str.length(); i++) {
			if ("_".equals(str.substring(i, i + 1))) {
				strAry = str.split("\\_");
				break;
			}
		}
		String result = "";
		if (strAry.length > 1) {
			for (int i = 0; i < strAry.length; i++) {
				result += strAry[i].substring(0, 1).toUpperCase() + strAry[i].substring(1).toLowerCase();
			}
		} else {
			// 若全部為大寫，則除了首字外全部轉為小寫
			if (strAry[0].matches("[0-9A-Z]+")) {
				return strAry[0].substring(0, 1).toUpperCase() + strAry[0].substring(1).toLowerCase();
			}
			result = strAry[0].substring(0, 1).toUpperCase() + strAry[0].substring(1);
		}
		return result;
	}

	/**
	 * 將字串轉換成駝峰命名樣式
	 *
	 * @param str 輸入字串
	 * @return String
	 * @throws Exception
	 */
	public static synchronized String prepareToCamelCase(String str) throws UtilException {

		str = str.trim();

		// 長度為零的狀況
		if (str.length() == 0) {
			return "";
		}

		// 長度為1的狀況
		if (str.length() == 1) {
			return str.toLowerCase();
		}

		// 依據 "_" 字元作切割
		String[] strAry = str.split("\\_");

		// 沒有"_"的狀況
		if (strAry.length == 1) {
			// 若全部為大寫，則全部轉為小寫
			if (strAry[0].matches("[0-9A-Z]+")) {
				return strAry[0].toLowerCase();
			}
			// 否則只轉字首為小寫
			return strAry[0].substring(0, 1).toLowerCase() + strAry[0].substring(1);
		}

		String result = "";
		String sstr = "";
		try {
			// 將每個字節轉為首字大寫
			for (int i = 0; i < strAry.length; i++) {
				sstr = strAry[i];
				if ("".equals(sstr.trim()))
					continue;
				result += sstr.substring(0, 1).toUpperCase();
				if (sstr.length() > 1)
					result += sstr.substring(1).toLowerCase();
			}
		} catch (Exception e) {
			throw new UtilException("ERROR! strAry:[" + strAry + "],sstr:[" + sstr + "]", e);
		}

		// 整字串首字轉小寫後回傳
		return result.substring(0, 1).toLowerCase() + result.substring(1);
	}

	// ====================================================================================
	// 欄位操作 1 (可取物件中之子物件的內容，以『.』的方式傳入參數)
	// ====================================================================================
	/**
	 * 取得物件中的某個欄位 (支援 "id.xxx.xxx" 形式)
	 * @param paramName
	 * @param obj
	 * @param initGetFields
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws Exception
	 */
	public Object getObjectParamValue(String paramName, Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		if (obj == null) {
			return null;
		}

		Object resultObj = null;

		String[] paramNameAry = paramName.split("\\.");

		if (paramNameAry.length > 1) {
			// 第一個路徑節點參數名稱
			String currParamName = BeanUtil.prepareToCamelCase(paramNameAry[0]);
			resultObj = this.getObjectParamValue(currParamName, obj);
			// 路徑中有任一個點為 null , 直接返回 null
			if (resultObj == null) {
				return null;
			}
			// 取傳入的 paramName, 除去第一組後的路徑
			String subParamPath = paramName.substring(paramNameAry[0].length() + 1);
			return this.getObjectParamValue(subParamPath, resultObj);
		}

		// Map 物件處理
		if (obj instanceof Map || obj instanceof HashMap) {
			return ((Map<?, ?>) obj).get(paramName);
		}

		String mothedName = "get" + paramName.substring(0, 1).toUpperCase();

		if (paramName.length() > 1) {
			mothedName += paramNameAry[0].substring(1);
		}

		Method method = BeanUtil.findDeclaredMethod(obj.getClass(), mothedName, new Class[0]);
		if (method == null) {
			return null;
		}
		return method.invoke(obj, new Object[0]);
	}

	/**
	 * set 物件中的某個欄位 (支援 "id.xxx.xxx" 形式)
	 * @param fieldName
	 * @param targetObj
	 * @param value
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws Exception
	 */
	public void setObjectParamValue(String fieldName, Object targetObj, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		// ===============================================================
		// 檢核
		// ===============================================================
		if (toStringAble(targetObj)) {
			throw new UtilException("setObjectParamValue : 名稱[" + fieldName + "], 型態為[" + targetObj.getClass().getName() + "] 不可存入值!");
		}

		// ===============================================================
		// 傳入欄位名稱為路徑(非欄位) 時，進行遞迴
		// ===============================================================
		String[] paramNameAry = fieldName.split("\\.");
		if (paramNameAry.length > 1) {
			// 第一個路徑節點參數名稱
			String currParamName = BeanUtil.prepareToCamelCase(paramNameAry[0]);
			Object subObj = this.getObjectParamValue(currParamName, targetObj);
			// 路徑中有任一個點為 null , 直接返回 null
			if (subObj == null) {
				return;
			}
			// 取傳入的 paramName, 除去第一組後的路徑
			String subParamPath = fieldName.substring(paramNameAry[0].length() + 1);
			this.setObjectParamValue(subParamPath, subObj, value);
			return;
		}

		// ===============================================================
		// targetObj 為 Map
		// ===============================================================
		// Map 物件處理
		if (targetObj.getClass().isAssignableFrom(Map.class)) {
			((Map<String, Object>) targetObj).put(fieldName, value);
			return;
		}

		// 取得所有可存取的欄位
		LinkedHashMap<String, Object> fieldMap = this.findAaccessibleFields(targetObj.getClass());

		// get field
		Object fieldProcessObj = fieldMap.get(fieldName);

		if (fieldProcessObj != null) {
			// 可直接存取欄位 (取出為 Field)
			if (Field.class.isAssignableFrom(fieldProcessObj.getClass())) {
				((Field) fieldProcessObj).set(targetObj, value);
				return;
			}
			// 需透過 getter 存取欄位 (取出為 HashMap<String, Method>)
			if (HashMap.class.isAssignableFrom(fieldProcessObj.getClass())) {
				HashMap<String, Object> methodMap = (HashMap<String, Object>) fieldProcessObj;
				Method method = (Method) methodMap.get("setter");
				if (method != null) {
					try {
						method.invoke(targetObj, new Object[] { value });
					} catch (java.lang.IllegalArgumentException e) {
						LOG.error("set [" + fieldName + "] fail! ParameterTypes:");
						for (Class<?> clazz : method.getParameterTypes()) {
							LOG.error(clazz.getName());
						}
						throw e;
					}
					return;
				}
			}
		}
		throw new UtilException("setObjectParamValue : 物件[" + targetObj.getClass().getName() + "] 無[" + fieldName + "] 欄位可存入值!");
	}

	// ====================================================================================
	// 檢視 Bean 的內容
	// ====================================================================================

	/**
	 * 解析傳入物件內容
	 * @param targetObj 要解析的物件
	 * @return
	 */
	public String showContent(Object targetObj) {
		return this.showContent(null, targetObj);
	}

	/**
	 * 解析傳入物件內容
	 * @param targetName 顯示的名稱
	 * @param targetObj 要解析的物件
	 * @return
	 */
	public String showContent(String targetName, Object targetObj) {
		StringBuffer sb = new StringBuffer();
		sb.append("\n========================================================\n");
		if (StringUtil.notEmpty(targetName)) {
			String className = "";
			if(targetObj!=null){
				className = targetObj.getClass().getName();
			}
			sb.append("ShowContent: [<" + targetName + "> (" + className + ")]");
		} else {
			if (targetObj != null) {
				sb.append("ShowContent: [" + targetObj.getClass().getName() + "]");
			}
		}
		sb.append("\n========================================================\n");
		try {
			sb.append(this.showContent(targetObj, 0));
		} catch (Throwable e) {
			LOG.error("ShowContent 處理失敗!");
			LOG.error(StringUtil.getExceptionStackTrace(e));
			return "ShowContent 處理失敗!";
		}
		return sb.toString();
	}

	/**
	 * 解析傳入物件內容
	 * @param targetObj 要解析的物件
	 * @param depth 顯示縮排深度
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws Exception
	 */
	public String showContent(Object targetObj, int depth) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		depth++;

		// 初始化 FileUtil
		FileUtil fu = new FileUtil();

		// 判斷傳入為null
		if (targetObj == null) {
			fu.addLine("  <null>");
			return fu.getContent();
		}

		// 取得物件 物件型別名稱
		String objClassName = targetObj.getClass().getName();

		if (this.isPass(objClassName)) {
			return "";
		}

		// =================================================
		// 傳入物件為 Object Array
		// =================================================
		if (Object[].class.isAssignableFrom(targetObj.getClass())) {
			Object[] objs = (Object[]) targetObj;
			fu.addLine("(Array:" + objClassName + ") length=" + objs.length + ":");
			for (int i = 0; i < objs.length; i++) {
				// array index
				fu.addStr(fu.addIndent("[" + i + "]:", depth, false));
				// index content
				fu.addStr(this.showContent(objs[i], depth));
			}
			return fu.getContent();
		}

		// =================================================
		// 傳入物件為 List
		// =================================================
		if (List.class.isAssignableFrom(targetObj.getClass())) {
			List<?> objList = (List<?>) targetObj;
			fu.addLine("(" + objClassName + ") size=" + objList.size() + ":");
			for (int i = 0; i < objList.size(); i++) {
				// List index
				fu.addStr(fu.addIndent("[" + i + "]:", depth, false));
				// index content
				fu.addStr(this.showContent(objList.get(i), depth));
			}
			return fu.getContent();
		}

		// =================================================
		// 傳入物件為 Map
		// =================================================
		if (Map.class.isAssignableFrom(targetObj.getClass())) {
			Map<?, ?> objMap = (Map<?, ?>) targetObj;
			fu.addLine("(" + objClassName + ")");
			Iterator<?> iterator = objMap.keySet().iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				// key
				fu.addStr(fu.addIndent("[" + key + "]:", depth, false));
				// index content
				fu.addStr(this.showContent(objMap.get(key), depth));
			}
			return fu.getContent();
		}

		// =================================================
		// 傳入物件為 Set
		// =================================================
		if (Set.class.isAssignableFrom(targetObj.getClass())) {
			Set<?> objSet = (Set<?>) targetObj;
			fu.addLine("(" + objClassName + ")");
			for (Object object : objSet) {
				fu.addStr(fu.addIndent(":", depth, false));
				fu.addStr(this.showContent(object, depth));
			}
			return fu.getContent();
		}

		// =================================================
		// pass
		// =================================================
		String[] passAry = new String[] { "java.io.", "org.apache.struts"};
		for (String str : passAry) {
			if (targetObj.getClass().getName().indexOf(str) > -1) {
				fu.addLine("<" + targetObj.toString() + ">	(" + targetObj.getClass().getName() + ")");
				return fu.getContent();
			}
		}

		// =================================================
		// 可 ToString 物件
		// =================================================
		if (this.toStringAble(targetObj)) {
			fu.addLine("<" + targetObj.toString() + ">	(" + targetObj.getClass().getName() + ")");
			return fu.getContent();
		}

		// =================================================
		// Bean
		// =================================================
		LinkedHashMap<String, Object> fieldMap = this.findAaccessibleFields(targetObj.getClass());
		Iterator<String> iterator = fieldMap.keySet().iterator();
		fu.addLine("(" + targetObj.getClass().getName() + "):");
		while (iterator.hasNext()) {
			// fieldName
			String fieldName = iterator.next();
			fu.addStr(fu.addIndent("[" + fieldName + "]:", depth, false));
			// get field
			Object fieldProcessObj = fieldMap.get(fieldName);

			if (fieldProcessObj != null) {
				// 可直接存取欄位 (取出為 Field)
				if (Field.class.isAssignableFrom(fieldProcessObj.getClass())) {
					fu.addStr(this.showContent(((Field) fieldProcessObj).get(targetObj), depth));
					continue;
				}
				// 需透過 getter 存取欄位 (取出為 HashMap<String, Method>)
				if (HashMap.class.isAssignableFrom(fieldProcessObj.getClass())) {
					HashMap<String, Object> methodMap = (HashMap<String, Object>) fieldProcessObj;
					Method method = (Method) methodMap.get("getter");
					if (method != null) {
						if (method.getModifiers() != Modifier.SYNCHRONIZED) {
							try {
								Object subobj = method.invoke(targetObj, new Object[] {});
								fu.addStr(this.showContent(subobj, depth));
							} catch (IllegalAccessException e) {
								fu.addLine(e.getMessage());
							}
						} else {
							fu.addLine("is SYNCHRONIZED");
						}

						continue;
					}
				}
			}
			fu.addLine(" Unreadable !");
		}
		return fu.getContent();
	}

	private boolean isPass(String objClassName) {
		// 依據名稱判斷
		if (objClassName.indexOf("org.hibernate.proxy.AbstractLazyInitializer") > 0) {
			return true;
		}
		return false;
	}

	/**
	 * @param obj
	 * @return
	 */
	private boolean toStringAble(Object obj) {
		// 依據名稱判斷
		String className = obj.getClass().getName().toLowerCase();
		String[] classNameAry = new String[] { "jsonarray", "stringbuffer", "timestamp", "boolean" };
		for (String name : classNameAry) {
			if (className.indexOf(name) > -1) {
				return true;
			}
		}

		String[] startWithAry = new String[]{"jxl.", "java.io."};
		for (String name : startWithAry) {
			if (className.startsWith(name)) {
				return true;
			}
		}

		// 依據 class 判斷
		Class<? extends Object> objClass = obj.getClass();
		Class[] classAry = new Class[] { Integer.class, int.class, Double.class, double.class, Float.class, float.class, Long.class, long.class, String.class, BigDecimal.class, boolean.class,
				Class.class };
		for (Class<?> clazz : classAry) {
			if (clazz.isAssignableFrom(objClass)) {
				return true;
			}
		}

		return false;
	}

	// ====================================================================================
	// Class finder
	// ====================================================================================
	private static LinkedHashMap<String, LinkedHashMap<String, Object>> classFieldMap = 
			new LinkedHashMap<String, LinkedHashMap<String, Object>>();

/**
	 * 取得 class 中所有可存取的 field
	 * @param clazz
	 * @return LinkedHashMap<field 名稱 , field 或 HashMap<'getter||setter',Method>>
	 */
	public LinkedHashMap<String, Object> findAaccessibleFields(Class<?> clazz) {

		// 以 ThreadLocal 進行快取
		String storeKey = "findAaccessibleFields_" + clazz.getName();
		LinkedHashMap<String, Object> fieldMap = classFieldMap.get((storeKey));
		if (fieldMap != null) {
			return fieldMap;
		}
		fieldMap = new LinkedHashMap<String, Object>();
		classFieldMap.put(storeKey, fieldMap);
		// DataHolder.putThreadLocalData(storeKey, fieldMap);

		// class 本身, 以及父類別所具有的 public field
		Field[] publicFields = clazz.getFields();
		for (Field field : publicFields) {
			fieldMap.put(field.getName(), field);
		}

		// class 本身所有的 field (public、private、proteced)
		List<Field> fieldList = new ArrayList<Field>();
		fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));

		// 取得父類別 field (private、proteced)
		if (clazz.getSuperclass() != null) {
			this.getSupperClassPrivateField(clazz.getSuperclass(), fieldList);
		}

		for (Field field : fieldList) {
			if (!isPublic(field.getModifiers())) {
				String fieldName = field.getName().substring(0, 1).toUpperCase();
				if (field.getName().length() > 1) {
					fieldName += field.getName().substring(1);
				}
				HashMap<String, Object> methodMap = new HashMap<String, Object>();

				// 取得 getter
				Method getter = BeanUtil.findDeclaredMethod(clazz, "get" + fieldName, new Class[0]);
				if (getter != null) {
					methodMap.put("getter", getter);
				}
				// 取得 setter
				Method setter = BeanUtil.findDeclaredMethod(clazz, "set" + fieldName, new Class[] { field.getType() });
				if (setter != null) {
					methodMap.put("setter", setter);
				}

				if (getter != null || setter != null) {
					methodMap.put("field", field);
					fieldMap.put(field.getName(), methodMap);
				}
			}
		}

		return fieldMap;
	}

	private void getSupperClassPrivateField(Class<?> clazz, List<Field> list) {

		if (clazz.getSuperclass() != null) {
			this.getSupperClassPrivateField(clazz.getSuperclass(), list);
		}

		Field[] currClassFields = clazz.getDeclaredFields();

		for (Field field : currClassFields) {
			if (!isPublic(field.getModifiers())) {
				list.add(field);
			}
		}
	}

	/**
	 * 尋找 class 本身以及其父類別的 method
	 * @param clazz Class
	 * @param methodName method Name
	 * @param paramTypes param Types <class>
	 * @return
	 */
	public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
		try {
			return clazz.getDeclaredMethod(methodName, paramTypes);
		} catch (NoSuchMethodException ex) {
			if (clazz.getSuperclass() != null) {
				return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
			}
			return null;
		}
	}

	// ====================================================================================
	// Other
	// ====================================================================================
	/**
	 * 將 bean 中 null 欄位設為空白
	 * @param targetObj
	 */
	public void formatNull2EmptyString(Object targetObj) {

		LinkedHashMap<String, Object> fieldMap = this.findAaccessibleFields(targetObj.getClass());
		Iterator<String> iterator = fieldMap.keySet().iterator();

		while (iterator.hasNext()) {
			// fieldName
			String fieldName = iterator.next();
			// get field
			Object fieldProcessObj = fieldMap.get(fieldName);

			if (fieldProcessObj != null) {

				// 可直接存取欄位 (取出為 Field)
				if (Field.class.isAssignableFrom(fieldProcessObj.getClass())) {
					Field field = (Field) fieldProcessObj;
					if (String.class.isAssignableFrom(field.getType())) {
						try {
							if (field.get(targetObj) == null) {
								field.set(targetObj, "");
								continue;
							}
						} catch (Exception e) {
							LOG.error("Exception, formatNull2EmptyString [" + fieldName + "] pass !");
							LOG.error(StringUtil.getExceptionStackTrace(e));
							continue;
						}
					}
				}

				// 需透過 getter 存取欄位 (取出為 HashMap<String, Method>)
				if (HashMap.class.isAssignableFrom(fieldProcessObj.getClass())) {
					HashMap<String, Object> methodMap = (HashMap<String, Object>) fieldProcessObj;
					Field field = (Field) methodMap.get("field");
					Method getter = (Method) methodMap.get("getter");
					Method setter = (Method) methodMap.get("setter");

					if (field == null || !String.class.isAssignableFrom(field.getType()) || getter == null || setter == null) {
						continue;
					}
					try {
						setter.invoke(targetObj, new Object[] { "" });
					} catch (Exception e) {
						LOG.error("Exception, formatNull2EmptyString [" + fieldName + "] pass !");
						LOG.error(StringUtil.getExceptionStackTrace(e));
						continue;
					}
				}
			}
		}
	}

	public static String showRequest(HttpServletRequest request) {
		FileUtil fu = new FileUtil();

		fu.addLine("=================================================");
		fu.addLine("Attribute");
		fu.addLine("=================================================");
		Enumeration<?> attrEmu = request.getAttributeNames();
		while (attrEmu.hasMoreElements()) {
			String name = (String) attrEmu.nextElement();
			fu.addLine(name + ":" + request.getAttribute(name));
		}

		fu.addLine("=================================================");
		fu.addLine("Parameter");
		fu.addLine("=================================================");
		Enumeration<?> paramEmu = request.getParameterNames();
		while (paramEmu.hasMoreElements()) {
			String name = (String) paramEmu.nextElement();
			fu.addLine(name + ":" + request.getParameter(name));
		}
		return fu.getContent();
	}

	public static void trnsRequest2Bean(HttpServletRequest request, Object bean) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		BeanUtil beanUtil = new BeanUtil();
		Enumeration<String> paramEmu = request.getParameterNames();
		while (paramEmu.hasMoreElements()) {
			String name = paramEmu.nextElement();
			try {
				beanUtil.setObjectParamValue(name, bean, request.getParameter(name));
			} catch (UtilException e) {
				//
			}
		}
	}

	// ====================================================================================
	// Map to Bean
	// ====================================================================================
	/**
	 * 將 Map 中的資料放入 bean
	 * @param dataMap 資料 Map
	 * @param cls bean 的 class
	 * @param LOG Log物件
	 * @return Object bean
	 * @throws Exception
	 */
	public Object map2Bean(Map<String, ?> dataMap, Class<?> cls, Logger LOG) throws Exception {

		Object bean = null;
		try {
			bean = cls.newInstance();
		} catch (Exception e) {
			LOG.error("class:[" + cls.getName() + "] 初始化失敗!");
			e.printStackTrace();
			throw e;
		}

		// 取出bean裏的所有方法, 並放入Map
		Method[] methods = cls.getMethods();
		HashMap<String, Method> methodNameMap = new HashMap<String, Method>();
		for (Method method : methods) {
			// 參數不為1 時略過
			if (method.getParameterTypes().length != 1) {
				continue;
			}
			// 只提取 set 開頭的 method
			if (method.getName().startsWith("set")) {
				methodNameMap.put(method.getName(), method);
			}
		}

		Iterator<String> it = dataMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();

			if (dataMap.get(key) == null) {
				continue;
			}

			// ==================================
			// 組方法名稱
			// ==================================
			String[] keyAry = key.toLowerCase().split("_");
			String methodName = "set";
			for (String paramNamePart : keyAry) {
				methodName += paramNamePart.substring(0, 1).toUpperCase();
				if (paramNamePart.length() > 1) {
					methodName += paramNamePart.substring(1);
				}
			}
			// ==================================
			// 方法不存在時跳過
			// ==================================
			if (!methodNameMap.containsKey(methodName)) {
				LOG.debug("[" + methodName + "] not in [" + cls.getName() + "], " + key + ":[" + dataMap.get(key) + "] pass!");
				continue;
			}

			// ==================================
			// 將值放入 bean
			// ==================================
			Method method = methodNameMap.get(methodName);

			if (!dataMap.get(key).getClass().getName().equals(method.getParameterTypes()[0].getName())) {
				if (method.getParameterTypes()[0].getName().equals(String.class.getName())) {
					LOG.debug(key + " toString :" + dataMap.get(key));
					method.invoke(bean, new Object[] { dataMap.get(key).toString() });
				}
			} else {
				method.invoke(bean, new Object[] { dataMap.get(key) });
			}
		}

		return bean;
	}

	// ====================================================================================
	// 修飾子判斷
	// ====================================================================================
	/**
	 * The <code>int</code> value representing the <code>public</code> modifier.
	 */
	public static final int PUBLIC = 0x00000001;

	/**
	 * The <code>int</code> value representing the <code>private</code> modifier.
	 */
	public static final int PRIVATE = 0x00000002;

	/**
	 * The <code>int</code> value representing the <code>protected</code> modifier.
	 */
	public static final int PROTECTED = 0x00000004;

	/**
	 * The <code>int</code> value representing the <code>static</code> modifier.
	 */
	public static final int STATIC = 0x00000008;

	public static boolean isStatic(int mod) {
		return (mod & STATIC) != 0;
	}

	public static boolean isPublic(int mod) {
		return (mod & PUBLIC) != 0;
	}

	public static boolean isPrivate(int mod) {
		return (mod & PRIVATE) != 0;
	}

	public static boolean isProtected(int mod) {
		return (mod & PROTECTED) != 0;
	}
}
