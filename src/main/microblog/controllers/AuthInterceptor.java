package microblog.controllers;

import io.syncframework.api.Interceptor;
import io.syncframework.api.Result;
import io.syncframework.api.ResultFactory;
import io.syncframework.api.SessionContext;

@Interceptor
public class AuthInterceptor {
	private SessionContext session;
	
	public Result before() {
		if(!session.containsKey("USER"))
			return ResultFactory.render("/login.ftl");
		return null;
	}
	
	public Result after() {
		return null;
	}

	public SessionContext getSession() {
		return session;
	}

	public void setSession(SessionContext session) {
		this.session = session;
	}
}
