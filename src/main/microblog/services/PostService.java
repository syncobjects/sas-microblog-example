package microblog.services;

import java.util.List;

import microblog.models.Post;
import microblog.models.User;

public interface PostService {
	public void create();
	public void delete(Long id);
	public void drop();
	public Post get(Long id);
	public List<Post> list(User user);
	public void save(Post post);
}