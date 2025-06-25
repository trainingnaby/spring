
package com.example.bibliotheque.jpa.repository;

import com.example.bibliotheque.entity.Critique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CritiqueRepository extends JpaRepository<Critique, Long> {
    List<Critique> findByLivreId(Long livreId);
}
