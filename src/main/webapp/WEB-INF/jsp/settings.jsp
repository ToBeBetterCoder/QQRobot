<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn">
	<head>
		<title>设置</title>
    	<%@include file="common/head.jsp"%>
    	<link href="${pageContext.request.contextPath}/resources/css/blue.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-switch.min.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/resources/css/common.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/resources/css/settings.css" rel="stylesheet">
  	</head>
  	<body>
		<nav class="navbar navbar-fixed-top" role="navigation">
		  	<div class="container">
			  	<div class="row">
			    	<button id="logout" type="button" class="btn btn-danger btn-block btn-no-radius">退出登录</button>
					<br />
					<!-- 回复 -->
					<ul class="list-group list-with-switch">
						<li class="list-group-item">
						             自动回复
						    <span class="badge">
								<input id="autoReply" data-on-color="info" data-size="small" type="checkbox" name="my-checkbox" ${empty autoReply ? "" : (autoReply.isAutoReply ? "checked" : "")}/>
						    </span>
					  	</li>
						<li class="list-group-item">
						  	<span class="badge">
								<input id="replyAll" data-on-color="info" data-size="small" type="checkbox" name="my-checkbox" ${empty autoReply ? "" : (autoReply.isSpecial ? "" : "checked")}/>
						    </span>
						             全部回复
						</li>
					</ul>
					
					<!-- 联系人 群组 -->
					<div id="headerType" class="btn-group btn-group-justified" data-toggle="buttons">
						<label class="btn btn-no-border btn-info btn-no-radius active">
							<input type="radio" name="options" checked> 联系人
						</label>
						<label class="btn btn-no-border btn-no-radius btn-info">
						    <input type="radio" name="options"> 群组
						</label>
					</div>
				</div>
			</div>
		</nav>
	
		<div class="container">
			<div class="row">
				<!-- 好友列表 -->
				<div id="friendsList">
					<!-- 联系人 -->
					<div class="panel-group" role="tablist" aria-multiselectable="true">
						<c:forEach var="friendsGroup" items="${friendsViewMap.result}">
							<div class="panel panel-default">
								<div class="panel-heading" role="tab" id="heading_${friendsGroup.cateSort}">
							      	<h4 class="panel-title">
						      			<a data-toggle="collapse" data-parent="" href="#collapse_${friendsGroup.cateSort}" aria-expanded="true" aria-controls="collapse_${friendsGroup.cateSort}">
							        		<span class="glyphicon glyphicon-chevron-right"></span>&nbsp;&nbsp;${friendsGroup.cateName}
								        </a>
					      				<input type="checkbox">
							      	</h4>
								</div>
						    	<div id="collapse_${friendsGroup.cateSort}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading_${friendsGroup.cateSort}">
									<ul class="list-group">
										<c:forEach var="friend" items="${friendsGroup.list}">
											<li class="list-group-item">
											  	<label>
													<span>${empty friend.markname ? friend.nick : friend.markname}</span>
												  	<input type="checkbox" ${friend.select == "Y" ? "checked" : ""}>
											  	</label>
										  	</li>
										</c:forEach>
									</ul>
							    </div>	
							</div>
						</c:forEach>
					  <!-- <div class="panel panel-default">
					    <div class="panel-heading" role="tab" id="headingTwo">
					      <h4 class="panel-title">
					        <a data-toggle="collapse" data-parent="" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
					         	 <span class="glyphicon glyphicon-chevron-right"></span>&nbsp;&nbsp;同学
					        </a>
			      			<input id="cc" type="checkbox" checked style="">
					      </h4>
					    </div>
					    <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
					    	<ul class="list-group">
								<li class="list-group-item">
							  	<label>
								  	<span>王小二</span>
								  	<input type="checkbox" checked>
							  	</label>
							  </li>
							  <li class="list-group-item">
							  	<span>王小二</span>
							  	<input type="checkbox" checked>
							  </li>
							  <li class="list-group-item">
							  	<span>王小二</span>
							  	<input type="checkbox" checked>
							  </li>
							  <li class="list-group-item">
							  	<span>王小二</span>
							  	<input type="checkbox" checked>
							  </li>
							  <li class="list-group-item">
							  	<span>王小二</span>
							  	<input type="checkbox" checked>
							  </li>
							</ul>
					    </div>
					  </div>
					  <div class="panel panel-default">
					    <div class="panel-heading" role="tab" id="headingThree">
					      <h4 class="panel-title">
					        <a class="collapsed" data-toggle="collapse" data-parent="" href="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
					        	<span class="glyphicon glyphicon-chevron-right"></span>&nbsp;&nbsp;家人
					        </a>
					        <input id="cc" type="checkbox" checked style="">
					      </h4>
					    </div>
					    <div id="collapseThree" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingThree">
					    	<ul class="list-group">
								<li class="list-group-item">
							  	<span>王小二</span>
							  	<input type="checkbox" checked>
							  </li>
							  <li class="list-group-item">
							  	<span>王小二</span>
							  	<input type="checkbox" checked>
							  </li>
							  <li class="list-group-item">
							  	<span>王小二</span>
							  	<input type="checkbox" checked>
							  </li>
							  <li class="list-group-item">
							  	<span>王小二</span>
							  	<input type="checkbox" checked>
							  </li>
							  <li class="list-group-item">
							  	<span>王小二</span>
							  	<input type="checkbox" checked>
							  </li>
							</ul>
					    </div>
					  </div> -->
					</div>
					<!-- 群组 -->
					<div class="panel-group not-show" role="tablist" aria-multiselectable="true">
						<div class="panel panel-default">
						    <div class="panel-heading" role="tab" id="heading_discuss">
						      	<h4 class="panel-title">
					      			<a data-toggle="collapse" href="#collapse_discuss" aria-expanded="true" aria-controls="collapse_discuss">
							          	<span class="glyphicon glyphicon-chevron-right"></span>&nbsp;&nbsp;讨论组
							        </a>
					      			<input type="checkbox">
						      	</h4>
						    </div>
						    <div id="collapse_discuss" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading_discuss">
								<ul class="list-group">
									<c:forEach var="discuss" items="${discussesViewMap.result}">
										<li class="list-group-item">
											<label>
												<span>${discuss.name}</span>
											  	<input type="checkbox" ${discuss.select == "Y" ? "checked" : ""}>
										  	</label>
										</li>
									</c:forEach>
								</ul>
						    </div>
						</div>
						
					  	<div class="panel panel-default">
						    <div class="panel-heading" role="tab" id="heading_group">
							    <h4 class="panel-title">
							    	<a data-toggle="collapse" href="#collapse_group" aria-expanded="false" aria-controls="collapse_group">
							          	<span class="glyphicon glyphicon-chevron-right"></span>&nbsp;&nbsp;群
							        </a>
					      			<input type="checkbox">
							    </h4>
						    </div>
						    <div id="collapse_group" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading_group">
						    	<ul class="list-group">
							    	<c:forEach var="group" items="${groupsViewMap.result}">
										<li class="list-group-item">
											<label>
											  	<span>${group.name}</span>
											  	<input type="checkbox" ${group.select == "Y" ? "checked" : ""}>
										  	</label>
										</li>
									</c:forEach>
								</ul>
						    </div>
					  	</div>
					</div>
					<button type="button" class="btn btn-block btn-info">提交</button>
				</div>
			</div>
		</div>
		
    	<%@include file="common/foot.jsp"%>
    	<script src="${pageContext.request.contextPath}/resources/js/icheck.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap-switch.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/my-tips.js"></script>
		<script>
			$(function() {
				// iCheck init
				$('.panel-group input:checkbox').iCheck({
					checkboxClass : 'icheckbox_minimal-blue',
					radioClass : 'iradio_minimal-blue',
					increaseArea : '20%' // optional
				});
				// bootstrapSwitch
				$("[name='my-checkbox']").bootstrapSwitch();
				// tablist change
				$("#headerType label").on("click", function() {
					var index = $(this).index();
					$(".panel-group").eq(index).show().siblings(".panel-group").hide();
				});
				// list show
				$('#replyAll').on('switchChange.bootstrapSwitch', function(event, state) {
					if (state) {
						$.alert("全部回复已开启");
						$("#friendsList, #headerType").slideUp("fast");
					} else {
						$.alert("请自定义回复");
						$("#friendsList, #headerType").slideDown("fast");
					}
				});
				// list mark
				$(".panel-title a").on("click", function() {
					$(this).find(".glyphicon").toggleClass("glyphicon-chevron-down");
				});
				$('#autoReply').on('switchChange.bootstrapSwitch', function(event, state) {
					if (state) {
						$.alert("自动回复已开启");
					} else {
						$.alert("自动回复已关闭");
					}
				});
			});

			/* 
			$('input[name="my-checkbox"]').bootstrapSwitch('state', false);

			 */
			/*$('input').on('ifChecked', function(event){
			  alert("");
			});
			
			$('input').on('ifUnchecked', function(event){
			  alert("");
			});
			$('input').iCheck('check');
			$('input').iCheck('uncheck');*/
		</script>
  	</body>
</html>
