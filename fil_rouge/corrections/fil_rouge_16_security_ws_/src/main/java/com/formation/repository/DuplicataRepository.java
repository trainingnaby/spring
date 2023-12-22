package com.formation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.formation.domain.Duplicata;

@Repository
public interface DuplicataRepository extends JpaRepository<Duplicata, String> {

	// Auto génération de la méthode associée rien qu'avec le nom 
	Optional<Duplicata> findById(String id);
	
	
//	// auto génération 
//	List<Duplicata> findByMontantGreaterThan(int montant);
//	
//	List<Duplicata> findMontantBetween(int montant1, int montant2);
//	
//	Duplicata findByIdAndUserId(String id, String userId);
	
	

}