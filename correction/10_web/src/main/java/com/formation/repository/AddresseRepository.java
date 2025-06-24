package com.formation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.formation.domain.Addresse;

public interface AddresseRepository  extends JpaRepository<Addresse,Long>{
	
}
