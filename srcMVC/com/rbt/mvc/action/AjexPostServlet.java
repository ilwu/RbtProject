package com.rbt.mvc.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import com.rbt.mvc.action.base.BaseActioin;
import com.rbt.mvc.action.base.BaseActionBean;
import com.rbt.mvc.exception.MvcAppException;
import com.rbt.mvc.exception.MvcBusinessException;
import com.rbt.util.BeanUtil;
import com.rbt.util.StringUtil;

/**
 * AjexPostServlet
 *<br>
 * 內含JSONObject
 * 需以下第三方程式庫
 * commons-beanutils-1.8.3.jar<br>
 * commons-collections-3.2.1.jar<br>
 * commons-lang-2.6.jar<br>
 * commons-logging-1.1.1.jar<br>
 * ezmorph-1.0.6.jar<br>
 * json-lib-2.3-jdk15.jar<br>
 *
 * @author Allen Wu
 */
public class AjexPostServlet extends HttpServlet {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3854934303178894337L;

	/**
	 * LOG4j
	 */
	private static Logger LOG = Logger.getLogger(AjexPostServlet.class);

	/**
	 * constructor
	 */
	public AjexPostServlet() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// =================================================
		// 將內容轉 UTF-8
		// =================================================
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");

		// =================================================
		// 取得前端傳入值
		// =================================================
		// 呼叫的 class
		String actionClass = request.getParameter("_actionClass");
		// 執行的mothed
		String actionName = request.getParameter("_actionName");
		// json 資料要轉成 bean 的 class
		String beanClass = request.getParameter("_beanClass");
		// resultPage
		String resultPage = request.getParameter("_resultPage");
		// post Type :getPageContent、ajaxPost、popup
		String postType = request.getParameter("_postType");

		String errorMessage = "";

		// 檢核設定為空
		if (StringUtil.isEmpty(actionClass)) {
			errorMessage += "未傳入 actionClass !\n";
		}
		if (StringUtil.isEmpty(actionName)) {
			errorMessage += "未傳入 actionName !\n";
		}
		if (StringUtil.isEmpty(beanClass)) {
			errorMessage += "未傳入 beanClass !\n";
		}
		if (StringUtil.isEmpty(postType)) {
			errorMessage += "未傳入 postType !\n";
		}
		if("getPageContent".equals(postType) && StringUtil.isEmpty(resultPage)){
			errorMessage += "未傳入 resultPage !\n";
		}

		if (StringUtil.notEmpty(errorMessage)) {
			this.returnException(response, errorMessage);
			return;
		}

		LOG.info("actionName:" + actionName);

		// =================================================
		// process Bean
		// =================================================
		BaseActionBean bean = null;

		Class<?> beanCls = null;
		try {
			beanCls = Class.forName(beanClass);
			bean = (BaseActionBean) beanCls.newInstance();
		} catch (Exception e) {
			this.returnException(response, "Create bean fale ! class[" + actionClass + "]! ");
			LOG.error(StringUtil.getExceptionStackTrace(e));
			return;
		}

		// copy param to bean
		try {
			// BeanUtil.setRequest2Bean(request, bean);
			BeanUtil.trnsRequest2Bean(request, bean);
			// attrClearFix
			this.attrClearFix(request, beanCls, "__checkBoxRegister");
			this.attrClearFix(request, beanCls, "__radioRegister");
		} catch (Exception e) {
			this.returnException(response, "Request2Bean error! class[" + beanClass + "], 請洽系統資訊人員!");
			LOG.error(StringUtil.getExceptionStackTrace(e));
			return;
		}

		// =================================================
		// 取得 action 並執行
		// =================================================
		Class<?> actionCls = null;
		try {
			actionCls = Class.forName(actionClass);
		} catch (ClassNotFoundException e) {
			this.returnException(response, "Can not found action class[" + actionClass + "]! ");
			LOG.error(StringUtil.getExceptionStackTrace(e));
			return;
		}

		try {
			// class newInstance
			BaseActioin baseActioin = (BaseActioin) actionCls.newInstance();
			// get method
			Method method = actionCls.getMethod("action", new Class[] { BaseActionBean.class, String.class });
			// method invoke
			method.invoke(baseActioin, new Object[] { bean, actionName });

		}catch(InvocationTargetException e){
			LOG.error(StringUtil.getExceptionStackTrace(e.getTargetException()));

			//處理BusinessException
			if(e.getTargetException() instanceof MvcBusinessException){
				this.returnException(response,((MvcBusinessException)e.getTargetException()).getErrorMsg());
				return;
			}
			if(e.getTargetException() instanceof MvcAppException){
				this.returnException(response,((MvcAppException)e.getTargetException()).getErrorMsg());
				return;
			}
			if(e.getTargetException() instanceof Exception){
				this.returnException(response,"系統異常，請洽資訊人員! [" + ((Exception)e.getTargetException()).getMessage() + "]");
				return;
			}

		} catch (Exception e) {
			this.returnException(response, "系統異常, 請洽系統資訊人員! ");
			LOG.error(StringUtil.getExceptionStackTrace(e));
			return;
		}

		// =================================================
		// ajaxPost : 回傳 bean 內容
		// =================================================
		if("ajaxPost".equals(postType)){
			PrintWriter out = response.getWriter();
			String resultStr = JSONObject.fromObject(bean).toString();
			LOG.debug("AjexPost result:\n" + resultStr);
			out.write(resultStr);
			out.close();
			return;
		}

		// =================================================
		// getPageContent、popup
		// =================================================
		// getPageContent popup (此兩項目皆須將結果放到 session)
		request.getSession().setAttribute("result", bean);
		// getPageContent 項目需重新導向新頁
		if("getPageContent".equals(postType)){
			response.sendRedirect(resultPage);
		}
		return;
	}

	private void attrClearFix(HttpServletRequest request, Object bean, String checkParamName) throws Exception {

		String paramRegister = request.getParameter(checkParamName);

		if (StringUtil.isEmpty(paramRegister)) {
			return;
		}
		// 取得 request 中的參數 MAP
		Map<String, String[]> map = request.getParameterMap();

		BeanUtil beanUtil = new BeanUtil();

		for (String checkBoxName : paramRegister.split(",")) {

			// request ˋ中已經有此參數時跳過
			if (map.containsKey(checkBoxName)) {
				continue;
			}

			// 將該參數設為 null
			beanUtil.setObjectParamValue(checkBoxName, bean, null);
		}
	}

	/**
	 * 回傳失敗訊息
	 * @param response HttpServletResponse
	 * @param errorMessage 訊息
	 * @throws IOException
	 */
	private void returnException(HttpServletResponse response, String errorMessage) throws IOException {

		LOG.error(errorMessage);
		JSONObject jSONObject = new JSONObject();
		jSONObject.put("_AjexPostResult", "error");
		jSONObject.put("_AjexPostErrorMessage", errorMessage);
		PrintWriter out = response.getWriter();
		out.write(jSONObject.toString());
		out.close();
		return;
	}
}