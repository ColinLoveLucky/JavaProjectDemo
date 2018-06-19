<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="guestbook" class="cc.openhome.GuestBookBean"></jsp:useBean>
<c:if test="${param.msg!=null}">
	<jsp:useBean id="newMessage" class="cc.openhome.Message">
	</jsp:useBean>
	<jsp:setProperty name="newMessage" property="*" />
	<c:set target="${guestbook}" property="message" value="${newMessage}">
	</c:set>
</c:if>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<table style="text-align:left;width:100%;" border="0" cellpadding="2" cellspacing="2">
<tbody>
<c:forEach var="message" items="${guestbook.messages}">
<tr>
<td>
${message.name}
</td>
<td>
${message.email}
</td>
<td>
${message.msg}
</td>
</tr>
</c:forEach>
</tbody>
</table>

</body>
