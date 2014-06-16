package com.rbt.framework.model;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

import com.rbt.util.DateUtil;

/**
 * DateUtilTag類為頁面日期格式化tag。<p>
 *
 * style:<br>
 * "X" ==> XML格式, "AD" ==> 西元年, "R" ==> 民國年, "F" ==><br>
 * 類似xml格式,但沒有zonetime或"yyyy-mm-dd","yyyy/mm/dd"<br>
 * @author Mark Chen
 *
 */
public class DateUtilTag extends TagSupport {

	private static final long serialVersionUID = -6534111108695076315L;

	private static final String XML_STYLE="X";

	private static final String USER_STYLE="AD";

	private String value;

	/*
	 * style:
	 * "X" ==> XML格式, "AD" ==> 西元年, "R" ==> 民國年, "F" ==>
	 * 類似xml格式,但沒有zonetime或"yyyy-mm-dd","yyyy/mm/dd"
	 */
	private String style;

	private Date date;

	private String type;//type: "D" ==> 取日期, "T" ==> 取時間, "DT" ==> 取日期時間

	private Calendar calendar;

	@Override
	public int doStartTag() throws JspException {
		String outPut=null;//輸出內容
		//設置默認type
		if(this.isBlank(this.type)){
			this.type="D";
		}
		//設置默認style
		if(this.isBlank(this.style)){
			this.style=USER_STYLE;
		}
		//將style轉為統一的形式
		convertStyle();
		try {
			outPut = doDispatch();
			if(outPut==null){
				outPut="";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.pageContext.getOut().print(outPut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public String getPattern() {
		return this.style;
	}

	public void setPattern(String style) {
		this.style = style;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) throws JspException {
		this.value = (String) ExpressionEvaluatorManager.evaluate(
				"value", value.toString(), Object.class, this, this.pageContext);
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) throws JspException{
		this.date = (Date)ExpressionEvaluatorManager.evaluate(
				"date", date.toString(), Object.class, this, this.pageContext);;
	}

	public Calendar getCalendar() {
		return this.calendar;
	}

	public void setCalendar(Calendar calendar) throws JspException {
		this.calendar = (Calendar)ExpressionEvaluatorManager.evaluate(
				"calendar", calendar.toString(), Object.class, this, this.pageContext);
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private void convertStyle(){
		if(this.style.length()>=10){
			String strStyle = this.style.trim().substring(10);
			if(strStyle.equals("yyyy-mm-dd")||strStyle.equals("yyyy-mm-dd".toUpperCase())){
				this.style=XML_STYLE;
			}else{
				this.style=USER_STYLE;
			}
		}

	}

	private String doDispatch() throws Exception{
		String outPut = null;
		if(this.date!=null){
			outPut = dateToString();
		}
		if(!this.isBlank(this.value)){
			outPut = convertString();
		}
		if(this.calendar!=null){
			outPut = calendarToString();
		}
		return outPut;
	}

	private String dateToString() throws Exception{
		if(this.calendar==null){
			this.calendar=Calendar.getInstance();
			this.calendar.setTime(this.date);
			return DateUtil.formatDateTime(this.calendar, this.type, this.style);
		}
		return null;
	}

	private String calendarToString() throws Exception{
		return DateUtil.formatDateTime(this.calendar, this.type, this.style);
	}

	private String convertString(){
		if(this.style.equals(XML_STYLE)){
			if(this.type.equals("T")){
				return DateUtil.formatTime(this.value);
			}
			return DateUtil.formatDateTime(this.value);
		}
		if(this.style.equals(USER_STYLE)){
			if(this.type.equals("T")){
				return DateUtil.formatTimeForUser(this.value);
			}
			return DateUtil.formateDateTimeForUser(this.value);
		}
		return null;
	}

	private boolean isBlank(String str){
		return str==null||str.trim().equals("");
	}
}
