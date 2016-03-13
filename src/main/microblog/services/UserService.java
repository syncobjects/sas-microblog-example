package microblog.services;

import java.util.List;

import microblog.models.User;

public interface UserService {
	public void create();
	public void delete(Long id);
	public void drop();
	public User get(Long id);
	public User get(String username);
	public List<User> list();
	public void save(User user);
}