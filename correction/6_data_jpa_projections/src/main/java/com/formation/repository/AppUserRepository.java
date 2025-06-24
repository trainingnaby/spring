package com.formation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.formation.domain.AppUser;
import com.formation.projection.AppUserProjection;

public interface AppUserRepository extends JpaRepository<AppUser,Long>{
	
	//select * from app_users where numero_fiscal = 'numero_fiscal'
	Optional<AppUser> findByNumeroFiscal(String numeroFiscal);
	
	// projection en utilisant une classe au lieu d'un dto
	// List<UserProjectionDto> findAllProjectedBy();
	
	// projection en utilisant une interface
	List<AppUserProjection> findAllProjectedBy();
	
	// les deux requetes precedentes sont equivalentes aux deux requetes suivantes
	@Query("SELECT u.numeroFiscal AS numeroFiscal, u.authorities AS authorities, u.username AS username, u.addresse AS addresse FROM AppUser u")
	List<AppUserProjection> getAllProjected();
	List<AppUserProjection> findAllBy();



	
	AppUserProjection findByNumeroFiscalContaining(String numeroFiscal);
	
	<T> T findByNumeroFiscal(String userName, Class<T> type);
	
}