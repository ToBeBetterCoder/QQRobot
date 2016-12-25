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
		alert: function(info) {
			$(tipsHtml).appendTo(parentDom)
				.addClass("success")
				.html(info)
				.fadeToggle(speedTime, function () {
					$("." + tipsClass + ":hidden").remove();
				});
		},
		// 警告级别
		alertW: function(info) {
			$(tipsHtml).appendTo(parentDom)
			.addClass("warning")
			.html(info)
			.fadeToggle(speedTime, function () {
				$("." + tipsClass + ":hidden").remove();
			});
		},
		// 错误级别
		alertE: function(info) {
			$(tipsHtml).appendTo(parentDom)
			.addClass("error")
			.html(info)
			.fadeToggle(speedTime, function () {
				$("." + tipsClass + ":hidden").remove();
			});
		}
	});
})(jQuery);