package microblog.services.mongodb;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;

import microblog.models.Post;
import microblog.models.User;
import microblog.services.ServiceFactory;
import microblog.services.UserService;

public class UserServiceImpl implements UserService {
	private static final String COLLECTION = "user";
	private static final String SEQUENCES = "sequences";
	private static final String FIELD_ID = "_id";
	private static final String FIELD_EMAIL = "email";
	private static final String FIELD_FIRST_NAME = "firstName";
	private static final String FIELD_LAST_NAME = "lastName";
	private static final String FIELD_PASSWORD = "password";
	private static final String FIELD_USERNAME = "username";
	private ServiceFactory sf;
	private MongoDatabase database;

	public UserServiceImpl(ServiceFactory sf, MongoDatabase database) {
		this.sf = sf;
		this.database = database;
	}

	/**
	 * Private method to be utilised on all operations where User object has to be created from a BSON Document.
	 * @param doc
	 * @return User
	 */
	private User get(Document doc) {
		User user = new User();
		user.setId(doc.getLong(FIELD_ID));
		user.setEmail(doc.getString(FIELD_EMAIL));
		user.setFirstName(doc.getString(FIELD_FIRST_NAME));
		user.setLastName(doc.getString(FIELD_LAST_NAME));
		user.setPassword(doc.getString(FIELD_PASSWORD));
		user.setUsername(doc.getString(FIELD_USERNAME));
		return user;
	}

	/**
	 * creates indexes and sequences under the mongo.
	 * create() can be also utilized to recreate indexes and sequences, so optimize the database.
	 */
	@Override
	public void create() {
		// creating or recreating the indexes
		MongoCollection<Document> coll = database.getCollection(COLLECTION);
		coll.dropIndexes();
		coll.createIndex(new Document(FIELD_ID, true), new IndexOptions().unique(true));
		coll.createIndex(new Document(FIELD_USERNAME, true));
		coll.createIndex(new Document(FIELD_EMAIL, true));

		// creating or recreating sequences
		MongoCollection<Document> sequences = database.getCollection(SEQUENCES);
		sequences.deleteOne(new Document(FIELD_ID, COLLECTION));

		// recreate sequence using the last id from the COLLECTION	
		Document doc = coll.find().sort(new Document(FIELD_ID, -1)).first();
		Long maxId = 1L;
		if(doc != null) {
			maxId = doc.getLong(FIELD_ID) + 1;
		}
		
		sequences.insertOne(new Document(FIELD_ID, COLLECTION).append("seq", maxId));
	}

	@Override
	public void delete(Long id) {
		if(id == null)
			throw new IllegalArgumentException("id");

		// first delete all users posts
		User user = new User();
		user.setId(id);
		List<Post> posts = sf.getPostService().list(user);
		for(Post post: posts) {
			sf.getPostService().delete(post.getId());
		}

		// now let's delete the user from the database
		MongoCollection<Document> coll = database.getCollection(COLLECTION);
		coll.deleteOne(new Document(FIELD_ID, id));
	}

	@Override
	public void drop() {
		MongoCollection<Document> coll = null;
		// drop the sequence
		coll = database.getCollection(SEQUENCES);
		coll.deleteOne(Filters.eq(FIELD_ID, COLLECTION));
		// drop the COLLECTION
		coll = database.getCollection(COLLECTION);
		coll.drop();
	}

	@Override
	public User get(Long id) {		
		MongoCollection<Document> coll = database.getCollection(COLLECTION);
		Document doc = coll.find(new Document(FIELD_ID, id)).first();
		if(doc == null) {
			return null;
		}
		return get(doc);
	}
	
	@Override
	public User get(String username) {
		MongoCollection<Document> coll = database.getCollection(COLLECTION);
		User user = null;
		Document filter = new Document("$or", Arrays.asList(new Document(FIELD_USERNAME, username), new Document(FIELD_EMAIL, username)));
		MongoCursor<Document> cursor = coll.find(filter).sort(new Document(FIELD_USERNAME, 1)).iterator();
		try {
			if(cursor.hasNext()) {
				Document doc = cursor.next();
				user = get(doc);
			}
		}
		finally {
			cursor.close();
		}
		return user;
	}

	@Override
	public List<User> list() {
		MongoCollection<Document> coll = database.getCollection(COLLECTION);
		List<User> list = new LinkedList<User>();
		MongoCursor<Document> cursor = coll.find().iterator();
		try {
			while(cursor.hasNext()) {
				Document doc = cursor.next();
				User user = get(doc);
				list.add(user);
			}
		}
		finally {
			cursor.close();
		}
		return list;
	}

	@Override
	public void save(User user) {
		if(user == null)
			throw new IllegalArgumentException("user");

		// we need to create an ID in case of insert
		if(user.getId() == null) {
			MongoCollection<Document> sequences = database.getCollection(SEQUENCES);
			Document update = new Document("$inc", new Document("seq", new Long(1)));
			Document doc = sequences.findOneAndUpdate(Filters.eq(FIELD_ID, COLLECTION), update, new FindOneAndUpdateOptions().upsert(true));
			if(doc == null)
				user.setId(1L);
			else
				user.setId(doc.getLong("seq"));
		}

		Document doc = new Document();
		doc.put(FIELD_ID, user.getId());
		doc.put(FIELD_EMAIL, user.getEmail());
		doc.put(FIELD_FIRST_NAME, user.getFirstName());
		doc.put(FIELD_LAST_NAME, user.getLastName());
		doc.put(FIELD_PASSWORD, user.getPassword());
		doc.put(FIELD_USERNAME, user.getUsername());

		MongoCollection<Document> coll = database.getCollection(COLLECTION);
		coll.replaceOne(Filters.eq(FIELD_ID, user.getId()), doc, new UpdateOptions().upsert(true));
	}
}
