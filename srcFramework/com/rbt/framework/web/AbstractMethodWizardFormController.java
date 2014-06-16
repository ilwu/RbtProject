package com.rbt.framework.web;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.rbt.exception.UtilException;
import com.rbt.framework.SystemLoader;
import com.rbt.framework.exception.ApplicationException;
import com.rbt.framework.exception.BusinessException;
import com.rbt.framework.exception.PopupException;
import com.rbt.framework.model.BaseCommand;
import com.rbt.framework.model.Command;
import com.rbt.framework.util.Constants;
import com.rbt.framework.util.ServiceAttr;
import com.rbt.framework.web.AutoWireHandlerMapping;
import com.rbt.util.BeanUtil;
import com.rbt.util.DateUtil;
import com.rbt.util.StringUtil;
import com.rbt.util.file.FileUtil;
/**
 * <tt>AbstractMethodWizardFormController</tt>抽象類實現web層的控制轉向。
 * 該類讀取service.xml檔中配置完成頁面的轉向。
 * <p>
 * <pre>
 * 此類中有兩個protected屬性所用説明：
 * protected String returnView=null;//特殊返回
 * 可以指定returnView的值，來替換service.xml中定義的result節點定義的value值。
 * For Example：
 *  service.xml中service定義可能會是這樣：
 *  <service name="C0101Q"  class="com.hitrust.acquire.web.action.C0101" >
 *		<step url="/ap01/01/01"  method="forward" function="DF0101F" result="/ap01/01/01" error="/ap01/01/01"/>
 *	</service>
 * 該定義中url("/ap01/01/01")成功的頁面轉向是 result("/ap01/01/01"),如果指定returnView就可以將成功的頁面轉向returnView定義的值。
 *
 * protected String exceptionView=Constants.EXCEPTION_BACK_URL;//exception返回url
 * 可以指定exceptionView的值，來替換service.xml中定義的error節點定義的value值。
 * 跟returnView的作用類似。
 * </pre>
 * @author jmiuhan
 *
 */
@SuppressWarnings("deprecation")
public class AbstractMethodWizardFormController extends SimpleFormController{
	protected Log LOG=LogFactory.getLog(getClass().getName());

	private static final String BACK_FLAG = "_back";

	private static final String INIT_FLAG = "_init";

	private AutoWireHandlerMapping urlMapping;

	private String handlerName;//handler Name

	protected static final String RETURN_NULL = "RETURN_NULL";

	protected String returnView=null;//特殊返回

	protected String exceptionView=Constants.EXCEPTION_BACK_URL;//exception返回url

