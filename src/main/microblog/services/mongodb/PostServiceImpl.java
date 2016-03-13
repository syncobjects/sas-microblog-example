package microblog.services.mongodb;

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
import microblog.services.PostService;
import microblog.services.ServiceFactory;

public class PostServiceImpl implements PostService {
	private static final String COLLECTION = "post";
	private static final String SEQUENCES = "sequences";
	private static final String FIELD_ID = "_id";
	private static final String FIELD_CREATED = "created";
	private static final String FIELD_LIKES = "likes";
	private static final String FIELD_MESSAGE = "message";
	private static final String FIELD_USER = "user";
	private ServiceFactory sf;
	private MongoDatabase database;
	
	public PostServiceImpl(ServiceFactory sf, MongoDatabase database) {
		this.sf = sf;
		this.database = database;
	}
	
	/**
	 * Private method to be utilised on all operations where Post object has to be created from a BSON Document.
	 * Also queries for the User.
	 * 
	 * @param doc
	 * @return Post
	 */
	private Post get(Document doc) {
		Post post = new Post();
		post.setId(doc.getLong(FIELD_ID));
		post.setCreated(doc.getDate(FIELD_CREATED));
		post.setLikes(doc.getInteger(FIELD_LIKES));
		post.setMessage(doc.getString(FIELD_MESSAGE));
		post.setUser(sf.getUserService().get(doc.getLong(FIELD_USER)));
		return post;
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
		coll.createIndex(new Document(FIELD_USER, true));

		// creating or recreating sequences
		MongoCollection<Document> sequences = database.getCollection(SEQUENCES);
		sequences.deleteOne(new Document(FIELD_ID, COLLECTION));

		// recreate sequence with last id from the COLLECTION
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
	public Post get(Long id) {
		MongoCollection<Document> coll = database.getCollection(COLLECTION);
		Document doc = coll.find(new Document(FIELD_ID, id)).first();
		if(doc == null) {
			return null;
		}
		return get(doc);
	}

	@Override
	public List<Post> list(User user) {
		MongoCollection<Document> coll = database.getCollection(COLLECTION);
		List<Post> list = new LinkedList<Post>();
		MongoCursor<Document> cursor = coll.find(Filters.eq(FIELD_USER, user.getId())).iterator();
		try {
			while(cursor.hasNext()) {
				Document doc = cursor.next();
				Post post = get(doc);
				list.add(post);
			}
		}
		finally {
			cursor.close();
		}
		return list;
	}

	@Override
	public void save(Post post) {
		if(post == null)
			throw new IllegalArgumentException("post");
		if(post.getUser() == null)
			throw new IllegalArgumentException("post.user");

		// we need to create an ID in case of insert
		if(post.getId() == null) {
			MongoCollection<Document> sequences = database.getCollection(SEQUENCES);
			Document update = new Document("$inc", new Document("seq", new Long(1)));
			Document doc = sequences.findOneAndUpdate(Filters.eq(FIELD_ID, COLLECTION), update, new FindOneAndUpdateOptions().upsert(true));
			if(doc == null)
				post.setId(1L);
			else
				post.setId(doc.getLong("seq"));
		}

		Document doc = new Document();
		doc.put(FIELD_ID, post.getId());
		doc.put(FIELD_CREATED, post.getCreated());
		doc.put(FIELD_LIKES, post.getLikes());
		doc.put(FIELD_MESSAGE, post.getMessage());
		doc.put(FIELD_USER, post.getUser().getId());

		MongoCollection<Document> coll = database.getCollection(COLLECTION);
		coll.replaceOne(Filters.eq(FIELD_ID, post.getId()), doc, new UpdateOptions().upsert(true));
	}
}
