package com.formation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.formation.domain.Duplicata;

@Repository
public interface DuplicataRepository extends JpaRepository<Duplicata, Integer> {

	Duplicata findById(String id);

	public int countByMontantGreaterThan(int value); // select count(*) from Duplicata where montant > value

	public List<Duplicata> findAllByMontantLessThan(int montantMinimum); // select * from Duplicata montant < montantMinimum

	public List<Duplicata> findFirst10ByOrderByMontantDesc(); // récupérer les 10 premiers duplicatas aprés tri par ordre decroissant

	public List<Duplicata> findByUserIdContaining(String partialUserId); // tous les duplicatas dont le userid contient la chaine partialUserId

	boolean existsByMontantGreaterThan(int maxValue); // existe t-il des duplicatas dont le montant est superieur à maxValue

}