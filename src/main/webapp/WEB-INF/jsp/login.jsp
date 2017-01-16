<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn">
	<head>
		<title>QQ机器人</title>
		<meta http-equiv="refresh" content="3">
	    <%@include file="common/head.jsp"%>
	    <style>
	    	.main-state-info {
	    		font-size: 20px;
	    		color: #444;
	    	}
	    </style>
	</head>
  	<body>
  		<div class="container">
  			<div class="row text-center">
  				<br />
  				<br />
  				<br />
	      		<img src="${empty imageCode ? '${pageContext.request.contextPath}/resources/image/arale.jpg' : imageCode}" alt="二维码" class="img-responsive img-thumbnail" />
  				<br />
  				<br />
	        	<h3 class="main-state-info">
	        		${empty isCodeScanned ? (empty imageCode ? "获取二维码异常" : "扫描二维码登录") : (isCodeScanned ? "登录中，请稍等..." : "扫描二维码登录")}
	        	</h3>
	        	<p class="text-muted small">
	        		登录后QQ机器人将自动回复
	        		<br />
	        		再次访问此页面可设置回复选项
	        	</p>
	        	<img class="not-show" alt="" src="${pageContext.request.contextPath}/resources/image/arale.jpg" />
			</div>
  		</div>
	    <%@include file="common/foot.jsp"%>
  	</body>
</html>
