/*
 * RbtUtil-rbtHinter by Allen Wu v1.00 (2013)
 *
 * 使用方法:
 * 1.	在 html 元素中, 建立屬性
 * 		hint : 提示文字內容
 * 		hintStyle : 提示文字class
 *
 * 2.	於文件 onload 呼叫 $(this).rbtHinter()
 * 3.	於文件 submit 或 檢核之前, 呼叫 $(this).rbtDeHinter(), 可將元素值返回空白
 * 4.	檢核失敗時, 可呼叫 $(this).rbtReHinter(), 將提示內容放回元素;
 */
(function($) {
	$.fn.rbtHinter = function() {
		$(":text").add(":password").add(" textarea").each(function() {
			var hint = $(this).attr('hint');
			if (hint == '')
				return;
			var hintStyle = $(this).attr('hintStyle');

			$(this).focus(function() {
				if ($(this).val() == hint) {
					$(this).val('');
					$(this).removeClass(hintStyle);
				}
			});
			$(this).blur(function() {
				if ($(this).val() == '') {
					$(this).val(hint);
					$(this).addClass(hintStyle);
				}
			});
			if ($(this).val() == '') {
				$(this).val(hint);
				$(this).addClass(hintStyle);
			}
		});
	};

	$.fn.rbtDeHinter = function() {
		$(":text").add(":password").add(" textarea").each(function() {
			var hint = $(this).attr('hint');
			if (hint == '')
				return;
			var hintStyle = $(this).attr('hintStyle');
			if ($(this).val() == hint) {
				$(this).val('');
				$(this).removeClass(hintStyle);
			}
		});
	};

	$.fn.rbtReHinter = function() {
		$(":text").add(":password").add(" textarea").each(function() {
			var hint = $(this).attr('hint');
			if (hint == '')
				return;
			$(this).blur();
		});
	};
})(jQuery);