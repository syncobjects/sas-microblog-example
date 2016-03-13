package microblog.services;

public interface ServiceFactory {
	public void init() throws Exception;
	public void destroy() throws Exception;
	public PostService getPostService();
	public UserService getUserService();
}