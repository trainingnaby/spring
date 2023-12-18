package com.formation.cas2;

/**
 * Création d"un singleton (instance unique pour la classe UserDaoImpl2
 */
public enum Application {
	
	INSTANCE;
	
	private UserDao2 userDao2;
	
	public UserDao2 getUserDao() {
		if(userDao2 == null) {
			this.userDao2 = new UserDaoImpl2();
		}
		return userDao2;
	}

}
