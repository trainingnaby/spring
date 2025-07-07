package com.formation;

public class UserService {
	
	// declaration de la dependance sur UserDao
	private UserDao userDao;
	
	// fournir un point d'injection pour la dépendance
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public User save (User user) {
		
		// application des régles métiers
		
		return userDao.save(user);
	}
	
	
	

}
