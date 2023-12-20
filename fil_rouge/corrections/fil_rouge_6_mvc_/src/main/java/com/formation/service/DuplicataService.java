package com.formation.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.formation.annotation.MonitorerTempsExecution;
import com.formation.domain.Duplicata;
import com.formation.domain.User;

@Service
@Scope("prototype")
public class DuplicataService {
	
	private List<Duplicata> duplicatas = new CopyOnWriteArrayList<Duplicata>(); //modélise pour le moment notre base de données
	
	@Autowired
	private UserService userService;
	
	private String cdnUrl;
	
	public DuplicataService(UserService userService, 
			@Value("${cdn.url}") String cdnUrl) {
		this.userService = userService;
		this.cdnUrl = cdnUrl;
	}

	@MonitorerTempsExecution
	public List<Duplicata> getDuplicatas(){
		return duplicatas;
	}
	
	@MonitorerTempsExecution
	public Duplicata createDuplicata(String userId, int montant) {
		//vérifier en base si le user existe
		User user_db = userService.findById(userId);
		if(user_db == null) {
			throw new IllegalArgumentException();
		}
		
		Duplicata duplicata = new Duplicata();
		duplicata.setId(userId);
		duplicata.setUserId(userId);
		duplicata.setMontant(montant);
		duplicata.setPdfurl(cdnUrl + "/pdf-test.pdf");
		
		duplicatas.add(duplicata);
		
		return duplicata;
		
	}
	
	
	

}
