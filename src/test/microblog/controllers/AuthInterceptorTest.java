package microblog.controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import io.syncframework.api.RenderResult;
import io.syncframework.api.Result;
import io.syncframework.api.SessionContext;

import microblog.models.User;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthInterceptorTest {
	private static final SessionContext session = new SessionContext();
	private AuthInterceptor interceptor;
	
	@Before
	public void before() {
		interceptor = new AuthInterceptor();
		interceptor.setSession(session);
	}
	
	@Test
	public void t01beforeUnauthorizedUserRequest() {
		// clearing session to make sure that no test is jeopardizing this one
		session.clear();
		
		Result result = interceptor.before();
		Assert.assertTrue(result instanceof RenderResult);
		RenderResult render = (RenderResult)result;
		Assert.assertTrue(render.getTemplate().equals("/login.ftl"));
	}
	
	@Test
	public void t02beforeAuthorizedUserRequest() {
		// clearing session to make sure that no test is jeopardizing this one
		session.clear();
		// just a mock user... no need to authenticate or something
		session.put("USER", new User());
		
		Result result = interceptor.before();
		Assert.assertTrue(result == null);
	}
	
	@Test
	public void t03after() {
		Assert.assertTrue(interceptor.after() == null);
	}
}
