/**
 * 
 * 自定义提示框
 */
(function($) {
	var tipsClass = "my-tips";
	var tipsHtml = '<div class="' + tipsClass + '"></div>';
	var parentDom = "body";
	var speedTime = 4000;
	
	$.extend({
		// 提示级别
		alert: function(info, callback) {
			$(tipsHtml).appendTo(parentDom)
				.addClass("success")
				.html(info)
				.fadeToggle(speedTime, function () {
					$("." + tipsClass + ":hidden").remove();
					if ($.isFunction(callback)) {
						callback();
					}
				});
		},
		// 警告级别
		alertW: function(info, callback) {
			$(tipsHtml).appendTo(parentDom)
			.addClass("warning")
			.html(info)
			.fadeToggle(speedTime, function () {
				$("." + tipsClass + ":hidden").remove();
				if ($.isFunction(callback)) {
					callback();
				}
			});
		},
		// 错误级别
		alertE: function(info, callback) {
			$(tipsHtml).appendTo(parentDom)
			.addClass("error")
			.html(info)
			.fadeToggle(speedTime, function () {
				$("." + tipsClass + ":hidden").remove();
				if ($.isFunction(callback)) {
					callback();
				}
			});
		}
	});
})(jQuery);