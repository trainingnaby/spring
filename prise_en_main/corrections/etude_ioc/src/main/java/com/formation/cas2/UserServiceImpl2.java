package com.formation.cas2;

import com.formation.cas1.User;

public class UserServiceImpl2 implements UserService2{
	

	/** Utilisation d'une instance unique de UserDao pour toute l'application
	 * 
	 * Avantages :
	 * instance unique => évite le gaspillage des resssources
	 * Permet d'éviter la duplication de code 
	 * 
	 * 
	 * Incovenients
	 * Recherche active des dépendances (contraire au prince d'injection de dépendances)
	 * Les dépendances de la classe sont difficiles à cerner à la lecture du code 
	 * 	problématique pour les tests unitaires
	 * 
	 * 
	 */

	@Override
	public void createUser(User user) {
		
		// application des règles métiers
		
		// enregistrement du user en base de données (séparation des responsabilités)
		Application.INSTANCE.getUserDao().create(user);
		
	}

}
