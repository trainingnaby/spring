package com.formation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.formation.domain.Users;

public interface UsersRepository extends JpaRepository<Users, String> {
	
	public  Users findByUsername(String username);

}
