
package com.example.bibliotheque.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.example.bibliotheque.jpa.repository.LivreRepository;


@Component
public class BibliothequeHealthIndicator implements HealthIndicator {

    private final LivreRepository livreRepository;

    public BibliothequeHealthIndicator(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    @Override
    public Health health() {
        long availableBooks = livreRepository.findAll()
                .stream()
                .filter(l -> !l.isArchive())
                .count();

        if (availableBooks == 0) {
            return Health.down().withDetail("error", "Aucun livre disponible").build();
        }

        return Health.up().withDetail("availableBooks", availableBooks).build();
    }
}
