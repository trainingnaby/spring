package com.formation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.formation.domain.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser,String>{
	
	//select * from app_users where numero_fiscal = 'numero_fiscal'
	Optional<AppUser> findByNumeroFiscal(String numeroFiscal);
	
	
}