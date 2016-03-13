package microblog.services;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import microblog.models.User;
import microblog.services.mongodb.ServiceFactoryImpl;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceTest {
	private static ServiceFactory sf;
	
	@BeforeClass
	public static void setup() {
		sf = new ServiceFactoryImpl("microblog", "localhost", 27017);
		try {
			sf.init();
			// initialize the tests from scratch
			sf.getUserService().drop();
			sf.getUserService().create();
		}
		catch(Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void t01create() {
		sf.getUserService().drop();
		sf.getUserService().create();
	}
	
	@Test
	public void t02insert() {
		User user = new User();
		user.setUsername("joaozinho");
		user.setEmail("joaozinho@acme.com");
		user.setFirstName("Joao");
		user.setLastName("Sync");
		user.setPassword("123");
		
		sf.getUserService().save(user);
		
		user = sf.getUserService().get(user.getId());
		Assert.assertTrue(user != null);
		Assert.assertTrue(user.getId() != null);
		Assert.assertTrue(user.getUsername().equals("joaozinho"));
		Assert.assertTrue(user.getEmail().equals("joaozinho@acme.com"));
		Assert.assertTrue(user.getFirstName().equals("Joao"));
		Assert.assertTrue(user.getLastName().equals("Sync"));
		Assert.assertTrue(user.getPassword().equals("123"));
	}
	
	@Test
	public void t02insert2() {
		User user = new User();
		user.setUsername("pedrinho");
		user.setEmail("pedrinho@acme.com");
		user.setFirstName("Pedro");
		user.setLastName("Sync");
		user.setPassword("123");
		sf.getUserService().save(user);
		
		user = sf.getUserService().get(user.getId());
		Assert.assertTrue(user != null);
		Assert.assertTrue(user.getId() != null);
		Assert.assertTrue(user.getUsername().equals("pedrinho"));
		Assert.assertTrue(user.getEmail().equals("pedrinho@acme.com"));
		Assert.assertTrue(user.getFirstName().equals("Pedro"));
		Assert.assertTrue(user.getLastName().equals("Sync"));
		Assert.assertTrue(user.getPassword().equals("123"));
	}
	
	@Test
	public void t03update() {
		List<User> users = sf.getUserService().list();
		Assert.assertTrue(users != null);
		Assert.assertTrue(users.size() == 2);
		
		User user = users.get(0);
		Assert.assertTrue(user.getFirstName().equals("Joao"));
		
		user.setPassword("difficultPassword!");
		sf.getUserService().save(user);
		
		users = sf.getUserService().list();
		user = users.get(0);
		Assert.assertTrue(user.getFirstName().equals("Joao"));
		Assert.assertTrue(user.getPassword().equals("difficultPassword!"));
	}
	
	@Test
	public void t04getUsingUsername() {
		User user = sf.getUserService().get("joaozinho");
		Assert.assertTrue(user != null);
		Assert.assertTrue(user.getUsername().equals("joaozinho"));
	}
	
	@Test
	public void t05getUsingEmail() {
		User user = sf.getUserService().get("joaozinho@acme.com");
		Assert.assertTrue(user != null);
		Assert.assertTrue(user.getUsername().equals("joaozinho"));
	}
	
	@Test
	public void t05recreateIndexes() {
		sf.getUserService().create();
	}
}
