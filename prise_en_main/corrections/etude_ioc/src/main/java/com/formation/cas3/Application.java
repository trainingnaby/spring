package com.formation.cas3;

/**
 * Création d"un singleton (instance unique pour la classe UserDaoImpl2
 */
public enum Application {
	
	INSTANCE;
	
	private UserDao3 userDao2;
	
	public UserDao3 getUserDao() {
		if(userDao2 == null) {
			this.userDao2 = new UserDaoImpl3();
		}
		return userDao2;
	}

}
