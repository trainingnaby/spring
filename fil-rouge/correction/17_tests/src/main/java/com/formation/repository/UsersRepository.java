package com.formation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.formation.domain.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

	Users findByUsername(String username);
	
}