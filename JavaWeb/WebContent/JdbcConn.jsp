<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="db" class="cc.openhome.DbBean"></jsp:useBean>
<c:set target="${db}" property="jdbcUrl" value="jdbc:mysql://172.16.36.45:3306/Test"/>
<c:set target="${db}" property="userName" value="root"/>
<c:set target="${db}" property="password" value="WangHua1986"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<c:choose>
<c:when test="${db.connectedOk}">
connect success!
</c:when>
<c:otherwise>connect failure!</c:otherwise>
</c:choose>
</body>
</html>