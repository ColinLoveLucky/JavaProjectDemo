<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
   <title>Spring MVC表单处理(复选框)</title>
   <style>
.error {
    color: #ff0000;
}

.errorStyle {
    color: #000;
    background-color: #ffEEEE;
    border: 3px solid #ff0000;
    padding: 8px;
    margin: 16px;
}
</style>
</head>
<body>

<h2>用户信息 - </h2>
<form:form method="POST" action="/SpringMVC4/addUser">
  <form:errors path="*" cssClass="errorStyle" element="div" />
   <table>
      <tr>
         <td><form:label path="username">用户名：</form:label></td>
         <td><form:input path="username" /></td>
          <td><form:errors path="username" cssClass="error" /></td>
      </tr>
      <tr>
         <td><form:label path="password">密码：</form:label></td>
         <td><form:password path="password" /></td>
      </tr>  
      <tr>
         <td><form:label path="address">地址：</form:label></td>
         <td><form:textarea path="address" rows="5" cols="30" /></td>
      </tr>  
      <tr>
         <td><form:label path="receivePaper">订阅新闻？</form:label></td>
         <td><form:checkbox path="receivePaper" /></td>
      </tr> 
      <tr>
      <td>
         <form:checkboxes items="${webFrameworkList}" path="favoriteFrameworks" />
      </td>
      </tr>
      <tr>
      <td>
      	<form:radiobutton path="gender" value="M" label="男" />
		<form:radiobutton path="gender" value="F" label="女" />
      </td>
      </tr>
      <tr>
      <td>
      	<form:select path="country">
   		<form:option value="NONE" label="Select"/>
   		<form:options items="${countryList}" />
		</form:select>
      </td>
      </tr>
       <tr>
            <td><form:label path="skills">技术：</form:label></td>
            <td><form:select path="skills" items="${skillsList}"
                    multiple="true" /></td>
        </tr>
      <tr>
         <td colspan="2">
            <input type="submit" value="提交"/>
         </td>
      </tr>
   </table>  
</form:form>
</body>
</html>