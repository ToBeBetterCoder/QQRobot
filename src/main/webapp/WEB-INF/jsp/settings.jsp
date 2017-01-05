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
								<input id="autoReply" data-on-color="info" data-size="small" type="checkbox" name="my-switch-checkbox" ${empty autoReply ? "" : (autoReply.isAutoReply ? "checked" : "")}/>
						    </span>
					  	</li>
						<li class="list-group-item">
						  	<span class="badge">
								<input id="replyAll" data-on-color="info" data-size="small" type="checkbox" name="my-switch-checkbox" ${empty autoReply ? "" : (autoReply.isSpecial ? "" : "checked")}/>
						    </span>
						             全部回复
						</li>
					</ul>
					
					<!-- 联系人 群组 -->
					<div id="headerType" class="btn-group btn-group-justified ${empty autoReply ? "" : (autoReply.isSpecial ? "" : "not-show")}" data-toggle="buttons">
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
				<div id="friendsList" class="${empty autoReply ? "" : (autoReply.isSpecial ? "" : "not-show")}">
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
												  	<input select="${friend.select == "Y" ? true : false}" kind="0" content="${empty friend.markname ? friend.nick : friend.markname}" name="my-select-checkbox" type="checkbox" ${friend.select == "Y" ? "checked" : ""}>
											  	</label>
										  	</li>
										</c:forEach>
									</ul>
							    </div>	
							</div>
						</c:forEach>
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
											  	<input select="${discuss.select == "Y" ? true : false}" kind="1" content="${discuss.name}" name="my-select-checkbox" type="checkbox" ${discuss.select == "Y" ? "checked" : ""}>
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
											  	<input select="${group.select == "Y" ? true : false}" kind="2" content="${group.name}" name="my-select-checkbox" type="checkbox" ${group.select == "Y" ? "checked" : ""}>
										  	</label>
										</li>
									</c:forEach>
								</ul>
						    </div>
					  	</div>
					</div>
					<button id="submitBtn" type="button" class="btn btn-block btn-info">提交</button>
				</div>
			</div>
		</div>
		
    	<%@include file="common/foot.jsp"%>
    	<script src="${pageContext.request.contextPath}/resources/js/icheck.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap-switch.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/my-tips.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/settings.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/my-service.js"></script>
		<script>
			$(function() {
				var contextPath = "${pageContext.request.contextPath}";
				var params = {
						contextPath: contextPath
					};
				settings.init(params);
			});

			/* 
			$('input[name="my-switch-checkbox"]').bootstrapSwitch('state', false);

			 */
			/*
			
			$('input').iCheck('check');
			$('input').iCheck('uncheck');*/
		</script>
  	</body>
</html>
