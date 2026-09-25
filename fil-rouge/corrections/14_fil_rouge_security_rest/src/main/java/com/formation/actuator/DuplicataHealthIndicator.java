package com.formation.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.formation.repository.DuplicataRepository;

/**
 * Health check metier expose automatiquement par Spring Boot Actuator.
 *
 * Le nom du bean etant "duplicataHealthIndicator", Actuator expose le composant
 * sous le nom "duplicata" dans /actuator/health et /actuator/health/duplicata.
 */
@Component
public class DuplicataHealthIndicator implements HealthIndicator {

    private static final long SEUIL_ALERTE_VOLUME = 1000;

    private final DuplicataRepository duplicataRepository;

    public DuplicataHealthIndicator(DuplicataRepository duplicataRepository) {
        this.duplicataRepository = duplicataRepository;
    }

    @Override
    public Health health() {
        try {
            long nombreDuplicatas = duplicataRepository.count();

            if (nombreDuplicatas == 0) {
                return Health.down()
                        .withDetail("message", "Aucun duplicata n'est present en base")
                        .withDetail("nombreDuplicatas", nombreDuplicatas)
                        .withDetail("action", "Verifier l'initialisation de data.sql ou generer un premier duplicata")
                        .build();
            }

            if (nombreDuplicatas > SEUIL_ALERTE_VOLUME) {
                return Health.status("DEGRADED")
                        .withDetail("message", "Le nombre de duplicatas est anormalement eleve pour le TP")
                        .withDetail("nombreDuplicatas", nombreDuplicatas)
                        .withDetail("seuilAlerteVolume", SEUIL_ALERTE_VOLUME)
                        .build();
            }

            return Health.up()
                    .withDetail("message", "Le module de generation de duplicatas est operationnel")
                    .withDetail("nombreDuplicatas", nombreDuplicatas)
                    .build();
        } catch (Exception exception) {
            return Health.down(exception)
                    .withDetail("message", "Impossible d'interroger la base des duplicatas")
                    .build();
        }
    }
}
