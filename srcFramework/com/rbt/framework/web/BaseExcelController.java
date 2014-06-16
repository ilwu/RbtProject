package com.rbt.framework.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.rbt.exception.BaseException;
import com.rbt.util.StringUtil;

public class BaseExcelController {

	/**
	 * Logging utility
	 */
	protected Log LOG = LogFactory.getLog(getClass().getName());

	/**
	 * 建構子
	 */
	public BaseExcelController() {
		//
	}

	/**
	 * @param response
	 * @param baos
	 * @param downloadFileName
	 * @throws IOException
	 */
	protected void exportFile(HttpServletResponse response, ByteArrayOutputStream baos, String downloadFileName) throws IOException {
		java.io.OutputStream out = null;
		try {
			out = response.getOutputStream();

			if(baos == null) {
				throw new java.lang.NullPointerException("exportFile failed: OutputStream is null");
			}

			int p = -1;
			if(downloadFileName != null) {
				p = downloadFileName.lastIndexOf(".");
			} else {
				downloadFileName = "";
			}
			String fileExt = "";
			if(p > -1) {
				fileExt =  downloadFileName.substring(p + 1);
			}
			String MIME = com.rbt.framework.util.Mime.getMimeType(fileExt);

			response.setContentType(MIME + ";charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + new String(downloadFileName.getBytes("BIG5"), "ISO8859-1") );
			response.setHeader("Cache-Control", "");
			response.setHeader("Pragma", "");
			response.setContentLength(baos.size());
			baos.writeTo(out);
		}catch (Exception ex) {
			this.LOG.error(StringUtil.getExceptionStackTrace(ex));
			byte[] msg = ("Internal Error: " + ex).getBytes();
			response.setCharacterEncoding("UTF-8");
			response.setContentType(com.rbt.framework.util.Mime.getMimeType("html") + ";charset=UTF-8");
			response.setContentLength(msg.length);
			out.write(msg);
		}
		finally {
			out.flush();
			out.close();
		}
	}

	public static String encodingFileName(String fileName) {
		String returnFileName = "";
		try {
			returnFileName = URLEncoder.encode(fileName, "UTF-8");
			returnFileName = StringUtils.replace(returnFileName, "+", "%20");
			if (returnFileName.length() > 150) {
				returnFileName = new String(fileName.getBytes("BIG5"), "ISO8859-1");
				returnFileName = StringUtils.replace(returnFileName, " ", "%20");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnFileName;
	}

	/**
	 * @param functionId
	 * @param returnPage
	 * @param e
	 * @param request
	 * @param response
	 */
	public void processException(String functionId, String returnPage, Throwable e, HttpServletRequest request, HttpServletResponse response) {
		this.LOG.error(StringUtil.getExceptionStackTrace(e));
		request.getSession().setAttribute("_function", functionId);
		request.getSession().setAttribute("messageRedirect", returnPage);
		try {
			if (e instanceof RuntimeException) {
				request.getSession().setAttribute("exceptions", e);
				response.sendRedirect(request.getContextPath() + "/message/exception.jsp");
			} else if (e instanceof BaseException) {
				request.getSession().setAttribute("_error", e);
				response.sendRedirect(request.getContextPath() + "/message/msg.jsp");
			} else {
				request.getSession().setAttribute("exceptions", e);
				response.sendRedirect(request.getContextPath() + "/message/exception.jsp");
			}
		} catch (IOException e1) {
			this.LOG.error(StringUtil.getExceptionStackTrace(e1));
		}
	}
}
