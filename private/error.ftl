<html>
<head>
	<title>Microblog</title>
</head>
<body>

<h1>An error has occurred when processing your request</h1>

<p>
	<#if _errors.message?>
		<font color="#cc0000">
		${_errors.message}
		</font>
	</#if>
</p>

</body>
</html>