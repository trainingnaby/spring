package com.formation.service;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import com.formation.domain.Duplicata;
import com.formation.domain.DuplicataDocument;
import com.formation.exception.DuplicataException;
import com.formation.repository.DuplicataElasticRepository;
import com.formation.repository.DuplicataRepository;

@Service 
public class DuplicataService {

	@Autowired
	DuplicataRepository duplicataRepository;
	
	@Autowired
	DuplicataElasticRepository elasticRepository;
	
	public List<Duplicata> listDuplicatas() {
		return duplicataRepository.findAll();
	}
	
	public void supprimerDuplicata(Duplicata duplicata) {
		duplicataRepository.delete(duplicata);
	}
	
	public Duplicata getDuplicata(long id) throws DuplicataException {
		Optional<Duplicata> byId = duplicataRepository.findById(id);
		if(byId.isPresent()) {
			return byId.get();
		}
		throw new DuplicataException("Duplicata not with id "+id+" not found !");
	}
	
	public Duplicata createDuplicata(String numeroFiscal, int montant, int annee) {
		
		Duplicata createdDuplicata = new Duplicata();
		
		createdDuplicata.setAnnee(Year.of(annee));
		createdDuplicata.setMontant(montant);
		createdDuplicata.setNumeroFiscal(numeroFiscal);
		createdDuplicata.setPdfUrl("https://pdf.com");
		Duplicata saveDuplicata = duplicataRepository.save(createdDuplicata);
		
		DuplicataDocument document = new DuplicataDocument();
		document.setId(saveDuplicata.getId());
		document.setAnnee(saveDuplicata.getAnnee());
		document.setMontant(saveDuplicata.getMontant());
		document.setNumeroFiscal(saveDuplicata.getNumeroFiscal());
		document.setPdfUrl(saveDuplicata.getPdfUrl());
		
		//persister aussi ElasticSearch
		elasticRepository.save(document);
		
		return saveDuplicata;
		
	}
	
	public Page<Duplicata> listDuplicatasPageable(Pageable pageable) { 
		return duplicataRepository.findAll(pageable);
	}
	
	public List<Duplicata> listDuplicatasSorted() {  
		return duplicataRepository.findAll(Sort.by("montant"));
	}
	
	public boolean existsGreaterThan (int montant) {  
		return duplicataRepository.existsByMontantGreaterThan(montant);
	}

	public Optional<DuplicataDocument> getDocumentById(long id) {
		return elasticRepository.findById(id);
	}

	public Iterable<DuplicataDocument> getDocuments() {
		return elasticRepository.findAll();
	}

}
