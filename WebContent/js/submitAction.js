/**
 * com.rbt by allen
 */

/**
 *
 */
function SubmitAction() {
	this.url = "";
	// call class
	this.actionClass = "";
	// call method name
	this.actionName = "";
	// bean class
	this.beanClass = "";
	// 要帶到後端的元素區塊 ID
	this.submitFormID = "";
	// 是否初始化 bean
	this.initBean = "";

	// submit!
	this.submit = _submitAction_Submit;
	// ajax post!
	this.ajaxPost = _submitAction_ajaxPost;
	// ajax 取得其他頁
	this.getPageContent = _submitAction_getPageContent;
	// popup
	this.popup = _submitAction_popup;
}

//========================================================================================
//Submit
//========================================================================================
/**
 * @param targetPage
 * @returns {_submitAction_Submit}
 */
function _submitAction_Submit(targetPage) {

	if(targetPage==null || targetPage==""){
		alert("系統錯誤 submitAction.submit(targetPage) 未傳入目標頁!");
		return;
	}

	if(this.url == ''){
		this.url = "../../SubmitAction";
	}

	var submitForm = $("#" + this.submitFormID);
	if(submitForm.length==0){
		alert('傳入 submitFormID 錯誤! :['+this.submitFormID+']');
		return;
	};

	//radio & checkbox 未選擇時，清空 bean 中該參數值
	_submitAction_ProcRegister(submitForm);

	//操作參數設定
	_submitAction_AppendAttr(submitForm, "_actionClass", this.actionClass);
	_submitAction_AppendAttr(submitForm, "_actionName", this.actionName);
	_submitAction_AppendAttr(submitForm, "_beanClass", this.beanClass);
	_submitAction_AppendAttr(submitForm, "_targetPage", targetPage);
	_submitAction_AppendAttr(submitForm, "_initBean", this.initBean);

	submitForm.attr('method','post');
	submitForm.attr('action', this.url);

	submitForm.submit();
};

//========================================================================================
//Ajax
//========================================================================================
/**
 * @param callback callback function
 * @returns {_submitAction_ajaxPost}
 */
function _submitAction_ajaxPost(callback){

	if(this.url == ''){
		this.url = "../../AjexPost";
	}

	var submitForm = $("#" + this.submitFormID);
	if(submitForm.length==0){
		alert('傳入 submitFormID 錯誤! :['+this.submitFormID+']');
		return;
	};

	//操作參數設定
	_submitAction_AppendAttr(submitForm, "_actionClass", this.actionClass);
	_submitAction_AppendAttr(submitForm, "_actionName", this.actionName);
	_submitAction_AppendAttr(submitForm, "_beanClass", this.beanClass);
	_submitAction_AppendAttr(submitForm, "_postType", "ajaxPost");

	//radio & checkbox 未選擇時，清空 bean 中該參數值
	_submitAction_ProcRegister(submitForm);

	// let's select and cache all the fields
	$inputs = $("[name='form1']").find("input, select, textarea");
	// serialize the data in the form
	var serializedData = $inputs.serialize();

	//post
	$.post(this.url, serializedData)
	.done(function(data) {

		$("#_actionClass").remove();
		$("#_actionName").remove();
		$("#_beanClass").remove();
		$("#_postType").remove();

		if (data == null || data == '') {
			this.result = JSON.parse('{}');
			callback(this.result);
			return;
		}
		var jsonObj = JSON.parse(data);
		if ("error" == jsonObj._AjexPostResult) {
			alert(jsonObj._AjexPostErrorMessage);
			callback(this.result);
			return;
		}
		this.result = jsonObj;
		callback(this.result);
		return;
	});
}


/**
 * 以 ajex 觸發 servlet 後, 呼叫 windows.open 或 windows.showModalDialog
 * 結果 bean 放在 session 中的 "result" (避免污染 command)
 * @param targetPage 目標頁 URL
 * @param targetWindow 目標頁名稱 (window open 用)
 * @param isOpenWindow true:windows.open ,false:windows.showModalDialog
 * @param width 視窗寬
 * @param height 視窗高
 * @param resizable true|false
 * @param scroll true|false
 * @param status true|false
 * @param callback callback function
 * @returns {_submitAction_popup}
 */
function _submitAction_popup(targetPage, targetWindow, isOpenWindow ,width, height, resizable, scroll, status, callback){

	if(targetPage==null || targetPage==""){
		alert("系統錯誤 未傳入目標頁!");
		return;
	}

	if(this.url == ''){
		this.url = "../../AjexPost";
	}

	var submitForm = $("#" + this.submitFormID);
	if(submitForm.length==0){
		alert('傳入 submitFormID 錯誤! :['+this.submitFormID+']');
		return;
	};

	//操作參數設定
	_submitAction_AppendAttr(submitForm, "_actionClass", this.actionClass);
	_submitAction_AppendAttr(submitForm, "_actionName", this.actionName);
	_submitAction_AppendAttr(submitForm, "_beanClass", this.beanClass);
	_submitAction_AppendAttr(submitForm, "_postType", "popup");

	// let's select and cache all the fields
	$inputs = $("[name='form1']").find("input, select, textarea");
	// serialize the data in the form
	var serializedData = $inputs.serialize();
	// let's disable the inputs for the duration of the ajax request
	//$inputs.attr("disabled", "disabled");

	//post
	$.post(this.url, serializedData)
	.done(function(data) {

		$("#_actionClass").remove();
		$("#_actionName").remove();
		$("#_beanClass").remove();
		$("#_postType").remove();

		if(data!=null && data.indexOf('_AjexPostErrorMessage')>0){
			var jsonObj = JSON.parse(data);
			alert(jsonObj._AjexPostErrorMessage);
			callback(null);
			return;
		}


		var sFeatures = _submitAction_GetCenterOnScreen(isOpenWindow ,width, height, resizable, scroll, status);
		var result;
		if(isOpenWindow){
			result =  window.open(targetPage, targetWindow, sFeatures, true);
		}else{
			result =  window.showModalDialog(targetPage, "", sFeatures);
		}
		callback(result);
		return;
	});
}


