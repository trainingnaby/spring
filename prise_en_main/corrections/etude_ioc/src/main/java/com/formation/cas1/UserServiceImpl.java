package com.formation.cas1;

public class UserServiceImpl implements UserService{
	
	// dépendance pour les actions en base de données
	private UserDao userDao;
	
	/**
	 * Premier solution pour créer la dépendance : la classe elle même instancie la dépendance
	 * 
	 * 
	 * Avantages : ça marche ...
	 * 
	 * 
	 * Incovenients :
	 * Couplage fort entre UserServiceImpl et UserDaoImpl
	 * Duplication de code (quand plusieurs classes qui ont la même dépendance)
	 * La classe UserDaoImpl fait les accès à la base : ouverture de sockets => trés cou^teux
	 * 
	 */
	public UserServiceImpl() {
		this.userDao = new UserDaoImpl();
	}

	@Override
	public void createUser(User user) {
		
		// application des règles métiers
		
		// enregistrement du user en base de données (séparation des responsabilités)
		userDao.create(user);
		
	}

}
