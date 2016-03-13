package microblog.controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.syncobjects.as.api.ApplicationContext;
import com.syncobjects.as.api.ApplicationContextMock;
import com.syncobjects.as.api.ErrorContext;
import com.syncobjects.as.api.RedirectResult;
import com.syncobjects.as.api.RenderResult;
import com.syncobjects.as.api.Result;
import com.syncobjects.as.api.SessionContext;

import microblog.models.User;
import microblog.startup.ServiceInitializer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SignupControllerTest {
	private static final ApplicationContext application = new ApplicationContextMock();
	private static final SessionContext session = new SessionContext();
	private SignupController controller;
	
	/**
	 * calling for ServiceInitializer... so application context can have the database connection established.
	 */
	@BeforeClass
	public static void setup() {
		ServiceInitializer si = new ServiceInitializer();
		si.setApplication(application);
		si.init();
	}
	
	@Before
	public void before() {
		controller = new SignupController();
		controller.setApplication(application);
		controller.setErrors(new ErrorContext());
		controller.setSession(session);
	}
	
	/**
	 * test to show the form
	 */
	@Test
	public void t01main() {
		Result result = controller.main();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/signup/form.ftl"));
	}
	
	@Test
	public void t02signup() {
		// clear session
		session.clear();
		
		controller.setUsername("newuser");
		controller.setEmail("newuser@email.com");
		controller.setFirstName("New");
		controller.setLastName("User");
		controller.setPassword("123");

		Result result = controller.signup();
		Assert.assertTrue(result instanceof RedirectResult);
		RedirectResult redirect = (RedirectResult)result;
		Assert.assertTrue(redirect.getUrl().equals("/home"));
		
		// check for the session as the user will be automatically authenticated.
		User user = (User)session.get("USER");
		Assert.assertTrue(user != null);
		Assert.assertTrue(user.getId() != null);
		Assert.assertTrue(user.getUsername().equals("newuser"));
		Assert.assertTrue(user.getEmail().equals("newuser@email.com"));
		Assert.assertTrue(user.getFirstName().equals("New"));
		Assert.assertTrue(user.getLastName().equals("User"));
		Assert.assertTrue(user.getPassword().equals("123"));
	}
	
	@Test
	public void t03signupWithNoUserInput() {
		// clear session; so no interference in this test
		session.clear();
		
		Result result = controller.signup();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/signup/form.ftl"));
		
		// first error is the email...
		Assert.assertTrue(controller.getErrors().get("email") != null);
		Assert.assertTrue(controller.getErrors().get("email").equals("Email is required"));
		Assert.assertTrue(controller.getErrors().get("firstName") != null);
		Assert.assertTrue(controller.getErrors().get("firstName").equals("First Name is required"));
		Assert.assertTrue(controller.getErrors().get("lastName") != null);
		Assert.assertTrue(controller.getErrors().get("lastName").equals("Last Name is required"));
		Assert.assertTrue(controller.getErrors().get("password") != null);
		Assert.assertTrue(controller.getErrors().get("password").equals("Password is required"));
		Assert.assertTrue(controller.getErrors().get("username") != null);
		Assert.assertTrue(controller.getErrors().get("username").equals("Username is required"));
	}
	
	@Test
	public void t04signupWithEmail() {
		// clear session; so no interference in this test
		session.clear();
		controller.setEmail("newuser@email.com");
		
		Result result = controller.signup();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/signup/form.ftl"));
		
		// first error is the email...
		Assert.assertTrue(controller.getErrors().get("email") == null);
		Assert.assertTrue(controller.getErrors().get("firstName") != null);
		Assert.assertTrue(controller.getErrors().get("firstName").equals("First Name is required"));
		Assert.assertTrue(controller.getErrors().get("lastName") != null);
		Assert.assertTrue(controller.getErrors().get("lastName").equals("Last Name is required"));
		Assert.assertTrue(controller.getErrors().get("password") != null);
		Assert.assertTrue(controller.getErrors().get("password").equals("Password is required"));
		Assert.assertTrue(controller.getErrors().get("username") != null);
		Assert.assertTrue(controller.getErrors().get("username").equals("Username is required"));
	}
	
	@Test
	public void t05signupDuplicatedEmail() {
		// clear session; so no interference in this test
		session.clear();
		controller.setUsername("newnewnew");
		controller.setEmail("newuser@email.com");
		controller.setFirstName("New");
		controller.setLastName("User");
		controller.setPassword("123");
		
		Result result = controller.signup();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/signup/form.ftl"));
		
		// first error is the email...
		Assert.assertTrue(controller.getErrors().get("email") != null);
		Assert.assertTrue(controller.getErrors().get("email").equals("Email is already registered"));
	}
	
	@Test
	public void t05signupDuplicatedUsername() {
		// clear session; so no interference in this test
		session.clear();
		controller.setUsername("newuser");
		controller.setEmail("newuser.new@email.com");
		controller.setFirstName("New");
		controller.setLastName("User");
		controller.setPassword("123");
		
		Result result = controller.signup();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/signup/form.ftl"));
		
		// first error is the email...
		Assert.assertTrue(controller.getErrors().get("username") != null);
		Assert.assertTrue(controller.getErrors().get("username").equals("Username is already registered"));
	}
}
