package com.formation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.formation.domain.Duplicata;
import com.formation.domain.User;
import com.formation.exception.DuplicataNotFoundException;
import com.formation.repository.DuplicataRepository;
import com.formation.websocket.DuplicataWebSocketNotifier;

@ExtendWith(MockitoExtension.class)
class DuplicataServiceTest {

    @Mock
    private DuplicataRepository duplicataRepository;

    @Mock
    private UserService userService;

    @Mock
    private DuplicataWebSocketNotifier webSocketNotifier;

    @InjectMocks
    private DuplicataService duplicataService;

    @Test
    void createDuplicata_doitSauvegarderEtNotifier() {
        ReflectionTestUtils.setField(duplicataService, "cdnUrl", "https://cdn.test.impots");

        User user = new User();
        user.setId("u1");
        user.setName("Utilisateur de test");

        when(userService.findById("u1")).thenReturn(user);
        when(duplicataRepository.save(any(Duplicata.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Duplicata resultat = duplicataService.createDuplicata("u1", 2500);

        assertThat(resultat.getId()).startsWith("dup-");
        assertThat(resultat.getUserId()).isEqualTo("u1");
        assertThat(resultat.getMontant()).isEqualTo(2500);
        assertThat(resultat.getPdfUrl()).isEqualTo("https://cdn.test.impots/pdfs/dummy.pdf");

        ArgumentCaptor<Duplicata> duplicataCaptor = ArgumentCaptor.forClass(Duplicata.class);
        verify(duplicataRepository).save(duplicataCaptor.capture());
        verify(webSocketNotifier).notifierCreation(resultat);
        assertThat(duplicataCaptor.getValue().getUserId()).isEqualTo("u1");
    }

    @Test
    void getById_doitLeverUneExceptionQuandLeDuplicataEstIntrouvable() {
        when(duplicataRepository.findById("inconnu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> duplicataService.getById("inconnu"))
                .isInstanceOf(DuplicataNotFoundException.class)
                .hasMessageContaining("inconnu");
    }
}
