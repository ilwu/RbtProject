// console
if (typeof console == "undefined") {
	this.console = {
		log : function(msg) {
		}
	};
}
// startsWith
if (typeof String.prototype.startsWith != 'function') {
	String.prototype.startsWith = function(str) {
		return this.indexOf(str) == 0;
	};
}
// endsWith
if (typeof String.prototype.endsWith != 'function') {
	String.prototype.endsWith = function(str) {
		return this.slice(-str.length) == str;
	};
}

// 鎖住按鍵
function public_buttonLock(val) {
	$(":button").each(function() {
		if (val) {
			$(this).attr("disabled", "disabled");
		} else {
			$(this).removeAttr("disabled");
		}
	});
}

/**
 * _public_setCheckAll 使用的記錄物件
 */
var _checkAllSettingObj = new Object();
/**
 * 綁定全選事件
 * @param eventCheckboxID
 * @param targetCheckboxName
 */
function _public_setCheckAll(eventCheckboxID, targetCheckboxName) {

	//取得事件觸發 checkbox
	var eventCheckbox = $("input[id=" + eventCheckboxID + "]:checkbox");
	//未找到時返回
	if (eventCheckbox.length < 1)
		return;
	//紀錄關聯的標的物件名稱
	_checkAllSettingObj[eventCheckboxID] = targetCheckboxName;

	//綁定全選事件
	eventCheckbox.click(function() {
		//取得ID
		var id = $(this).attr("id");
		//取得事件觸發 checkbox 目前的選取狀態
		var isChecked = $('input[id=' + id + ']:checked').length > 0;
		//jquery 版本
		var jVersion = $().jquery;
		var jVersionAry = jVersion.split(".");
		jVersion = jVersionAry[0] + ".";
		for(var i=1; i<jVersionAry.length;i++){
			jVersion += padLeft(jVersionAry[i],2);
		}
		//set 全選或全不選
		jVersion = Number(jVersion);
		$("input[name='" + _checkAllSettingObj[id] + "']:checkbox").each(function() {
			if (jVersion >= 1.06) {
				$(this).prop('checked', isChecked);
			} else {
				$(this).attr('checked', isChecked);
			}
		});
	});
}

/**
 * 在submit 之前，插入 function
 */
function _public_formSubmitInterceptor(interceptor) {
	if (isFunction(interceptor)) {
		for ( var i = 0; i < document.forms.length; i++) {
			var _sumbitFrom = document.forms[i];
			if (_sumbitFrom != null && _sumbitFrom != 'undefined') {
				_sumbitFrom.oldSubmit = _sumbitFrom.submit;
				_sumbitFrom.submit = function() {
					if (typeof (interceptor) != 'undefined' && typeof (interceptor) == 'function') {
						interceptor();
					}
					_sumbitFrom.oldSubmit();
				};
			}
		}
	}
}