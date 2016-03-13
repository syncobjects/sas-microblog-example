package microblog.startup;

import java.util.Properties;

import com.syncobjects.as.api.ApplicationContext;
import com.syncobjects.as.api.Initializer;

import microblog.services.ServiceFactory;
import microblog.services.mongodb.ServiceFactoryImpl;

@Initializer
public class ServiceInitializer {
	private ApplicationContext application;
	
	public void init() {
		System.out.println("application starting");
		
		Properties props = (Properties)application.get(ApplicationContext.PROPERTIES);
		String name = props.getProperty("database.name");
		String host = props.getProperty("database.host");
		String port = props.getProperty("database.port");
		
		ServiceFactory sf = new ServiceFactoryImpl(name, host, new Integer(port));
		try {
			sf.init();
		}
		catch(Exception e) {
			System.err.println("failed to initialize the database resource");
			e.printStackTrace();
		}
		application.put("SERVICE", sf);
	}
	
	public void destroy() {
		ServiceFactory sf = (ServiceFactory)application.get("SERVICE");
		try { 
			sf.destroy();
		}
		catch(Exception e) {
			System.err.println("failed to destroy database resources");
			e.printStackTrace();
		}
		System.out.println("application shutdown");
	}

	public ApplicationContext getApplication() {
		return application;
	}

	public void setApplication(ApplicationContext application) {
		this.application = application;
	}
}
