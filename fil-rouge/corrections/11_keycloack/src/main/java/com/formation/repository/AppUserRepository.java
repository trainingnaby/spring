package com.formation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.formation.domain.AppUser;
import com.formation.projection.AppUserProjection;
import com.formation.projection.UserProjectionDto;

public interface AppUserRepository extends JpaRepository<AppUser,Long>{
	
	//select * from app_users where numero_fiscal = 'numero_fiscal'
	Optional<AppUser> findByNumeroFiscal(String numeroFiscal);
	
	List<UserProjectionDto> findAllProjectedBy();
	
	AppUserProjection findByNumeroFiscalContaining(String numeroFiscal);
	
	<T> T findByNumeroFiscal(String userName, Class<T> type);
	
	boolean existsByNumeroFiscal(String numeroFiscal);
	
	AppUser findByUsername(String username);
	
}