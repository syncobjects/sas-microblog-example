package microblog.services.mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import microblog.services.PostService;
import microblog.services.ServiceFactory;
import microblog.services.UserService;

public class ServiceFactoryImpl implements ServiceFactory {
	private String name;
	private String host;
	private Integer port;
	private MongoClient client;
	private MongoDatabase database;
	private PostService postService;
	private UserService userService;
	
	public ServiceFactoryImpl(String name, String host, Integer port) {
		this.name = name;
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void init() throws Exception {
		MongoClientOptions options = MongoClientOptions.builder()
				.serverSelectionTimeout(100)
				.connectTimeout(100)
				.build();
		ServerAddress address = new ServerAddress(this.host, this.port);
		client = new MongoClient(address, options);
		database = client.getDatabase(this.name);
		MongoCollection<Document> coll = database.getCollection("test");
		coll.count();
		
		
		
		System.out.println("database connected");
	}

	@Override
	public void destroy() throws Exception {
		client.close();
		System.out.println("database disconnected");
	}
	
	@Override
	public PostService getPostService() {
		if(postService == null)
			postService = new PostServiceImpl(this, database);
		return postService;
	}
	
	@Override
	public UserService getUserService() {
		if(userService == null)
			userService = new UserServiceImpl(this, database);
		return userService;
	}
}