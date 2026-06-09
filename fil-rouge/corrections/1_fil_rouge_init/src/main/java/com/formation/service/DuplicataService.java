package com.formation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.formation.domain.Duplicata;
import com.formation.domain.User;

@Service
public class DuplicataService {
	
	private List<Duplicata> duplicatas = new ArrayList<>();
	
	private UserService userService;
	
	private String cdnUrl;
	
	public DuplicataService(UserService userService, @Value("${cdn.url}") String cdnUrl) {
		this.userService = userService;
		this.cdnUrl = cdnUrl;
	}
	
	public List<Duplicata> getDuplicatas() {
		return duplicatas;
	}
	
	public Duplicata createDuplicata(String userId, int montant) {
		
		String pdfUrl = cdnUrl + "/pdfs/dummy.pdf";
		
		User user = userService.findById(userId);
		if(user == null) {
			throw new IllegalArgumentException("User not found with id: " + userId);
		}
		
		Duplicata duplicata = new Duplicata();
		duplicata.setId("dup-" + System.currentTimeMillis());
		duplicata.setUserId(userId);
		duplicata.setMontant(montant);
		duplicata.setPdfUrl(pdfUrl);
		
		duplicatas.add(duplicata);
		return duplicata;
	}

}