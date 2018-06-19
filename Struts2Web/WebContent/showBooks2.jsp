<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<table border="1" width="360">
<caption>Author books</caption>
<s:iterator value="books" status="index">
	<s:if test="#index.odd ==true">
		<tr style="background-color:#cccccc">
	</s:if>
	<s:else>
		<tr>
	</s:else>
			<td>
			name:
			</td>
			<td>
			<s:property />
			</td>
	</tr>
</s:iterator>
</table>
</body>
</html>