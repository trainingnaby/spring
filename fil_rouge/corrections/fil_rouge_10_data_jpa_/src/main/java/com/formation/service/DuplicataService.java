package com.formation.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.formation.domain.Duplicata;
import com.formation.repository.DuplicataRepository;

@Component
public class DuplicataService {

	private UserService userService;

	@Autowired
	private DuplicataRepository duplicataRepository;

	private String cdnUrl;

	public DuplicataService(UserService userService, 
			@Value("${cdn.url}") String cdnUrl) {
		this.userService = userService;
		this.cdnUrl = cdnUrl;
	}

	@Transactional
	public Duplicata createDuplicata(String userId, int montant) {
		System.out.println("Une transaction est t-il en cours? = " + TransactionSynchronizationManager.isActualTransactionActive());

		String random_userId = UUID.randomUUID().toString();

		Duplicata duplicata = new Duplicata();
		
		duplicata.setId(random_userId);
		duplicata.setPdfurl(cdnUrl+"/monpdf.pdf");
		duplicata.setMontant(montant);
		duplicata.setUserId(userId);
		
		//appel de duplicataRepository pour sauvegarder en base de données
		duplicataRepository.save(duplicata);
		
		return duplicata;

	}

	@Transactional
	public List<Duplicata> listDuplicatas() {
		System.out.println("Une transaction est t-il en cours? = " + TransactionSynchronizationManager.isActualTransactionActive());
		
		List<Duplicata> allDuplicatas = duplicataRepository.findAll();
		
		return allDuplicatas;
		
	}

}
