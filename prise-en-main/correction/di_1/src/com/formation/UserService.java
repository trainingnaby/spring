package com.formation;

public class UserService {
	
	// Application sans gestion des dépendances
	
	// Avantages : ça marche et c'est simple à comprendre
	
	// Inconvénients : pas de séparation des responsabilités,
	// Duplication de code, pas de test unitaire possible,
	// couplage fort entre les classes,
	// Gaspillage de ressources
	
	
	
	// séparation des responsabilités : quand on veut
	// faire des opérations sur les utilisateurs,
	private UserDao userDao;
	
	public User save (User user) {
		
		// appication des régles métiers
		
		UserDao userDao = new UserDao();
		return userDao.save(user);
	}
	
	
	

}
