package com.formation;

public class Container {
	
	// Mini moteur d'injection de dépendances
	// cette classe est conceptuellement un conteneur d'injection de dépendances
	// La définition des classes et de leurs dépendances se fera 
	// dans un fichier de configuration XML ou un fichier de propriétés
	// ou des annotations
	// classe Container === Spring Ioc ou Google Guice, CDI
	
	public void createObjectAndInjectDependencies() {
		
		// Création de l'objet UserService
		UserService userService = new UserService();
		
		// Création de l'objet UserDao
		UserDao userDao = new UserDao();
		
		// Injection de la dépendance UserDao dans UserService
		userService.setUserDao(userDao);
		
		// Maintenant, userService peut utiliser userDao pour ses opérations
	}

}
