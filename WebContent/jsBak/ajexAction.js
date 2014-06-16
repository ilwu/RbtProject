/**
 *
 */
function AjexAction() {
	this.url = "../../AjexPost";
	// call class
	this.actionClass = "";
	// call method name
	this.actionName = "";
	// bean class
	this.beanClass = "";
	// 要帶到後端的元素區塊 ID
	this.postAreaId = "";
	// 自訂 Data object
	this.cusData = null;
	// result
	this.result = null;

	// function post!
	this.post = _ajexAction_Post;
}

function _ajexAction_Post(callback) {
	// reset
	this.result = null;

	// Post Data Object
	var postData = null;

	// form2Json
	if (this.postAreaId == null || this.postAreaId == '') {
		postData = {};
	} else {
		postData = _ajexAction_form2Object(this.postAreaId);
	}

	// merge
	if (this.cusData != null) {
		$.extend(true, postData, this.cusData);
	}

	$.ajax({
		type : "post",
		url : this.url,
		data : {
			actionClass : this.actionClass,
			actionName : this.actionName,
			beanClass : this.beanClass,
			jsonData : JSON.stringify(postData)
		},
		// 請求成功後的回調函數有兩個參數
		success : function(data, textStatus) {
			if (data == null || data == '') {
				this.result = JSON.parse('{}');
				return;
			}
			var jsonObj = JSON.parse(data);
			if ("error" == jsonObj._AjexPostResult) {
				alert(jsonObj._AjexPostErrorMessage);
				return;
			}
			this.result = jsonObj;
			return;
		}
	}).done(callback);
};


/**
 * @returns
 */
function _ajexAction_Post1(callback) {
	// reset
	this.result = null;

	// Post Data Object
	var postData = null;

	// form2Json
	if (this.postAreaId == null || this.postAreaId == '') {
		postData = {};
	} else {
		postData = _ajexAction_form2Object(this.postAreaId);
	}

	// merge
	if (this.cusData != null) {
		$.extend(true, postData, this.cusData);
	}

	$.ajax({
		type : "post",
		url : this.url,
		data : {
			actionClass : this.actionClass,
			actionName : this.actionName,
			beanClass : this.beanClass,
			jsonData : JSON.stringify(postData)
		},
		// 請求成功後的回調函數有兩個參數
		success : function(data, textStatus) {
			if (data == null || data == '') {
				this.result = JSON.parse('{}');
				return;
			}
			var jsonObj = JSON.parse(data);
			if ("error" == jsonObj._AjexPostResult) {
				alert(jsonObj._AjexPostErrorMessage);
				return;
			}
			this.result = jsonObj;
			return;
		}
	}).done(callback);
};

/**
 * @param areaID
 * @returns
 */
function _ajexAction_form2Object(areaID) {

	var dataObject = {};

	// 取得 text checkbox textarea
	$("#" + areaID + " :text")
		.add("#" + areaID + " :password")
		.add("#" + areaID + " :checkbox")
		.add("#" + areaID + " textarea")
		.add("#" + areaID + " select")
		.add("#" + areaID + " :radio").each(function() {

		var name = $(this).attr("name");

		// 檢核 name 屬性是否存在
		if (name == null || name == '' || name == 'undefined') {
			return true; // (同 continue)
		}

		//屬性中含有. 的特別處理
		var nameAry = name.split(".");
		if (nameAry.length > 1) {
			var objStr = 'dataObject';
			for ( var i = 0; i < nameAry.length-1; i++) {
				objStr += "." + nameAry[i];
				var subObject = eval(objStr + ";");
				if (subObject == null) {
					eval(objStr + "= new Object();");
				}
			}
		}

		var value = "";
		// 依據元件類型處理
		if ($(this).is(':radio')) {
			if($(this).is(':checked')){
				value = $(this).val();
			}else{
				var subObject = eval('dataObject.' + name);
				if(subObject==null){
					value = null;
				}
			}
		} else if ($(this).is(':text') || $(this).is(':password') || $(this).is('select')) {
			value = $(this).val();
		} else if ($(this).is('textarea')) {
			value = $(this).html();
		}

		// checkbox 類型特別處理
		if ($(this).is(':checkbox')) {
			// 該名稱 checkbox 未建立時, 建一個空的 array, 避免傳到後端的值無此元素
			var obj = eval('dataObject.' + name) + '';
			if (obj == 'undefined') {
				eval('dataObject.' + name + "= new Array()");
			}
			// 有勾選的用 push 方式放入值
			if ($(this).is(':checked')) {
				eval('dataObject.' + name + ".push('" + $(this).val() + "')");
			}
			return true; // (同 continue)
		}

		eval('dataObject.' + name + "='" + value + "'");
		//alert('dataObject.' + name + "='" + value + "'");

	});
	return dataObject;
}

/**
 * 使用範例
 */
function _ajexAction_test() {

	var ajexAction = new AjexAction();
	ajexAction.actionClass = 'com.turbotech.disable.action.F08_10_299Action';
	ajexAction.actionName = 'getList';
	ajexAction.beanClass = 'com.turbotech.disable.actionbean.F08_10_299Bean';
	ajexAction.postAreaId = 'xForm';

	var cusJSONData = {
		"cus1" : "cus1",
		"cus2" : "cus2"
	};
	ajexAction.cusData = cusJSONData;

	// post & process callback function
	ajexAction.post(function(resultBean) {
		if (resultBean == null) {
			// process fail, do nothing
			// alert("return null");
			return;
		}
		alert(resultBean.account);
	});
}