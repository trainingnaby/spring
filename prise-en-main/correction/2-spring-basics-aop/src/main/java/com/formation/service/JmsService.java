package com.formation.service;

import org.springframework.stereotype.Service;

@Service
public class JmsService implements EnvoiService{

	@Override
	public void envoyer(String msg) {
		System.out.println("Début envoi message par Jms, details : ");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Fin envoi message par Jms");
	}
}
