package com.formation.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.formation.domain.Duplicata;
import com.formation.repository.projection.DuplicataResumeProjection;

/**
 * Repository Spring Data JPA.
 *
 * Dans le TP précédent, le repository contenait du SQL écrit à la main avec
 * JdbcTemplate. Ici, Spring Data JPA génère automatiquement la plupart des
 * requêtes à partir du nom des méthodes.
 */
public interface DuplicataRepository extends JpaRepository<Duplicata, String> {

    // 1) Requête dérivée : Spring Data comprend "findByUserId".
    List<Duplicata> findByUserId(String userId);

    // 2) Requête dérivée avec intervalle + tri dans le nom de méthode.
    List<Duplicata> findByMontantBetweenOrderByMontantDesc(int montantMin, int montantMax);

    // 3) Requête dérivée avec recherche textuelle insensible à la casse.
    List<Duplicata> findByUserIdContainingIgnoreCase(String morceauUserId);

    // 4) JPQL : on raisonne sur l'entité Duplicata et ses attributs Java.
    @Query("select d from Duplicata d where d.montant >= :montantMinimum order by d.createdAt desc")
    List<Duplicata> rechercherParMontantMinimum(@Param("montantMinimum") int montantMinimum);

    // 5) Projection : on renvoie une vue partielle, pas toute l'entité.
    List<DuplicataResumeProjection> findByMontantGreaterThanEqual(int montantMinimum);

    // 6) Pagination : le Pageable contient page, taille et tri.
    Page<Duplicata> findByUserIdContainingIgnoreCase(String morceauUserId, Pageable pageable);
}
