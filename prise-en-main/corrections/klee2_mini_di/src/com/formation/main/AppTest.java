package com.formation.main;

import com.formation.components.Repository;
import com.formation.components.Service;
import com.formation.components.Train;
import com.formation.components.Vehicule;
import com.formation.container.Container;

public class AppTest {
	public static void main(String[] args) throws Exception {
		Container myContainer = new Container(Repository.class, Service.class, Train.class);
		Service service = myContainer.getBean(Service.class);
		Vehicule vehicule = myContainer.getBean(Vehicule.class);
		vehicule.bouger();

		
		service.getUsers();
		vehicule.bouger();
		
		

	}
}
