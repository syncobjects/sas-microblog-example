<html>
<head>
	<title>Microblog</title>
</head>
<body>

<form action="/signup/signup" method="post">
<table>
<tr>
	<td>Username</td>
	<td>
		<input type="text" name="username" value="${username!}"/>
		<#if _errors.username??>
			<p style="color: #cc0000">${_errors.username}</p>
		</#if>
	</td>
</tr>
<tr>
	<td>Email</td>
	<td>
		<input type="text" name="email" value="${email!}"/>
		<#if _errors.email??>
			<p style="color: #cc0000">${_errors.email}</p>
		</#if>
	</td>
</tr>
<tr>
	<td>First Name</td>
	<td>
		<input type="text" name="firstName" value="${firstName!}"/>
		<#if _errors.firstName??>
			<p style="color: #cc0000">${_errors.firstName}</p>
		</#if>
	</td>
</tr>
<tr>
	<td>Last Name</td>
	<td>
		<input type="text" name="lastName" value="${lastName!}"/>
		<#if _errors.lastName??>
			<p style="color: #cc0000">${_errors.lastName}</p>
		</#if>
	</td>
</tr>
<tr>
	<td>Password</td>
	<td>
		<input type="password" name="password" value="${password!}"/>
		<#if _errors.password??>
			<p style="color: #cc0000">${_errors.password}</p>
		</#if>
	</td>
</tr>
<tr>
	<td></td>
	<td><input type="submit" value="Sign Up"/></td>
</tr>
</table>
</form>

</body>
</html>