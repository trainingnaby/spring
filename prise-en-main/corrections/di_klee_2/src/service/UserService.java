package service;

import config.Application;
import dao.UserDao;
import domain.User;

public class UserService {
	
	// avantages : instance unique de UserDao partagée => plein de gaspillage de ressources
	//			   plus de duplication de code (new)
	//			   couplage faible
	
	// incovenients : recherche active de dépendences
	//				  lecture illisible des dépendences d'une classe
	//				  => problématiqe pour les tests unitaires/integration
	
	
	public User save(User user) {
		
		// codage des règles métiers
		
		// appel du singleton
		return Application.INSTANCE.getUserDao().save(user);
	}

}
