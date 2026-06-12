package com.formation.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DuplicataMvcSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void pageFront_sansAuthentification_doitRedirigerVersLogin() throws Exception {
        mockMvc.perform(get("/ui/duplicatas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void pageCreation_avecRoleUser_doitRetourner403() throws Exception {
        mockMvc.perform(get("/ui/duplicatas/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void creationFront_avecRoleAdminEtCsrf_doitRedirigerApresCreation() throws Exception {
        mockMvc.perform(post("/ui/duplicatas")
                        .with(csrf())
                        .param("userId", "u1")
                        .param("montant", "2500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/ui/duplicatas"));
    }
}
