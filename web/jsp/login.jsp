<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
<title>Please Log In</title>
</head>
<body>
	<form method="POST" action="login" class="login">
		<img src="${pageContext.request.contextPath}/images/logo.png" /> <input type="submit"
			value="Please Login" />
		<div class="login-hint">Try logging in as <strong>api-demo@belladati.com</strong> with password <strong>apiDemo1</strong>.</div>
	</form>
</body>
</html>