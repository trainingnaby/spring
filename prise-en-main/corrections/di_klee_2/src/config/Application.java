package config;

import dao.UserDao;

// design pattern singleton => retourne toujours la meme instance de userdao
public enum Application {
	
	INSTANCE;
	
	private UserDao userDao;
	
	public UserDao getUserDao() {
		
		if(userDao == null) {
			userDao = new UserDao();
		}
		return userDao;
	}

}
