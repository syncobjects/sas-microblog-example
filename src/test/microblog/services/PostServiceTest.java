package microblog.services;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import microblog.models.Post;
import microblog.models.User;
import microblog.services.mongodb.ServiceFactoryImpl;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PostServiceTest {
	private static ServiceFactory sf;
	
	@BeforeClass
	public static void setup() {
		sf = new ServiceFactoryImpl("microblog", "localhost", 27017);
		try {
			sf.init();
			// initialize the tests from scratch
			sf.getPostService().drop();
			sf.getPostService().create();
		}
		catch(Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void t01create() {
		sf.getPostService().drop();
		sf.getPostService().create();
	}
	
	@Test
	public void t02insert() {
		User user = sf.getUserService().get(1L);
		Assert.assertTrue(user != null);
		
		Post post = new Post();
		post.setCreated(new Date());
		post.setLikes(0);
		post.setMessage("Today is my first day programming with SAS! Loved it!");
		post.setUser(user);
		sf.getPostService().save(post);
		
		Assert.assertTrue(post.getId() != null);
		post = sf.getPostService().get(post.getId());
		Assert.assertTrue(post != null);
		Assert.assertTrue(post.getId() != null);
		Assert.assertTrue(post.getCreated() != null);
		Assert.assertTrue(post.getLikes() == 0);
		Assert.assertTrue(post.getMessage().equals("Today is my first day programming with SAS! Loved it!"));
		Assert.assertTrue(post.getUser() != null);
		Assert.assertTrue(post.getUser().getId() == 1L);
	}
	
	@Test
	public void t02insert2() {
		User user = sf.getUserService().get(1L);
		Assert.assertTrue(user != null);
		
		Post post = new Post();
		post.setCreated(new Date());
		post.setLikes(0);
		post.setMessage("This is my second post. Amazing application!");
		post.setUser(user);
		sf.getPostService().save(post);
		
		post = sf.getPostService().get(post.getId());
		Assert.assertTrue(post != null);
		Assert.assertTrue(post.getId() != null);
		Assert.assertTrue(post.getCreated() != null);
		Assert.assertTrue(post.getLikes() == 0);
		Assert.assertTrue(post.getMessage().equals("This is my second post. Amazing application!"));
		Assert.assertTrue(post.getUser() != null);
		Assert.assertTrue(post.getUser().getId() == 1L);
	}
	
	@Test
	public void t03update() {
		Post post = sf.getPostService().get(1L);
		Assert.assertTrue(post != null);
		post.setLikes( post.getLikes() + 1 );
		sf.getPostService().save(post);
		
		post = sf.getPostService().get(post.getId());
		Assert.assertTrue(post != null);
		Assert.assertTrue(post.getId() != null);
		Assert.assertTrue(post.getCreated() != null);
		Assert.assertTrue(post.getLikes() == 1);
		Assert.assertTrue(post.getMessage().equals("Today is my first day programming with SAS! Loved it!"));
		Assert.assertTrue(post.getUser() != null);
		Assert.assertTrue(post.getUser().getId() == 1L);
	}
	
	@Test
	public void t04list() {
		User user = sf.getUserService().get(1L);
		Assert.assertTrue(user != null);
		
		List<Post> posts = sf.getPostService().list(user);
		Assert.assertTrue(posts.size() == 2);
	}
	
	@Test
	public void t05recreateIndexes() {
		sf.getPostService().create();
	}
}
