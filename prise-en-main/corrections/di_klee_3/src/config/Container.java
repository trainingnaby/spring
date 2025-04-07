package config;

import dao.UserDao;
import service.UserService;

public class Container {
	
	public void createObjectsAndInjectDependencies() {
		
		// Ici les classes de l'application sont instanciées et les dépendences sont injectées
		// cette classe est conceptuellement le conteneur d'injection de dépendences
		// la définition des classes et de leurs dépendences est faite dans un fichier de configuration ou
		// via des annotations (Spring, CDI, Guice ...)	
		
		// création des classes de notre application 
		UserDao userDao =  new UserDao();
		UserService userService = new UserService();
		
		// injection des dépendences<
		userService.setUserDao(userDao);
	}

}
