package com.formation.cas3;

public class GestionnaireDependances {
	
	public static void main(String[] args) {
		
		// création les objets de l'application
		UserDao3 userDao3 = new UserDaoImpl3();
		UserService3 userService3 = new UserServiceImpl3(userDao3);
		
		
		//utilisation de l'application 
		
		
	}

}