/**
 * @param resultPage
 * @param callback callback function
 * @returns {_submitAction_getPageContent}
 */
function _submitAction_getPageContent(resultPage ,callback){

	if(resultPage==null || resultPage==""){
		alert("系統錯誤 submitAction.getPageContent(resultPage) 未指定結果頁!");
		return;
	}

	if(this.url == ''){
		this.url = "../../AjexPost";
	}

	var submitForm = $("#" + this.submitFormID);
	if(submitForm.length==0){
		alert('傳入 submitFormID 錯誤! :['+this.submitFormID+']');
		return;
	};

	//操作參數設定
	_submitAction_AppendAttr(submitForm, "_actionClass", this.actionClass);
	_submitAction_AppendAttr(submitForm, "_actionName", this.actionName);
	_submitAction_AppendAttr(submitForm, "_beanClass", this.beanClass);
	_submitAction_AppendAttr(submitForm, "_resultPage", resultPage);
	_submitAction_AppendAttr(submitForm, "_postType", "getPageContent");

	//radio & checkbox 未選擇時，清空 bean 中該參數值
	_submitAction_ProcRegister(submitForm);

	//post
	$.post(this.url, submitForm.serialize())
	.done(function(data) {

		$("#_actionClass").remove();
		$("#_actionName").remove();
		$("#_beanClass").remove();
		$("#_postType").remove();

		if (data == null || data == '') {
			alert("處理失敗，無回傳資料!");
			callback(null);
			return;
		}

		if(data.indexOf('_AjexPostErrorMessage')>0){
			var jsonObj = JSON.parse(data);
			alert(jsonObj._AjexPostErrorMessage);
			callback(null);
			return;
		}
		callback(data);
		return;
	});
}



//========================================================================================
// Other function
//========================================================================================
/**
 * @param submitForm
 */
function _submitAction_ProcRegister(submitForm) {
	var _fromAttrNames = [];

	$(':checkbox').each(function() {
		var _thisName = $(this).attr('name');
		if ($.inArray(_thisName, _fromAttrNames) == -1)
			_fromAttrNames.push(_thisName);
	});
	submitForm.append("<input type=\"hidden\" name=\"__checkBoxRegister\" value=\"" + _fromAttrNames + "\">");

	$(':radio').each(function() {
		var _thisName = $(this).attr('name');
		if ($.inArray(_thisName, _fromAttrNames) == -1)
			_fromAttrNames.push(_thisName);
	});
	submitForm.append("<input type=\"hidden\" name=\"__radioRegister\" value=\"" + _fromAttrNames + "\">");
}

/**
 * @param submitForm
 * @param attrName
 * @param value
 */
function _submitAction_AppendAttr(submitForm, attrName, value){
	if($('#' + name).length==0){
		submitForm.append('<input type="hidden" id="' + attrName + '" name="' + attrName + '" value="' + value + '">');
	}
}

/**
 * 兜組置中視窗設定參數字串
 * @param isOpenWindow [true:視窗為OpenWindow | false:show model ]
 * @param width	視窗寬度
 * @param height 視窗寬度
 * @param resizable 可異動視窗大小 [yes|no]
 * @param scroll 可捲動視窗 [yes|no]
 * @param status 狀態列 [yes|no]
 * @returns {___myObject1}
 */
function _submitAction_GetCenterOnScreen(isOpenWindow ,width, height, resizable, scroll, status) {

	var str = "";
	var left = parseInt((screen.availWidth/2) - (width/2));
	var top = parseInt((screen.availHeight/2) - (height/2));
	if(isOpenWindow){
		str += "width=" + width + "px, ";
		str += "height=" + height + "px, ";
		str += "left=" + left + "px, ";
		str += "top=" + top + "px, ";
		if(resizable!= null){
			str += "resizable=" + resizable + ", ";
		}
		if(scroll!= null){
			str += "scrollbars=" + scroll + ", ";
		}
		if(status!= null){
			str += "status=" + status + ", ";
		}
	}else{
		str += "dialogWidth: " + width + "px; ";
		str += "dialogHeight: " + height + "px; ";
		str += "dialogLeft: " + parseInt((screen.availWidth/2) - (width/2)) + "px; ";
		str += "dialogTop: " + parseInt((screen.availHeight/2) - (height/2)) + "px; ";
		if(resizable!= null){
			str += "resizable: " + resizable + "; ";
		}
		if(scroll!= null){
			"scroll: " + scroll + "; ";
		}
		if(status!= null){
			str += "status: " + status + "; ";
		}
	}
	return str;
}