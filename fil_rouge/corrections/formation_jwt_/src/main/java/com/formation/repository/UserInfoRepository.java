package com.formation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.formation.entity.Utilisateur;

@Repository
public interface UserInfoRepository extends JpaRepository<Utilisateur, Integer> {
	Optional<Utilisateur> findByName(String username);
}
