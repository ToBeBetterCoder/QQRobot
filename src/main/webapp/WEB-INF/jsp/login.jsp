<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html lang="zh-cn">
	<head>
		<title>扫码登录</title>
	    <%@include file="common/head.jsp"%>
	</head>
  	<body>
	    <img src="${imageCode}"/>
		${friendsViewMap}
		<br />
		${discussesViewMap}
		<br />
		${groupsViewMap}
		<br />
	    <%@include file="common/foot.jsp"%>
  	</body>
</html>