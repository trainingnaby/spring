package com.formation.service;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.formation.domain.Duplicata;
import com.formation.repository.DuplicataRepository;

@Component
public class DuplicataService {

	private UserService userService;
	
	private DuplicataRepository duplicataRepository;


	private String cdnUrl = "https://cdn.dev.impots";

	public DuplicataService(UserService userService, DuplicataRepository duplicataRepository) {
		this.duplicataRepository = duplicataRepository;
		this.userService = userService;
	}

	@Transactional
	public Duplicata createDuplicata(String userId, int montant) {
		System.out.println("Une transaction est t-il en cours? = " + TransactionSynchronizationManager.isActualTransactionActive());
		String generatedPdfUrl = cdnUrl + "/images/default/sample.pdf";
		
		String random_id = UUID.randomUUID().toString();

		Duplicata duplicata = new Duplicata();
		duplicata.setId(random_id);
		duplicata.setPdfUrl(generatedPdfUrl);
		duplicata.setMontant(montant);
		duplicata.setUserId(userId);
		
		// utilisation de la méthode auto générée save
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
	
	public List<Duplicata> findAllBySorting() {
		return duplicataRepository.findAll(Sort.by("montant"));
	}
	
	
}