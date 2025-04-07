package service;

import dao.UserDao;
import domain.User;

public class UserService {
	
	// Avantages => identification facile des dépendances d'une classe à sa lecture
	// meilleure gestion des ressources (réseaux ...)
	// recherche passive de dépendances
	// possibilité d'enrichir les classes par le container : Ajout de logs, securité, transactions ...
	
	// declaration de la dépendence
    private UserDao userDao;
	
    // fournit un point d'injection de la dépendance (setters ou un constructeur)
    public void setUserDao(UserDao userDao) {
    	this.userDao = userDao;
    }
	
	public User save(User user) {
		
		// codage des règles métiers
		
		// utilisation de la dépendence sans faire un new
		return userDao.save(user);
	}

}
