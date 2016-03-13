package microblog.controllers;

import com.syncobjects.as.api.Action;
import com.syncobjects.as.api.ApplicationContext;
import com.syncobjects.as.api.Controller;
import com.syncobjects.as.api.ErrorContext;
import com.syncobjects.as.api.Parameter;
import com.syncobjects.as.api.Result;
import com.syncobjects.as.api.ResultFactory;
import com.syncobjects.as.api.SessionContext;

import microblog.models.User;
import microblog.services.ServiceFactory;

@Controller(url="/signup/*")
public class SignupController {
	private ApplicationContext application;
	private ErrorContext errors;
	private SessionContext session;
	@Parameter
	private String email;
	@Parameter
	private String firstName;
	@Parameter
	private String lastName;
	@Parameter
	private String password;
	@Parameter
	private String username;
	
	@Action
	public Result main() {
		// show the form...
		return ResultFactory.render("/signup/form.ftl");
	}
	
	/**
	 * do the user signup
	 * @return Redirects to /main or back to the form
	 */
	@Action
	public Result signup() {
		if(email == null) {
			errors.put("email", "Email is required");
		}
		if(firstName == null) {
			errors.put("firstName", "First Name is required");
		}
		if(lastName == null) {
			errors.put("lastName", "Last Name is required");
		}
		if(password == null) {
			errors.put("password", "Password is required");
		}
		if(username == null) {
			errors.put("username", "Username is required");
		}
		if(errors.size() > 0) {
			return ResultFactory.render("/signup/form.ftl");
		}
		
		ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
		// first is to validate if username and email are already registered.
		User user = sf.getUserService().get(email);
		if(user != null) {
			errors.put("email", "Email is already registered");
			return ResultFactory.render("/signup/form.ftl");
		}
		user = sf.getUserService().get(username);
		if(user != null) {
			errors.put("username", "Username is already registered");
			return ResultFactory.render("/signup/form.ftl");
		}
		
		user = new User();
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPassword(password);
		user.setUsername(username);
		sf.getUserService().save(user);
		
		session.put("USER", user);
		
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

	public SessionContext getSession() {
		return session;
	}

	public void setSession(SessionContext session) {
		this.session = session;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
