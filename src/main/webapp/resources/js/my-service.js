/**
 * 
 * 封装jQuery ajax
 */
(function($) {
	var defaultOptions = {
			dataType : "json",
			type : "GET",
			contentType :"application/json;charset=UTF-8",
			isForm: false,
			async: true,
			cache:false
		};
	var _callService = function(source, callback, options) {
		if (!source.url) {
			$.alertW("请求地址不能为空！");
			return;
		}
		// 没有success回调函数，默认为同步请求
		if (!callback || !$.isFunction(callback.success)) {
			options.async = false;
		}
		var newOptions = $.extend({}, defaultOptions, options);
		var reqOptions = $.extend(
				{},
				newOptions,
				{url: source.url, data: newOptions.isForm ? (source.data || {}) : (JSON.stringify(source.data) || {})},
				{contentType: newOptions.isForm ? "application/x-www-form-urlencoded;charset=UTF-8" : newOptions.contentType},
				{beforeSend: function () {
				    if (callback && $.isFunction(callback.before)) {
				    	callback.before();
				    }
				}}
			);
		var response;
		$.ajax(reqOptions).done(
			function (data, textStatus, jqXHR) {
			    // session过期
				if (data.success && data.code == 1) {
					$.alertW(data.error, function () {
						location.reload();
					});
				}
				if (callback && $.isFunction(callback.success)) {
			    	callback.success(data);
			    } else {
			    	// 同步
			    	response = data;
			    }
			}
		).fail(
			function (XMLHttpRequest, textStatus, errorThrown) {
			    // 通常 textStatus 和 errorThrown 之中
			    // 只有一个会包含信息
				if (callback && $.isFunction(callback.error)) {
			    	callback.error();
			    }
				$.alertE(errorThrown || textStatus);
			}
		).always(
			function () {
				if (callback && $.isFunction(callback.always)) {
			    	callback.always();
			    }
			}	
		);
		if (!reqOptions.async) {
			return response;
		}
	};
	$.extend({
		callServiceAsJson: function (source, callback) {
			return _callService(source, callback, {type: "POST"});
		},
		callServiceAsJsonGet: function (source, callback) {
			return _callService(source, callback);
		},
		callServiceAsHtml: function (source, callback) {
			return _callService(source, callback, {dataType: "html", type: "POST"});
		},
		callServiceAsHtmlGet: function (source, callback) {
			return _callService(source, callback, {dataType: "html"});
		},
		callServiceAsText: function (source, callback) {
			return _callService(source, callback, {dataType: "text", type: "POST"});
		},
		callServiceAsTextGet: function (source, callback) {
			return _callService(source, callback, {dataType: "text"});
		},
		callServiceAsJsonp: function (source, callback) {
			return _callService(source, callback, {dataType: "jsonp"});
		},
		callServiceAsJsonForm: function (source, callback) {
			return _callService(source, callback, {type: "POST", isForm: true});
		},
		callServiceAsJsonGetForm: function (source, callback) {
			return _callService(source, callback, {isForm: true});
		},
		callServiceAsHtmlForm: function (source, callback) {
			return _callService(source, callback, {dataType: "html", type: "POST", isForm: true});
		},
		callServiceAsHtmlGetForm: function (source, callback) {
			return _callService(source, callback, {dataType: "html", isForm: true});
		}
	});
})(jQuery);