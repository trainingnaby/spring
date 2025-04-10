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
		
		// flush le cache aprés une création
		flushCacheDuplicatas();
		
		return duplicata;

	}

	@Transactional
	@Cacheable(value = "cache_liste_duplicatas", unless = "#result==null or #result.size()==0")
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
	
	@CacheEvict(value = "cache_liste_duplicatas", allEntries = true)
	public void flushCacheDuplicatas() {}
	
	// @Cacheable vs @CachePut
	// @Cacheable => execute une seule fois la méthode et met le résulat en cache 
	// @cachePut => exécute toujours la méthode et met le resulat dans le cache : A utiliser dans les méthodes qui font des mises à jours de données
	
//	@Cacheable("duplicata")
//	public Duplicata getDuplicataById(String id) {
//		return duplicataRepository.findById(id);
//	}
	
//	@CachePut("duplicata") // chaque fois qu'une personne modifie le duplicata avec l'id, on vide le cache associé et on met le nouveau duplicata dans le cache
//	public Duplicata updateDuplicata(String id) {
//		// mise à jour de données
//		return duplicataRepository.save(duplicata);
//	}
	
	// cache duplicata => ["toto" : duplicataToto, "naby": Duplicata]
	
}