package com.formation.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.formation.domain.Duplicata;
import com.formation.repository.DuplicataRepository;

@Component
public class DuplicataService {

	private UserService userService;

	private DuplicataRepository duplicataRepository;

	private String cdnUrl;

	public DuplicataService(UserService userService, DuplicataRepository duplicataRepository, 
			@Value("${cdn.url}") String cdnUrl) {
		this.duplicataRepository = duplicataRepository;
		this.userService = userService;
		this.cdnUrl = cdnUrl;
	}

	@Transactional
	public Duplicata createDuplicata(String userId, int montant) {
		System.out.println("Une transaction est t-il en cours? = " + TransactionSynchronizationManager.isActualTransactionActive());
		
		String generatedPdfUrl = cdnUrl + "/images/default/sample.pdf";
		Duplicata duplicata = new Duplicata();
		duplicata.setPdfUrl(generatedPdfUrl);
		duplicata.setMontant(montant);
		duplicata.setUserId(userId);
		duplicata.setId(System.currentTimeMillis()+"");
		
		duplicataRepository.save(duplicata);
		
		return duplicata;

	}

	@Transactional
	public List<Duplicata> listDuplicatas() {
		System.out.println("Une transaction est t-il en cours? = " + TransactionSynchronizationManager.isActualTransactionActive());
		return duplicataRepository.findAll();
	}
	
	  public Page<Duplicata> findAllByPaging(Pageable paging) {
          return duplicataRepository.findAll(paging);
  }

}