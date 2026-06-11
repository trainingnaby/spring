package com.formation.repository.projection;

/**
 * Projection Spring Data JPA basée sur une interface.
 *
 * Spring Data ne charge que les colonnes nécessaires et construit une vue
 * légère du résultat.
 */
public interface DuplicataResumeProjection {
    String getId();
    String getUserId();
    int getMontant();
}
