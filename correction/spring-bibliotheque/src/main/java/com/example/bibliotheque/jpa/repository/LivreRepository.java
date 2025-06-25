
package com.example.bibliotheque.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.bibliotheque.entity.Livre;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {

    // Méthode dérivée auto-générée
    List<Livre> findByTitreContainingIgnoreCase(String titre);

    // JPQL pour trouver les livres publiés après une année donnée
    @Query("SELECT l FROM Livre l WHERE l.datePublication > :year")
    List<Livre> findRecentLivres(int year);

    // SQL natif pour récupérer les livres non archivés
    @Query(value = "SELECT * FROM Livre WHERE archive = false", nativeQuery = true)
    List<Livre> findAllNonArchived();
    
    Optional<Livre> findByIsbn(String isbn);
    
    
    List<Livre> findByAuteurId(Long auteurId);

    List<Livre> findAllByOrderByAuteurNomAsc();

    List<Livre> findAllByOrderByDatePublicationAsc();

    List<Livre> findAllByOrderByCategorieNomAsc();
    
    
}
