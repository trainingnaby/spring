package com.formation.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService implements EnvoiService {
	
	@Override
	public void envoyer(String msg) {
		System.out.println("Début envoi message par mail, details : ");
		System.out.println("message : "+msg);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Fin envoi message par mail");
		
	}

}
