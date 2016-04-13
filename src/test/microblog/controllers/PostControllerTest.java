package microblog.controllers;

import java.util.List;

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
import io.syncframework.api.Result;
import io.syncframework.api.SessionContext;

import microblog.models.Post;
import microblog.models.User;
import microblog.services.ServiceFactory;
import microblog.startup.ServiceInitializer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PostControllerTest {
	private static final ApplicationContext application = new ApplicationContextMock();
	private static final SessionContext session = new SessionContext();
	private PostController controller;
	
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
		controller = new PostController();
		controller.setApplication(application);
		controller.setErrors(new ErrorContext());
		controller.setSession(session);
		
		// for all the PostController tests we need the user authenticated...
		// instead we are going to query for the database first User and add it to the session...
		ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
		User user = sf.getUserService().get("pedrinho");
		Assert.assertTrue(user != null);
		session.put("USER", user);
	}
	
	@Test
	public void t01post() {
		controller.setMessage("This is my first post in this microblog app!");
		Result result = controller.post();
		Assert.assertTrue(result instanceof RedirectResult);
		RedirectResult redirect = (RedirectResult)result;
		Assert.assertTrue(redirect.getUrl().equals("/home"));
	}
	
	@Test
	public void t02postWithNoMessage() {
		Result result = controller.post();
		Assert.assertTrue(result instanceof RedirectResult);
		RedirectResult redirect = (RedirectResult)result;
		Assert.assertTrue(redirect.getUrl().equals("/home"));
	}
	
	@Test
	public void t03like() {
		User user = (User)session.get("USER");
		
		// get the first post from this user
		ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
		List<Post> posts = sf.getPostService().list(user);
		Assert.assertTrue(posts != null);
		Assert.assertTrue(posts.size() > 0);
		Post post = posts.get(0);
		
		controller.setId(post.getId());
		Result result = controller.like();
		Assert.assertTrue(result instanceof RedirectResult);
		RedirectResult redirect = (RedirectResult)result;
		Assert.assertTrue(redirect.getUrl().equals("/home"));
	}
	
	@Test
	public void t03likePostFromAnotherUser() {
		
		User user = (User)session.get("USER");
		
		// get the first post from this user
		ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
		User anotherUser = sf.getUserService().get("joaozinho");
		Assert.assertTrue(anotherUser != null);
		Assert.assertTrue(anotherUser.getId().equals(user.getId()) == false);
		
		List<Post> posts = sf.getPostService().list(anotherUser);
		Assert.assertTrue(posts != null);
		Assert.assertTrue(posts.size() > 0);
		Post post = posts.get(0);
		
		controller.setId(post.getId());
		Result result = controller.like();
		Assert.assertTrue(result instanceof RedirectResult);
		RedirectResult redirect = (RedirectResult)result;
		Assert.assertTrue(redirect.getUrl().equals("/"+anotherUser.getUsername()));
	}
	
	@Test
	public void t04likeMissingId() {
		// controller.setId(2L);
		Result result = controller.like();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/error.ftl"));
		Assert.assertTrue(controller.getErrors().get("message") != null);
		Assert.assertTrue(controller.getErrors().get("message").equals("id is required"));
	}
	
	@Test
	public void t05likeInvalidId() {
		controller.setId(1000L);
		Result result = controller.like();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/error.ftl"));
		Assert.assertTrue(controller.getErrors().get("message") != null);
		Assert.assertTrue(controller.getErrors().get("message").equals("invalid id"));
	}
	
	@Test
	public void t06delete() {
		User user = (User)session.get("USER");
		// get the first post from this user
		ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
		List<Post> posts = sf.getPostService().list(user);
		Assert.assertTrue(posts != null);
		Assert.assertTrue(posts.size() > 0);
		Post post = posts.get(0);
		
		controller.setId(post.getId());
		Result result = controller.delete();
		Assert.assertTrue(result instanceof RedirectResult);
		RedirectResult redirect = (RedirectResult)result;
		Assert.assertTrue(redirect.getUrl().equals("/home"));
		
		// checking the delete directly from the database
		post = sf.getPostService().get(post.getId());
		Assert.assertTrue(post == null);
	}
}
