
package com.example.bibliotheque.elasticsearch.repository;

import com.example.bibliotheque.entity.Livre;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivreSearchRepository extends ElasticsearchRepository<Livre, Long> {

    // Méthode dérivée pour recherche par titre
    List<Livre> findByTitre(String titre);

    // Recherche plein texte sur ISBN
    List<Livre> findByIsbn(String isbn);
    
    List<Livre> findByTitreContaining(String keyword);

}
