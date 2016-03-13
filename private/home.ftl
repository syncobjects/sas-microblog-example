<html>
<head>
	<title>Microblog</title>
</head>
<body>

<h1>${user.firstName!} ${user.lastName!} Posts</h1>

<#if _session.USER.id != user.id>
<p><a href="/home">Home</a></p>
</#if>
<p>Want to logout? Click <a href="/logout">here</a>!</p>


<#if _session.USER.id == user.id>
<form action="/post/post">
<table>
<tr>
	<td>New Post</td>
</tr>
<tr>
	<td><textarea name="message" cols=60 rows=10></textarea></td>
</tr>
<tr>
	<td><input type="submit" value="Save"></td>
</tr>
</table>
</form>
</#if>


<table width="100%">
<tr>
	<td>Previous posts</td>
</tr>
<#if posts??>
	<#list posts?reverse as post>
	<tr>
		<td>
		
			<table cellspacing=0 cellpadding=10 border=1 width="100%">
			<tr>
				<td>${post.message}</td>
			</tr>
			<tr>
				<td>
					Likes: ${post.likes}! <a href="/post/like?id=${post.id}">Like!</a><br/>
					<#if _session.USER.id == user.id><a href="/post/delete?id=${post.id}">Delete this post!</a></#if>
				</td>
			</tr>
			</table>
		
		</td>
	</tr>
	</#list>
</#if>
</table>

</body>
</html>