	public String getHandlerName() {
		return this.handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	/**
	 * 構造方法
	 */
	public AbstractMethodWizardFormController() {
		// AbstractMethodWizardFormController sets default cache seconds to 0.
		super();

		// Always needs session to keep data from all pages.
		setSessionForm(true);

		// Never validate everything on binding ->wizards validate individual pages.
		setValidateOnBinding(false);
	}

	/**
	 * 功能：初始化command物件
	 * @param Command command
	 */
	public void initCommand(Object command){
		if(this.LOG.isDebugEnabled()){
			this.LOG.debug("Command been init!");
		}
		try {
			//清空command物件
			BeanUtils.copyProperties(createCommand(),command);
		}catch (Exception e) {
			throw new RuntimeException("initCommand error",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected final ModelAndView processFormSubmission(HttpServletRequest request,
		HttpServletResponse response, Object command, BindException errors)throws Exception{
		//上下文件路徑
		String contextPath=request.getContextPath();
		//訪問路徑
		String url=request.getRequestURI();
		//獲取url連接路徑;訪問路徑:"/aproot/00/00.html";轉換后路徑-->/00/00
		url=StringUtil.getMiddleStr(contextPath,url,this.urlMapping.getSuffix());
		//獲取url mapping配置,from service.xml
		HashMap urlAttrMap = SystemLoader.getUrlAttributes(url);
		//訪問路徑不存在
		if(urlAttrMap==null){
			this.LOG.error("access url not Exist:["+url+"], urlAttrMap ==null");
			return showForm(request, errors, "login.html");
			//throw new RuntimeException("access url not Exist:["+url+"] illegal argument:",null);
		}
		//需要執行方法名
		String processMethodName=null;
		//是否初始化command物件標記
		String initFlag=null;
		//是否以back方式跳轉標記
		String backFlag=request.getParameter(BACK_FLAG);

		if(urlAttrMap.containsKey(ServiceAttr.METHOD)){
			processMethodName=(String)urlAttrMap.get(ServiceAttr.METHOD);
		}
		//取得function Id
		String functionId=null;
		if(urlAttrMap.containsKey(ServiceAttr.FUNCTION)){
			functionId=(String)urlAttrMap.get(ServiceAttr.FUNCTION);
		}
		//成功后轉向頁面
		String succView=null;
		if(urlAttrMap.containsKey(ServiceAttr.RESULT)){
			succView=(String)urlAttrMap.get(ServiceAttr.RESULT);
		}
		//成功確認頁面返回url
		String backUrlView=null;
		if(urlAttrMap.containsKey(ServiceAttr.BACK_URL)){
			backUrlView=(String)urlAttrMap.get(ServiceAttr.BACK_URL);
		}
		//出錯后轉向頁面
		String errorView=null;
		if(urlAttrMap.containsKey(ServiceAttr.ERROR)){
			errorView=(String)urlAttrMap.get(ServiceAttr.ERROR);
		}
		//back方式直接轉向
		if(backFlag!=null&&ServiceAttr.INIT_VALUE.equals(backFlag)){
			doBackward(command);
			return showForm(request, errors, succView);
		}
		//initFlag value
		if(urlAttrMap.containsKey(ServiceAttr.INIT)){
			initFlag=(String)urlAttrMap.get(ServiceAttr.INIT);
		}
		//initFlag="1"-->初始化command物件
		if(ServiceAttr.INIT_VALUE.equals(initFlag) || ServiceAttr.INIT_VALUE.equals(request.getParameter(INIT_FLAG))){
			initCommand(command);
		}

		//forward類型的method直接轉向
		if(ServiceAttr.METHOD_FORWARD.equals(processMethodName)){
			return showForm(request, errors, succView);
		}

		//修正 checkBox 的 submit value
		this.checkBoxAttrClearFix(request, command);

		if(processMethodName!=null){
			if(this.LOG.isDebugEnabled()){
				this.LOG.debug("Found process method:["+processMethodName+"] start do!");
			}
			try{
				BaseCommand baseCommand = (BaseCommand)command;
				baseCommand.setFuncId(functionId);
				//執行對應方法
				doProcessMethod(processMethodName,request,response,command,errors);
				//寫入 command LOG
				this.writeCommandLog(baseCommand);

			}catch(InvocationTargetException e){
				if(e.getTargetException() instanceof UtilException){
					request.setAttribute("_error",new ApplicationException((UtilException)e.getTargetException()));
					request.setAttribute("_function",functionId);
					throw (UtilException)e.getTargetException();
				}if(e.getTargetException() instanceof BusinessException){
					//處理BusinessException
					setMessageRedirect(request,errorView);//定義返回路徑
					request.setAttribute("_error",e.getTargetException());
					request.setAttribute("_function",functionId);
					return showForm(request, errors, Constants.MSG_URL);
				}if(e.getTargetException() instanceof ApplicationException){//處理ApplicationException
					setMessageRedirect(request,this.exceptionView);//定義返回路徑
					request.setAttribute("_error",e.getTargetException());
					request.setAttribute("_function",functionId);
					throw (ApplicationException)e.getTargetException();
				}if(e.getTargetException() instanceof PopupException){//處理PopupException
					request.setAttribute("_error",e.getTargetException());
					request.setAttribute("_function",functionId);
					request.setAttribute(Constants.EXCEPTIONS, e.getTargetException());
					return showForm(request, errors, this.exceptionView);
				}

				setMessageRedirect(request,this.exceptionView);//定義返回路徑
				request.setAttribute("_function",functionId);
				this.LOG.error("Method:["+processMethodName+"] throw exception:"
						+e.getTargetException().getMessage(),e.getTargetException());
				//setMessageRedirect(request,getErrorViewNameFormRequest(request));
				request.setAttribute(Constants.EXCEPTIONS, e.getTargetException());
				throw e;
			}
		}

		String viewName=succView;
		//特殊返回視圖處理
		if(this.returnView!=null){
			viewName=this.returnView;
			this.returnView=null;
		}

		if(RETURN_NULL.equals(this.returnView)){
			return null;
		}
		//設置backurl
		setMessageRedirect(request,backUrlView);
		//設置function Id
		request.setAttribute("_function",functionId);

		return showForm(request, errors, viewName);
	}

	private void setMessageRedirect(HttpServletRequest request, String formView) {
		request.setAttribute(Constants.MESSAGE_REDIRECT, formView);
	}

	/*
	 * 功能：根據方法名反射方法
	 * @param String processMethodName 需要反射方法名
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @param Object command
	 * @param BindException errors
	 * @param Messages messages
	 * @throws InvocationTargetException
	 */
	private void doProcessMethod(String processMethodName, HttpServletRequest request,HttpServletResponse response,Object command, BindException errors)
		throws InvocationTargetException{
		Method[] methods=getClass().getMethods();
		for(int i=0;i<methods.length;i++){
			Method method=methods[i];
			if(method.getName().equals(processMethodName)){
				if(this.LOG.isDebugEnabled()){
					this.LOG.debug("found method:["+processMethodName+"]");
				}
				Class[] types= method.getParameterTypes();
				Object[] parameters=new Object[types.length];
				for(int j=0;j<types.length;j++){
					if(types[j].equals(HttpServletRequest.class)){
						parameters[j]=request;
						continue;
					}
					if(types[j].equals(HttpServletResponse.class)){
						parameters[j]=response;
						continue;
					}
					if(types[j].equals(BindException.class)){
						parameters[j]=errors;
						continue;
					}
					if(types[j].equals(Command.class)){
						parameters[j]=command;
						continue;
					}
					if(types[j].equals(getCommandClass())){
						parameters[j]=command;
						continue;
					}
				}
				try {
					method.invoke(this, parameters);
					return;
				} catch (IllegalAccessException e) {
					if(this.LOG.isDebugEnabled()){
						this.LOG.debug("method:["+processMethodName+"] illegal access:"+e.getMessage());
					}
					//must be some code error,don't hanlder it
					throw new RuntimeException("method:["+processMethodName+"] illegal access:"+e.getMessage(),e);
				} catch (IllegalArgumentException e) {
					if(this.LOG.isDebugEnabled()){
						this.LOG.debug("method:["+processMethodName+"] illegal argument:"+e.getMessage());
					}
					//must be some code error,don't hanlder it
					throw new RuntimeException("method:["+processMethodName+"] illegal argument:"+e.getMessage(),e);
				}
			}
		}
		if(this.LOG.isDebugEnabled()){
			this.LOG.debug("method:["+processMethodName+"] not found!");
		}
		throw new RuntimeException("method:["+processMethodName+"] not found!");
	}

	/**
	 * 修正原本有值的 checkbox 全不勾選後, 參數不會被清除的問題<br>
	 * 注：<br>
	 * 未勾選的 checkbox ，在 request 中，並不會帶此參數 (不會有 checkBox Parameter = null),
	 * 所以如果某page第一次sumbit時 checkbox 有帶值, 第二次 submit 時清空 checkbox, 則第一次
	 * 的值並不會被清掉，因為 request 中無此 Parameter, spring 會誤判頁面無此參數，故不會異動model 中checkbox參數值
	 * @param request
	 * @param command
	 */
	private void checkBoxAttrClearFix(HttpServletRequest request, Object command){
		BaseCommand baseCommand = (BaseCommand)command;
		if(baseCommand.getCheckBoxRegister()==null || "".equals(baseCommand.getCheckBoxRegister().trim())){
			return;
		}
		//取得 request 中的參數 MAP
		Map<String, String[]> map = request.getParameterMap();

		for (String checkBoxName : baseCommand.getCheckBoxRegister().split(",")) {

			//request ˋ中已經有此參數時跳過
			if(map.containsKey(checkBoxName)){
				continue;
			}
			//組出 Method 名稱
			if(checkBoxName==null || "".equals(checkBoxName.trim()))continue;
			String methodName = "set" + checkBoxName.substring(0,1).toUpperCase();
			if(checkBoxName.length()>1)methodName+=checkBoxName.substring(1);

			//取得 Method
			Method setMethod = BeanUtils.findDeclaredMethod(command.getClass(), methodName, new Class[]{String.class});
			if(setMethod ==null) continue;

			//清空該參數值
			try {
				this.LOG.debug("clear properties:[" + methodName +"]");
				setMethod.invoke(command, new Object[]{""});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
			return createCommand();
	}


	//支持GET提交方式
	@Override
	protected boolean isFormSubmission(HttpServletRequest request) {
		//return "POST".equals(request.getMethod());
		return true;
	}

	/**
	 * 預留request封裝command
	 * @param request
	 * @param command
	 * @return
	 */
	protected Object composeCommand(HttpServletRequest request,Object command){
		return command;
	}

	public void setUrlMapping(AutoWireHandlerMapping urlMapping) {
		this.urlMapping = urlMapping;
	}

	protected void doBackward(Object command){
		//
	}

	/**
	 * 將 command 寫入 LOG
	 * @param command
	 */
	private void writeCommandLog(BaseCommand command){
		try {
			if(StringUtil.notEmpty(SystemLoader.getSysProperties("CommandLog"))){
				//String content = new BeanUtil().showContent(command.getFuncId(), command);
				String content = "";
				String fileName = DateUtil.getCurrentDateTime("yyyyMMdd_hhmmsss") + "(" + command.getFuncId() + ").txt";
				new FileUtil().writeToFile(content.getBytes("UTF-8"), SystemLoader.getSysProperties("CommandLog"), fileName);
			}
		} catch (Exception e) {
			this.LOG.error("writeCommandLog error!");
			this.LOG.error(StringUtil.getExceptionStackTrace(e));
		}
	}
}
