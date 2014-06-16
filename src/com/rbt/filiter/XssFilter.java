package com.rbt.filiter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.rbt.util.XssUtil;

/**
 * Servlet Filter implementation class XssFilter
 */
public class XssFilter implements Filter {

	static class FilteredRequest extends HttpServletRequestWrapper {

		public FilteredRequest(ServletRequest request) {
			super((HttpServletRequest) request);
		}

		public String getParameter(String paramName) {
			String value = super.getParameter(paramName);
			return XssUtil.XssEncode(value);
		}

		public String[] getParameterValues(String paramName) {
			String values[] = super.getParameterValues(paramName);

			for (int index = 0; index < values.length; index++) {
				values[index] = XssUtil.XssEncode(values[index]);
			}
			return values;
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		System.out.println("httpServletRequest.getPathTranslated():" + httpServletRequest.getPathTranslated());
		System.out.println("XssFilter..");
		chain.doFilter(new FilteredRequest(request), response);
	}

	public void destroy() {
		//
	}

	public void init(FilterConfig filterConfig) {
		//
	}

}
