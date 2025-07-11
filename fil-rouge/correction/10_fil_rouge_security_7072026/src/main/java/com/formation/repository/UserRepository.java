package com.formation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.formation.domain.UserApp;

public interface UserRepository extends JpaRepository<UserApp, String> {
	
	UserApp findByUsername(String username);

}
