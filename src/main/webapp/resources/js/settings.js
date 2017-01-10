settings = (function() {
	var _contextPath = "";
	// 自动回复列表
	var _replyList = {
			add: [], 
			del: []
		};
	var _submitList = function() {
		// 清空原来数据
		_replyList.add = [];
		_replyList.del = [];
		$('input[name="my-select-checkbox"]').each(function() {
			var afterCheck = $(this).prop("checked");
			var beforeCheck = eval($(this).attr("select"));
			var reply = {
		  			name: $(this).attr("content"),
		  			type: parseInt($(this).attr("kind"))
		  		};
			// del
			if (beforeCheck == true && afterCheck == false) {
				_replyList.del.push(reply);
			}
			// add
			if (beforeCheck == false && afterCheck == true) {
				_replyList.add.push(reply);
			}
		});
	};
	var _pluginInit = function() {
		// iCheck init
		$('.panel-group input:checkbox').iCheck({
			checkboxClass : 'icheckbox_minimal-blue',
			radioClass : 'iradio_minimal-blue',
			increaseArea : '20%' // optional
		});
		// bootstrapSwitch
		$("[name='my-switch-checkbox']").bootstrapSwitch();
	};
	var _listOption = function() {
		// tablist change
		$("#headerType label").on("click", function() {
			var index = $(this).index();
			$(".panel-group").eq(index).show().siblings(".panel-group").hide();
		});
		// list mark
		$(".panel-title a").on("click", function() {
			$(this).find(".glyphicon").toggleClass("glyphicon-chevron-down");
		});
		// select all
		$(".panel .panel-title input").on("ifChecked", function() {
			$(this).parentsUntil(".panel").parent().find(".list-group-item input").iCheck('check');
		});
		// cancle all
		$(".panel .panel-title input").on("ifUnchecked", function() {
			$(this).parentsUntil(".panel").parent().find(".list-group-item input").iCheck('uncheck');
		});
	};
	var _listSubmitServer = function() {
		$("#submitBtn").on("click", function() {
			_submitList();
			if (_replyList.add.length == 0 && _replyList.del.length == 0) {
				$.alertW("未做任何更改哟~");
				return;
			}
			// 以前，一直以为在SpringMVC环境中，@RequestBody接收的是一个Json对象，一直在调试代码都没有成功，后来发现，其实 @RequestBody接收的是一个Json对象的字符串，而不是一个Json对象。然而在ajax请求往往传的都是Json对象，后来发现用 JSON.stringify(data)的方式就能将对象变成字符串。同时ajax请求的时候也要指定dataType: "json",contentType:"application/json" 这样就可以轻易的将一个对象或者List传到Java端
			var source = {
					url: "submitList",
					data: _replyList
				};
			$.callServiceAsJson(source, {
				success: function(response) {
					// {"success":true,"code":0,"data":{"info":"设置成功"}}
					if (response.code == 0) {
						$("#submitBtn").off("click");
						$("#submitBtn").prop("disabled", true);
						$.alert(response.data.info, function() {
							// 提交后刷新页面 不然再次提交会有数据错误
							location.reload();
						});
					} else if (response.code == -1) {
						$.alertE(response.error);
					}
				}
			});
		});
	};
	var _replyAllServer = function(flag) {
		var source = {
				url: "setReplyAll",
				data: {replyAll: flag ? "off" : "on"}
			};
		$.callServiceAsJson(source, {
			success: function(response) {
				if (response.code == 0) {
					flag ? $.alert(response.data.info) : $.alert("请自定义回复列表");
					var listDom = $("#friendsList, #headerType, #submitBtn");
					flag ? listDom.slideUp("fast") : listDom.slideDown("fast");
				} else if (response.code == -1) {
					$.alertE(response.error);
				}
			}
		});
	};
	var _autoReplyServer = function(flag) {
		var source = {
				url: "setAutoReply",
				data: {autoReply: flag ? "on" : "off"}
			};
		$.callServiceAsJson(source, {
			success: function(response) {
				if (response.code == 0) {
					$.alert(response.data.info);
				} else if (response.code == -1) {
					$.alertE(response.error);
				}
			}
		});
	};
	var _replySet = function() {
		$('#replyAll').on('switchChange.bootstrapSwitch', function(event, state) {
			_replyAllServer(state);
		});
		$('#autoReply').on('switchChange.bootstrapSwitch', function(event, state) {
			_autoReplyServer(state);
		});
	};
	var _robotQuit = function() {
		$("#robotQuitBtn").on("click", function() {
			var source = {
					url: "robotQuit"
				};
			$.callServiceAsJsonGet(source, {
				success: function(response) {
					if (response.code == 0) {
						$("#robotQuit").off("click");
						$("#robotQuit").prop("disabled", true);
						$("#loading").hide();
						$("#contentBody").hide();
						$("#reLogin").show();
					} else if (response.code == -1) {
						$.alertE(response.error);
					}
				}
			});
		});
	};
	var _reLogin = function() {
		$("#reLoginBtn").on("click", function() {
			location.reload();
		});
	};
	var _pageLoad = function() {
		$("#loading").hide();
		$("#contentBody").show();
	};
	var _init = function(params) {
		// 上下文设置
		_contextPath = params.contextPath;
		// 第三方插件初始化
		_pluginInit();
		// 联系人列表相关UI操作
		_listOption();
		// 自动回复、全部回复开关
		_replySet();
		// 提交联系人更改
		_listSubmitServer();
		// 退出
		_robotQuit();
		// 重新登录
		_reLogin();
		// 页面加载显示
		_pageLoad();
	};
	return {
			init: _init
		};
})();