package com.rbt.framework.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import com.rbt.framework.SystemLoader;
import com.rbt.framework.util.ServiceAttr;
import com.rbt.util.StringUtil;

/**
 * <tt>AutoWireHandlerMapping</tt>類實現URL-mapped。<br>
 * 根據server.xml定義檔，將設定的url mapping 到相應的handler。
 */
public class AutoWireHandlerMapping extends AbstractUrlHandlerMapping{
	protected final Log logger = LogFactory.getLog(getClass());
	//Control類package
	private String actionClassPath;
	//url訪問後綴
	private String suffix;
	//service配置定義
	private HashMap serviceConfig;

	public String getActionClassPath() {
		return this.actionClassPath;
	}

	public void setActionClassPath(String actionClassPath) {
		this.actionClassPath = actionClassPath;
	}

	private final Map urlMap = new HashMap();

	public void setUrlMap(Map urlMap) {
		this.urlMap.putAll(urlMap);
	}

	public void setMappings(Properties mappings) {
		this.urlMap.putAll(mappings);
	}

	/**
	 * 功能：根據service.xml定義實現url映射
	 * @exception BeansException
	 */
	@Override
	public void initApplicationContext() throws BeansException {
		//設置上下文context
		SystemLoader.setApplicationContext(this.getWebApplicationContext());
		SystemLoader.init();

		initInterceptors();

		//取得service配置
		this.serviceConfig  = SystemLoader.getServiceConfig();
		Iterator itr = this.serviceConfig.keySet().iterator();
		try{
			while (itr.hasNext()) {
				String service=(String)itr.next();
				//取得各個service屬性值
				HashMap serviceAttrMap = (HashMap)this.serviceConfig.get(service);
				if (serviceAttrMap.containsKey(ServiceAttr.URL)) { // Single type service
					String handlerName=(String)serviceAttrMap.get(ServiceAttr.NAME);
					String beanName=(String)serviceAttrMap.get(ServiceAttr.PROCESS);
					String commandName=(String)serviceAttrMap.get(ServiceAttr.COMMAND_NAME);
					String url=(String)serviceAttrMap.get(ServiceAttr.URL)+this.suffix;
					Object handler = autoWireHandler(handlerName,beanName,commandName);
					// prepend with slash if it's not present
					if (!url.startsWith("/")) {
						url = "/" + url;
					}
					registerHandler(url, handler);
				} else { // Multi type service(setp 模式)
					HashMap stepConfig = (HashMap) serviceAttrMap.get(ServiceAttr.STEP);
					if(stepConfig != null){
						Iterator itStep = stepConfig.keySet().iterator();
						String handlerName=(String)serviceAttrMap.get(ServiceAttr.NAME);
						String beanName=(String)serviceAttrMap.get(ServiceAttr.PROCESS);
						String commandName=(String)serviceAttrMap.get(ServiceAttr.COMMAND_NAME);
						Object handler = autoWireHandler(handlerName,beanName,commandName);

						List urlList=new ArrayList();
						while (itStep.hasNext()){
							String stepName=(String)itStep.next();
							HashMap stepAttrMap=(HashMap)stepConfig.get(stepName);
							if(stepAttrMap.containsKey(ServiceAttr.URL)){
								String url=(String)stepAttrMap.get(ServiceAttr.URL)+this.suffix;
								// prepend with slash if it's not present
								if (!url.startsWith("/")) {
									url = "/" + url;
								}
								urlList.add(url);
							}
						}

						if(urlList.size()>0){
							registerHandler((String[])urlList.toArray(new String[0]),handler);
						}
					} else {
						this.logger.warn("Have no step configuration! ");
					}
				}
			}
		}catch(Exception e){
			this.logger.error("initApplicationContext ERR!!"+e.toString());
			this.logger.error(StringUtil.getExceptionStackTrace(e));
		}

		//支持配置
		if (this.urlMap.isEmpty()) {
			this.logger.info("'urlMap' not set on SimpleUrlHandlerMapping");
		}else {
			Iterator itr1 = this.urlMap.keySet().iterator();
			while (itr1.hasNext()) {
				String url = (String) itr1.next();
				Object handler = autoWireHandler(this.urlMap.get(url).toString(),this.urlMap.get(url).toString(),"command");
				// prepend with slash if it's not present
				if (!url.startsWith("/")) {
					url = "/" + url;
				}
				registerHandler(url, handler);
			}
		}
	}

	protected Object autoWireHandler(String handlerName,String beanName,String commandName){
		Object handler=null;
		try{
			try{
				//支持配置
				handler=((AbstractApplicationContext) getApplicationContext()).getBeanFactory().getBean(handlerName);
				//found bean define
				return handlerName;
			}catch (NoSuchBeanDefinitionException e) {
				//do nothing,go down
			}

			//autoWire by name
			handler=((AbstractApplicationContext) getApplicationContext()).getBeanFactory().
			autowire(Class.forName(beanName), AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
			BeanUtils.setProperty(handler,"handlerName",handlerName);
			if(StringUtil.notEmpty(commandName)){
				BeanUtils.setProperty(handler,"commandName",commandName);
			}
		}catch (Exception e){
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Not fonund handler for [" + handlerName + "] in autoWire");
			}
		}
		if(handler!=null){
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Found handler [" + handlerName + "]");
			}
			return handler;
		}
		return handlerName;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * Register the specified handler for the given URL paths.
	 * @param urlPaths the URLs that the bean should be mapped to
	 * @param beanName the name of the handler bean
	 * @throws BeansException if the handler couldn't be registered
	 * @throws IllegalStateException if there is a conflicting handler registered
	 */
	protected void registerHandler(String[] urlPaths, Object handler) throws BeansException, IllegalStateException {
		Assert.notNull(urlPaths, "URL path array must not be null");
		for (int j = 0; j < urlPaths.length; j++) {
			registerHandler(urlPaths[j], handler);
		}
	}

	public String getSuffix() {
		return this.suffix;
	}
}
