package com.formation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.formation.domain.Duplicata;

@Repository
public interface DuplicataRepository extends JpaRepository<Duplicata, String> {

	Optional<Duplicata> findById(String id);

}