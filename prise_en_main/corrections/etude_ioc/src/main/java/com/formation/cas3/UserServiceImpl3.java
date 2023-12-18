package com.formation.cas3;

import com.formation.cas1.User;

public class UserServiceImpl3 implements UserService3{
	
	//déclaration de la dépendance
	private UserDao3 userDao3;
	
	// définition d'un point d'injection de la dépendance par constructeur
	public UserServiceImpl3(UserDao3 userDao3) {
		this.userDao3 = userDao3;
	}
	
//	//définiton d'un point d'injection par setter
//	public void setUserDao3(UserDao3 userDao3) {
//		this.userDao3 = userDao3;
//	}
/**
 * Utilisation d'une entité tierce qui, sur simple déclaration d'un dépendance et la définition
 * d'un point d'injection de cette dépendance, va créer et injecter la dépendance
 * 
 * 
 * Avantages :
 * pas de duplication de code
 * respecte le principe de l'IoC
 * Vue claire des dépendances des classes (ideal pour les tests unitaires)
 * 
 * Le gestionnaire de dépendances peut enrichir les objets via l'AOP
 * 
 * 
 */

	@Override
	public void createUser(User user) {
		
		// application des règles métiers
		
		// enregistrement du user en base de données (séparation des responsabilités)
		userDao3.create(user);
		
	}

}
