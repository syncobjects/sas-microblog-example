<html>
<head>
	<title>Microblog</title>
</head>
<body>

<form action="/login" method='post'>
<table>
<tr>
	<td>Username or email</td>
	<td><input type="text" name="username" value="${username!}"></td>
</tr>
<tr>
	<td>Password</td>
	<td><input type="password" name="password" value=""></td>
</tr>
<tr>
	<td></td>
	<td><input type="submit" value="Login"></td>
</tr>
<#if _errors.login??>
<tr>
	<td colspan=2><font color="#cc0000">${_errors.login}</font></td>
</tr>
</#if>
<tr>
	<td colspan=2>Not registered ? <a href="/signup/main">Sign up now!</a></td>
</tr>
</table>
</form>

</body>
</html>