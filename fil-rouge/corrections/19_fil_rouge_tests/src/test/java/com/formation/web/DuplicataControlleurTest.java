package com.formation.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.formation.domain.Duplicata;
import com.formation.security.JwtAuthenticationFilter;
import com.formation.service.DuplicataService;

@WebMvcTest(controllers = DuplicataControlleur.class)
@AutoConfigureMockMvc(addFilters = false)
class DuplicataControlleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DuplicataService duplicataService;

    // Ces deux mocks évitent de charger la vraie sécurité dans ce test de contrôleur.
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void duplicatas_doitRetournerLaListeAuFormatJson() throws Exception {
        Duplicata duplicata = new Duplicata();
        duplicata.setId("dup-test-controller-001");
        duplicata.setUserId("u1");
        duplicata.setMontant(2500);
        duplicata.setPdfUrl("https://cdn.test.impots/pdfs/dummy.pdf");
        duplicata.setCreatedAt(LocalDateTime.now());

        when(duplicataService.getDuplicatas()).thenReturn(List.of(duplicata));

        mockMvc.perform(get("/duplicatas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("dup-test-controller-001"))
                .andExpect(jsonPath("$[0].userId").value("u1"))
                .andExpect(jsonPath("$[0].montant").value(2500));
    }
}
