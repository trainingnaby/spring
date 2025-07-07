package com.formation;

public class UserService {
	
	// Avantages :  instance unique de UserDao => plus de gaspillages de mémoire / ressources
	// plus de duplication des new
	// couplage faible entre UserService et UserDao
	
	
	// inconvenients : recherche active de la dépendance UserDao dans Config
	// lecture illigible de la dépendance UserDao dans UserService
	
	public User save (User user) {
		
		UserDao userDao = Config.INSTANCE.getUserDao(); // chaque fois on reutilise la même instance de UserDao
		return userDao.save(user);
	}
	
	
	

}
