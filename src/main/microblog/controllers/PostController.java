package microblog.controllers;

import java.util.Date;

import com.syncobjects.as.api.Action;
import com.syncobjects.as.api.ApplicationContext;
import com.syncobjects.as.api.Controller;
import com.syncobjects.as.api.ErrorContext;
import com.syncobjects.as.api.Parameter;
import com.syncobjects.as.api.Result;
import com.syncobjects.as.api.ResultFactory;
import com.syncobjects.as.api.SessionContext;

import microblog.models.Post;
import microblog.models.User;
import microblog.services.ServiceFactory;

@Controller(url="/post/*")
public class PostController {
	private ApplicationContext application;
	private ErrorContext errors;
	private SessionContext session;
	@Parameter
	private Long id;
	@Parameter
	private String message;
	
	@Action(interceptedBy=AuthInterceptor.class)
	public Result delete() {
		if(id == null) {
			errors.put("message", "id is required");
			return ResultFactory.render("/error.ftl");
		}
		
		ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
		sf.getPostService().delete(id);
		
		return ResultFactory.redirect("/home");
	}
	
	@Action(interceptedBy=AuthInterceptor.class)
	public Result like() {		
		if(id == null) {
			errors.put("message", "id is required");
			return ResultFactory.render("/error.ftl");
		}
		
		ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
		Post post = sf.getPostService().get(id);
		if(post == null) {
			errors.put("message", "invalid id");
			return ResultFactory.render("/error.ftl");
		}
		post.setLikes( post.getLikes() + 1 );
		sf.getPostService().save(post);
		
		User user = (User)session.get("USER");
		if(post.getUser().getId().equals(user.getId()))
			return ResultFactory.redirect("/home");
		else
			return ResultFactory.redirect("/"+post.getUser().getUsername());
	}

	@Action(interceptedBy=AuthInterceptor.class)
	public Result post() {		
		if(message == null)
			return ResultFactory.redirect("/home");
		
		ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
		User user = (User)session.get("USER");
		
		Post post = new Post();
		post.setCreated(new Date());
		post.setLikes(0);
		post.setMessage(message);
		post.setUser(user);
		sf.getPostService().save(post);
		
		return ResultFactory.redirect("/home");
	}

	public ApplicationContext getApplication() {
		return application;
	}

	public void setApplication(ApplicationContext application) {
		this.application = application;
	}

	public SessionContext getSession() {
		return session;
	}

	public void setSession(SessionContext session) {
		this.session = session;
	}

	public ErrorContext getErrors() {
		return errors;
	}

	public void setErrors(ErrorContext errors) {
		this.errors = errors;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}