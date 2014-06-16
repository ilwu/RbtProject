package com.rbt.framework;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rbt.framework.model.SystemConfig;
import com.rbt.framework.util.DOM;
import com.rbt.framework.util.ServiceAttr;
import com.rbt.util.StringUtil;

/**
 * 系統參數初始設定 , server.xml 載入、設置系統路徑
 * @author Allen
 */
public class SystemLoader {
	// log4J
	private static Logger LOG = Logger.getLogger(SystemLoader.class);

	// Private properities for service
	private static String sysDir = null;
	private static String sysErr = null;
	private static Document serviceDoc;// 存儲service.xml
	// System service config
	private static HashMap sysService;
	// System service config
	private static HashMap urlService;
	// 系統上下文context
	private static WebApplicationContext applicationContext;
	/**
	 * 系統 *.properties 設定檔 (SystemLoader 不在 init 時讀入)
	 */
	private static Properties sysProperties = null;

	/**
	 * 功能：初始化配置文件。設定系統目錄；加載server.xml,menu.xml。
	 *
	 */
	public static void init() {
		try {
			// getApplicationContext();
			if (applicationContext == null) {
				throw new IllegalStateException("Cannot initialize SystemLoader's WebApplicationContext - "
						+ "check whether you have ContextLoader* definitions in your web.xml!");
			}
			LOG.info("SystemLoader initial start..");

			// 取得系統根目錄
			sysDir = applicationContext.getServletContext().getRealPath("/");

			LOG.debug("The System Path is :" + sysDir);

			// 設置配置文件根目錄
			String cfgDir = sysDir + "/WEB-INF/classes/";

			// 加載service.xml
			serviceDoc = DOM.loadDoc(cfgDir + "service.xml");
			loadSysService();

			//讀取系統設定
			loadSystemConfig();

			LOG.info("SystemLoader initial finish..");
		} catch (Exception ex) {
			// store the initial error message for display.
			sysErr = ex.toString();
			LOG.error("SystemLoader initial exception:" + sysErr);
		}
	}

	/**
	 * 讀取系統設定 (xxx.Properties)
	 */
	private static void loadSystemConfig() {
		SystemConfig systemConfig = (SystemConfig) applicationContext.getBean("SystemConfig");
		if (systemConfig == null) {
			LOG.warn("bean name:[SystemConfig] was not setting in applicationContent!");
			return;
		}
		if (StringUtil.isEmpty(systemConfig.getPropertiesName())) {
			LOG.warn("propertiesName 未設定");
			return;
		}

		SystemLoader.sysProperties = new Properties();
		WebApplicationContext wac = SystemLoader.getApplicationContext();
		try {
			if (wac != null) {
				SystemLoader.sysProperties.load(wac.getResource("classpath:" + systemConfig.getPropertiesName()).getInputStream());
			} else {
				SystemLoader.sysProperties.load(ClassLoader.getSystemResource(systemConfig.getPropertiesName()).openStream());
			}
		} catch (IOException e) {
			LOG.error(StringUtil.getExceptionStackTrace(e));
			return;
		}
	}

	/**
	 * 取得系統設定
	 * @param key
	 * @return
	 */
	public static String getSysProperties(String key) {
		if(SystemLoader.sysProperties==null){
			LOG.warn("系統無設定檔!");
			return "";
		}
		return SystemLoader.sysProperties.getProperty(key);
	}


	/**
	 * 功能:取得系統根目錄
	 * @return String 系統根目錄
	 */
	public static String getSysDir() {
		return sysDir;
	}

	/**
	 * 功能:取得系統初始錯誤信息
	 * @return String 初始化錯誤信息, null means OK.
	 */
	public static String getSysErr() {
		return sysErr;
	}

	/**
	 * 取得service.xml定義
	 * @return HashMap
	 */
	public static HashMap getServiceConfig() {
		return sysService;
	}

	/**
	 * 取得當前系統上下文context
	 * @return WebApplicationContext
	 */
	public static WebApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(WebApplicationContext applicationContext) {
		SystemLoader.applicationContext = applicationContext;
	}

	/*
	 * 功能：加載service.xml
	 */
	private static void loadSysService() {
		sysService = new HashMap();
		urlService = new HashMap();
		// url name
		String url = null;
		// Load service list
		NodeList serviceList = serviceDoc.getElementsByTagName(ServiceAttr.SERVICE);
		for (int i = 0; serviceList != null && i < serviceList.getLength(); i++) {
			Element serviceNode = (Element) serviceList.item(i);
			// 獲取service名字
			String name = serviceNode.getAttribute(ServiceAttr.NAME);
			// 獲取service 對應 屬性
			HashMap serviceAttrMap = getNodeAttributes(serviceNode);

			sysService.put(name, serviceAttrMap);
			// 獲取url 屬性
			if (serviceAttrMap.containsKey(ServiceAttr.URL)) {
				url = (String) serviceAttrMap.get(ServiceAttr.URL);
				urlService.put(url, serviceAttrMap);
				// System.out.println("name/url:"+name+"/"+url);
			}
			// 判斷service 是否配置step模式
			HashMap stepMap = new HashMap();
			NodeList stepList = serviceNode.getElementsByTagName(ServiceAttr.STEP);
			for (int j = 0; stepList != null && j < stepList.getLength(); j++) {
				Element stepNode = (Element) stepList.item(j);

				String stepUrl = stepNode.getAttribute(ServiceAttr.URL);

				// 獲取url 屬性
				if (stepUrl != null) {
					urlService.put(stepUrl, getNodeAttributes(stepNode));
					// System.out.println("name/stepurl:"+name+"/"+stepUrl);
				}
				stepMap.put(stepUrl, getNodeAttributes(stepNode));
			}
			if (stepList != null && stepList.getLength() > 0) {
				serviceAttrMap.put(ServiceAttr.STEP, stepMap);
			}
		}
	}

	/**
	 * 功能：取得server.xml中指定url 設定屬性值
	 * @param String url
	 * @return HashMap
	 */
	public static HashMap getUrlAttributes(String url) {
		if (urlService.containsKey(url)) {
			return (HashMap) urlService.get(url);
		}
		return null;
	}

	/**
	 * 取得xml結點屬性
	 * @param node
	 * @return
	 */
	private static HashMap getNodeAttributes(Node node) {
		HashMap attrMap = new HashMap();

		NamedNodeMap attrList = node.getAttributes();
		for (int i = 0; attrList != null && i < attrList.getLength(); i++) {
			Node attr = attrList.item(i);
			attrMap.put(attr.getNodeName(), attr.getNodeValue());
		}

		return attrMap;
	}

}
