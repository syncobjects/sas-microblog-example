package microblog.controllers;

import com.syncobjects.as.api.Interceptor;
import com.syncobjects.as.api.Result;
import com.syncobjects.as.api.ResultFactory;
import com.syncobjects.as.api.SessionContext;

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
