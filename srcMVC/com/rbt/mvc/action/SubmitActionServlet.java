package com.rbt.mvc.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.rbt.mvc.action.base.BaseActioin;
import com.rbt.mvc.action.base.BaseActionBean;
import com.rbt.mvc.exception.MvcAppException;
import com.rbt.mvc.exception.MvcBusinessException;
import com.rbt.util.BeanUtil;
import com.rbt.util.StringUtil;

/**
 * SubmitActionServlet
 * @author Allen Wu
 */
public class SubmitActionServlet extends HttpServlet {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -239235721244396800L;

	/**
	 * LOG4j
	 */
	private static Logger LOG = Logger.getLogger(SubmitActionServlet.class);

	/**
	 * constructor
	 */
	public SubmitActionServlet() {
		// javascript 呼叫使用範例
		// var ajAction = new ajexAction();
		// ajAction.actionClass = 'com.turbotech.disable.action.F08_10_299Action';
		// ajAction.actionName = 'getList';
		// ajAction.beanClass = 'com.turbotech.disable.actionbean.F08_10_299Bean';
		// ajAction.postAreaId = 'xForm';
		// ajAction.post();
	}

	/*
	 * (non-Javadoc)
	 *
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
		String actionClass = StringUtil.safeTrim(request.getParameter("_actionClass"));
		// 執行的mothed
		String actionName = StringUtil.safeTrim(request.getParameter("_actionName"));
		// json 資料要轉成 bean 的 class
		String beanClass = StringUtil.safeTrim(request.getParameter("_beanClass"));
		// target Page
		String targetPage = StringUtil.safeTrim(request.getParameter("_targetPage"));
		// initBean
		String initBean = StringUtil.safeTrim(request.getParameter("_initBean"));

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
		if (StringUtil.isEmpty(targetPage)) {
			errorMessage += "未傳入 targetPage !\n";
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
		// 由 Session 中取得 bean
		if (request.getSession().getAttribute("command") != null && StringUtil.isEmpty(initBean)) {
			// 由 Session 中取得 bean
			bean = (BaseActionBean) request.getSession().getAttribute("command");
			// 檢查 已存在的 bean 是否和傳入指定的 class 相同
			if (!bean.getClass().getName().equals(beanClass)) {
				LOG.debug("已存在的 Bean 型態不同! old:[" + bean.getClass() + "],new [" + beanClass + "]");
				bean = null;
			}
		}else{
			LOG.debug("command is null or set initBean!");
		}

		// 若 bean 不存在或需重建時，產生一個新的 bean
		if (bean == null) {
			Class<?> beanCls = null;
			try {
				beanCls = Class.forName(beanClass);
				bean = (BaseActionBean) beanCls.newInstance();
			} catch (Exception e) {
				this.returnException(response, "Create bean fail ! class[" + actionClass + "]! ");
				LOG.error(StringUtil.getExceptionStackTrace(e));
				return;
			}
		}

		// copy param to bean
		try {
			//BeanUtil.setRequest2Bean(request, bean);
			BeanUtil.trnsRequest2Bean(request, bean);
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
		// 回傳結果
		// =================================================
		request.getSession().setAttribute("command", bean);

		//System.out.println(new BeanUtil().showFieldValue(bean));

		// =================================================
		// 跳轉頁面
		// =================================================
		response.sendRedirect(targetPage);
	}

	/**
	 * 回傳失敗訊息
	 * @param response HttpServletResponse
	 * @param errorMessage 訊息
	 * @throws IOException
	 */
	private void returnException(HttpServletResponse response, String errorMessage) throws IOException {

		LOG.error(errorMessage);
		StringBuffer sb = new StringBuffer();
		sb.append("<script type=\"text/javascript\">");
		sb.append("alert('" + errorMessage + "');");
		sb.append("history.go(-1);");
		sb.append("</script>");

		PrintWriter out = response.getWriter();
		out.write(sb.toString());
		out.close();
	}
}
