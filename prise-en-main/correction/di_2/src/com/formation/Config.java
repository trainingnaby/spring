package com.formation;

public enum Config {

	INSTANCE;

	private UserDao userDao;

	// Singleton pattern pour s'assurer qu'il n'y a qu'une seule instance de UserDao
	public UserDao getUserDao() {
		if (userDao == null) {
			userDao = new UserDao();
		}
		return userDao;
	}

}
