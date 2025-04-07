package service;

import dao.UserDao;
import domain.User;

public class UserService {
	
	// avantages : ça marche ....
	
	// inconvenients : si plusieurs classes utilisent un UserDao => duplication de code
	//				   ouverture de sockets reseaux à chaque instanciation d'un UserDao => gaspillage de ressources
	//				   couplage fort entre UserService et UserDao
	
	private UserDao userDao;
	
	public User save(User user) {
		
		// codage des règles métiers
		
		// sauvergarde en base de données le user (séparation des responsabilités)
		userDao = new UserDao(); //instancie un objet de la classe
		userDao.save(user);
		
		return user;
	}

}
