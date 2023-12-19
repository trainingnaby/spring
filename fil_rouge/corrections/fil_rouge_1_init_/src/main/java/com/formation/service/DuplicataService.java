package com.formation.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.formation.domain.Duplicata;
import com.formation.domain.User;

@Service
public class DuplicataService {
	
	private List<Duplicata> duplicatas = new CopyOnWriteArrayList<Duplicata>(); //modélise pour le moment notre base de données
	
	@Autowired
	private UserService userService;
	
	public List<Duplicata> getDuplicatas(){
		return duplicatas;
	}
	
	public Duplicata createDuplicata(String userId, int montant) {
		//vérifier en base si le user existe
		User user_db = userService.findById(userId);
		if(user_db == null) {
			throw new IllegalArgumentException();
		}
		
		Duplicata duplicata = new Duplicata();
		duplicata.setId(userId);
		duplicata.setUserid(userId);
		duplicata.setMontant(montant);
		duplicata.setPdfurl("https://www.orimi.com/pdf-test.pdf");
		
		duplicatas.add(duplicata);
		
		return duplicata;
		
	}
	
	
	

}
