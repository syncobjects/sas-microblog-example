package microblog.controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import io.syncframework.api.ApplicationContext;
import io.syncframework.api.ApplicationContextMock;
import io.syncframework.api.ErrorContext;
import io.syncframework.api.RedirectResult;
import io.syncframework.api.RenderResult;
import io.syncframework.api.RequestContext;
import io.syncframework.api.Result;
import io.syncframework.api.SessionContext;

import microblog.models.User;
import microblog.startup.ServiceInitializer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainControllerTest {
	private static final ApplicationContext application = new ApplicationContextMock();
	private static final SessionContext session = new SessionContext();
	private MainController controller;
	
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
		controller = new MainController();
		controller.setApplication(application);
		controller.setErrors(new ErrorContext());
		controller.setRequest(new RequestContext());
		controller.setSession(session);
	}
	
	@Test
	public void t01mainFirstRequest() {
		// we usually don't use RequestContext on the Tests. However, it is important for this test case as
		// the RequestContext.URL will be utilized by the MainController.
		controller.getRequest().put(RequestContext.URL, "/");
		
		Result result = controller.main();
		Assert.assertTrue(result instanceof RedirectResult);
		RedirectResult redirect = (RedirectResult)result;
		Assert.assertTrue(redirect.getUrl().equals("/home"));
	}
	
	@Test
	public void t02mainRequestToAValidUsername() {
		// we usually don't use RequestContext on the Tests. However, it is important for this test case as
		// the RequestContext.URL will be utilized by the MainController.
		controller.getRequest().put(RequestContext.URL, "/joaozinho");
		
		Result result = controller.main();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/home.ftl"));
		// check for user
		Assert.assertTrue(controller.getUser() != null);
		// check for posts
		Assert.assertTrue(controller.getPosts() != null);
		Assert.assertTrue(controller.getPosts().size() > 0);
	}
	
	@Test
	public void t03mainRequestToAInvalidUsername() {
		controller.getRequest().put(RequestContext.URL, "/unknown");
		
		Result result = controller.main();
		Assert.assertTrue(result instanceof RedirectResult);
		RedirectResult redirect = (RedirectResult)result;
		Assert.assertTrue(redirect.getUrl().equals("/home"));
	}
	
	@Test
	public void t04login() {
		controller.setUsername("pedrinho");
		controller.setPassword("123");
		
		Result result = controller.login();
		Assert.assertTrue(result instanceof RedirectResult);
		RedirectResult redirect = (RedirectResult)result;
		Assert.assertTrue(redirect.getUrl().equals("/home"));
		
		// check for the user session
		User user = (User)session.get("USER");
		Assert.assertTrue(user != null);
		Assert.assertTrue(user.getUsername().equals("pedrinho"));
		Assert.assertTrue(user.getPassword().equals("123"));
	}
	
	@Test
	public void t05loginWithNoUsernameAndPassword() {
		// make sure the other sucessful authentication does not interfere with this test
		session.clear();
		
		Result result = controller.login();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/login.ftl"));
		
		// check for the error message
		Assert.assertTrue(controller.getErrors().get("login") != null);
		Assert.assertTrue(controller.getErrors().get("login").equals("username or password are invalid"));
	}
	
	@Test
	public void t05loginWithInvalidUsername() {
		// make sure the other sucessful authentication does not interfere with this test
		session.clear();
				
		controller.setUsername("invalid");
		controller.setPassword("credentials");
		
		Result result = controller.login();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/login.ftl"));
		
		// check for the error message
		Assert.assertTrue(controller.getErrors().get("login") != null);
		Assert.assertTrue(controller.getErrors().get("login").equals("username or password are invalid"));
	}
	
	@Test
	public void t05loginWithInvalidPassword() {
		// make sure the other sucessful authentication does not interfere with this test
		session.clear();
		
		controller.setUsername("pedrinho");
		controller.setPassword("invalidpassword");
		
		Result result = controller.login();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/login.ftl"));
		
		// check for the error message
		Assert.assertTrue(controller.getErrors().get("login") != null);
		Assert.assertTrue(controller.getErrors().get("login").equals("username or password are invalid"));
	}
	
	@Test
	public void t06loginWithUserAuthenticated() {
		// make sure the other sucessful authentication does not interfere with this test
		session.clear();
		// using a mock User in the session to simulate full authentication
		session.put("USER", new User());
		
		Result result = controller.login();
		Assert.assertTrue(result instanceof RedirectResult);
		RedirectResult redirect = (RedirectResult)result;
		Assert.assertTrue(redirect.getUrl().equals("/home"));
	}
	
	@Test
	public void t07logout() {
		// make sure the other sucessful authentication does not interfere with this test
		session.clear();
		// using a mock User in the session to simulate full authentication
		session.put("USER", new User());
		
		Result result = controller.logout();
		Assert.assertTrue(result instanceof RedirectResult);
		RedirectResult redirect = (RedirectResult)result;
		Assert.assertTrue(redirect.getUrl().equals("/main"));
		
		// session has no more the user on it
		Assert.assertTrue(session.containsKey("USER") == false);
	}
	
	@Test
	public void t08home() {
		// make sure the other sucessful authentication does not interfere with this test
		session.clear();
		
		// first we need a sucessfull login... reexecuting test 04
		t04login();
		
		// now we are ready to test home
		Result result = controller.home();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/home.ftl"));
		Assert.assertTrue(controller.getUser() != null);
		Assert.assertTrue(controller.getUser().getUsername().equals("pedrinho"));
		Assert.assertTrue(controller.getPosts() != null);
	}
}
