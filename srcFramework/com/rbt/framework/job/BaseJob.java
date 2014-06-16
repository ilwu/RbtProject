package com.rbt.framework.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rbt.framework.SystemLoader;
import com.rbt.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
 * BaseJob
 * @author
 */
public abstract class BaseJob extends QuartzJobBean  {

	/**
	 * Log定義
	 */
	protected Log LOG = LogFactory.getLog(getClass().getName());// Log定義
	protected ApplicationContext  applicationContext = SystemLoader.getApplicationContext();
	protected JobExecutionContext jobExecutionContext;

	public BaseJob(){
	}

	/* (non-Javadoc)
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
	 */
	@Override
	protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
		this.jobExecutionContext = ctx;
		long start_time = System.currentTimeMillis();
		try{
			this.LOG.info("Start Job Process !");
			process();
		}catch (Exception e) {
			this.LOG.error(getClassName()+"Error",e);
			this.LOG.error(StringUtil.getExceptionStackTrace(e));
		}finally{
			this.LOG.info("Job Process Finish!");
		}
		int totolTime = (int) (System.currentTimeMillis() - start_time);
		this.LOG.info("共耗時：[" +  new SimpleDateFormat("mm:ss.SSS").format(new Date(totolTime)) + "] [totolTime]:" + totolTime);
	}

	/**
	 * 主執行方法
	 * @throws Exception
	 */
	protected abstract void process() throws Exception;
	/**
	 * @throws Exception
	 */
	public abstract @ResponseBody String run(HttpServletRequest request);

	/**
	 * 即時同步:頁面使用
	 * @param request
	 * @return
	 */
	protected String realTimeProcess(HttpServletRequest request) {
		long start_time = System.currentTimeMillis();
		try {
			this.process();

			long totolTime =  System.currentTimeMillis() - start_time;
			String time = "共耗時：[" +  new SimpleDateFormat("mm:ss.SSS").format(new Date(totolTime)) + "] [" + totolTime + "]";
			this.LOG.info(time);
			return "執行成功" + time;

		} catch (Exception e) {
			String errorMesg = "即時同步[" + this.getClass().getName() + "]執行失敗!\n" + StringUtil.getExceptionStackTrace(e);
			this.LOG.error(errorMesg);
			return errorMesg.replaceAll("\r\n", "<br/>");
		}
	}

	private String getClassName(){
		String name = this.getClass().getName();
		if(name.lastIndexOf(".")!=-1){
			name = name.substring(name.lastIndexOf(".")+1, name.length());
		}
		return name;
	}
}
