package com.formation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.formation.domain.Duplicata;
import com.formation.repository.projection.DuplicataResumeProjection;

@DataJpaTest
@ActiveProfiles("test")
class DuplicataRepositoryTest {

    @Autowired
    private DuplicataRepository duplicataRepository;

    @Test
    void findByUserId_doitRetournerLesDuplicatasDeLUtilisateur() {
        Duplicata duplicata = new Duplicata();
        duplicata.setId("dup-test-dao-001");
        duplicata.setUserId("dao-user");
        duplicata.setMontant(3200);
        duplicata.setPdfUrl("https://cdn.test.impots/pdfs/dummy.pdf");
        duplicata.setCreatedAt(LocalDateTime.now());
        duplicataRepository.saveAndFlush(duplicata);

        List<Duplicata> resultats = duplicataRepository.findByUserId("dao-user");

        assertThat(resultats).hasSize(1);
        assertThat(resultats.get(0).getMontant()).isEqualTo(3200);
    }

    @Test
    void projection_doitRetournerSeulementLeResumeDuDuplicata() {
        List<DuplicataResumeProjection> projections = duplicataRepository.findByMontantGreaterThanEqual(1000);

        assertThat(projections).isNotEmpty();
        assertThat(projections.get(0).getId()).isNotBlank();
        assertThat(projections.get(0).getUserId()).isNotBlank();
    }
}
