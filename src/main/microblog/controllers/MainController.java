package microblog.controllers;

import java.util.List;

import io.syncframework.api.Action;
import io.syncframework.api.ApplicationContext;
import io.syncframework.api.Controller;
import io.syncframework.api.ErrorContext;
import io.syncframework.api.Parameter;
import io.syncframework.api.RequestContext;
import io.syncframework.api.Result;
import io.syncframework.api.ResultFactory;
import io.syncframework.api.SessionContext;

import microblog.models.Post;
import microblog.models.User;
import microblog.services.ServiceFactory;

@Controller(url="/*")
public class MainController {
	private ApplicationContext application;
	private ErrorContext errors;
	private RequestContext request;
	private SessionContext session;
	@Parameter
	private User user;
	@Parameter
	private String username;
	@Parameter
	private String password;
	@Parameter
	private List<Post> posts;
	
	@Action
	public Result login() {
		// validates the form first...
		if(username == null || password == null) {
			errors.put("login", "username or password are invalid");
			return ResultFactory.render("/login.ftl");
		}
		
		// retrieving database services
		ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
		
		// search for username or email...
		user = sf.getUserService().get(username);
		if(user == null) {
			errors.put("login", "username or password are invalid");
			return ResultFactory.render("/login.ftl");
		}		
		// check password
		if(!user.getPassword().equals(password)) {
			errors.put("login", "username or password are invalid");
			return ResultFactory.render("/login.ftl");
		}
		
		session.put("USER", user);
		
		return ResultFactory.redirect("/home");
	}
	
	@Action
	public Result logout() {
		session.remove("USER");
		return ResultFactory.redirect("/main");
	}
	
	@Action(interceptedBy=AuthInterceptor.class)
	public Result home() {
		ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
		
		user = (User)session.get("USER");
		posts = sf.getPostService().list(user);
		
		return ResultFactory.render("/home.ftl");
	}
	
	@Action(interceptedBy=AuthInterceptor.class)
	public Result main() {
		String url = (String)request.get(RequestContext.URL);
		
		if(!url.equals("/")) {
			ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
			
			username = url.substring(1);
			user = sf.getUserService().get(username);
			if(user != null) {
				posts = sf.getPostService().list(user);
				return ResultFactory.render("/home.ftl");
			}
		}
		
		return ResultFactory.redirect("/home");
	}

	public ApplicationContext getApplication() {
		return application;
	}

	public void setApplication(ApplicationContext application) {
		this.application = application;
	}

	public ErrorContext getErrors() {
		return errors;
	}

	public void setErrors(ErrorContext errors) {
		this.errors = errors;
	}

	public RequestContext getRequest() {
		return request;
	}

	public void setRequest(RequestContext request) {
		this.request = request;
	}

	public SessionContext getSession() {
		return session;
	}

	public void setSession(SessionContext session) {
		this.session = session;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
}
