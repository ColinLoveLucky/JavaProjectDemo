<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="login.action" method="post">
<table align="center">
<caption>User Login</caption>
<tr>
<td>
name:<input type="text" name="username" />
</td>
</tr>
<tr>
<td>
password:<input type="password" name="password" />
</td>
</tr>
<tr>
<td>
<input type="submit" value="login">
<input type="reset" value="reset" />
</td>
</tr>
</table>
</form>
</body>
</html>