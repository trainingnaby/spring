package com.formation.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.formation.domain.DuplicataDocument;

public interface DuplicataElasticRepository extends ElasticsearchRepository<DuplicataDocument, Long>{
	
	 boolean existsByMontantGreaterThan(int maxValue); 

	 int countByMontantGreaterThan(int value);

	 DuplicataDocument findByNumeroFiscal(String numeroFiscal);
	
}