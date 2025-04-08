package com.formation.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.formation.domain.Duplicata;
import com.formation.domain.User;

@Service
@Scope("prototype")
public class DuplicataService {
	
	private List<Duplicata> duplicatas = new CopyOnWriteArrayList<Duplicata>();
	
	
	private UserService userService;
	
	private String cdnUrl;
	
	public List<Duplicata> getDuplicatas(){
		return duplicatas;
	}
	
	@Autowired
	public DuplicataService(UserService userService, @Value("${cdn.url}") String cdnUrl) {
		this.userService = userService;
		this.cdnUrl = cdnUrl;
	}


	public Duplicata createDuplicata(String userId, int montant) {
		
		User user_in_db = userService.findById(userId);
		if(user_in_db == null) {
			throw new IllegalArgumentException();
		}
		
		String randomDuplicataId = UUID.randomUUID().toString();
		
		Duplicata duplicata = new Duplicata();
		duplicata.setId(randomDuplicataId);
		duplicata.setUserId(userId);
		duplicata.setMontant(montant);
		duplicata.setPdfUrl("https://"+cdnUrl+"/sites/frilame/files/produit-doc/dummy.pdf");
		
		duplicatas.add(duplicata);
		
		return duplicata;
	}

}
