package com.formation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.formation.domain.Duplicata;

@Repository
public interface DuplicataRepository extends JpaRepository<Duplicata,String>{
	
	boolean existsByMontantGreaterThan(int maxValue); 

	public int countByMontantGreaterThan(int value); 
	
}