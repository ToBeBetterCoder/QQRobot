settings = (function() {
	var _contextPath = "";
	// 自动回复列表
	var _replyList = {
			add: [], 
			del: []
		};
	var _submitList = function() {
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
	var _init = function(params) {
		_contextPath = params.contextPath;
		// iCheck init
		$('.panel-group input:checkbox').iCheck({
			checkboxClass : 'icheckbox_minimal-blue',
			radioClass : 'iradio_minimal-blue',
			increaseArea : '20%' // optional
		});
		// bootstrapSwitch
		$("[name='my-switch-checkbox']").bootstrapSwitch();
		// tablist change
		$("#headerType label").on("click", function() {
			var index = $(this).index();
			$(".panel-group").eq(index).show().siblings(".panel-group").hide();
		});
		// list mark
		$(".panel-title a").on("click", function() {
			$(this).find(".glyphicon").toggleClass("glyphicon-chevron-down");
		});
		// checkbox
		/*$('input[name="my-select-checkbox"]').on('ifChecked', function(event){
			console.log($(this).attr("content"));
		  	console.log($(this).attr("kind"));
		  	console.log("add");
		});
		
		$('input[name="my-select-checkbox"]').on('ifUnchecked', function(event){
			console.log($(this).attr("content"));
			console.log($(this).attr("kind"));
			console.log("del");
		});*/
		$(".panel .panel-title input").on("ifChecked", function() {
			$(this).parentsUntil(".panel").parent().find(".list-group-item input").iCheck('check');
		});
		$(".panel .panel-title input").on("ifUnchecked", function() {
			$(this).parentsUntil(".panel").parent().find(".list-group-item input").iCheck('uncheck');
		});
		$("#submitBtn").on("click", function() {
			_submitList();
			if (_replyList.add.length == 0 && _replyList.del.length == 0) {
				$.alertW("未做任何更改哟~");
				return;
			}
			// TODO 提交后刷新页面 不然再次提交会有数据错误
			// 以前，一直以为在SpringMVC环境中，@RequestBody接收的是一个Json对象，一直在调试代码都没有成功，后来发现，其实 @RequestBody接收的是一个Json对象的字符串，而不是一个Json对象。然而在ajax请求往往传的都是Json对象，后来发现用 JSON.stringify(data)的方式就能将对象变成字符串。同时ajax请求的时候也要指定dataType: "json",contentType:"application/json" 这样就可以轻易的将一个对象或者List传到Java端
			var source = {
					url: "submitList",
					data: _replyList
				}
			$.callServiceAsJson(source, {
				success: function(data) {
					$.alert("success");
					console.log(data);
				}
			});
		});
	};
	return {
			init: _init,
			submitList: _submitList
		};
})();