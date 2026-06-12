package com.formation.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DuplicataRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getDuplicatas_sansToken_doitRetourner401() throws Exception {
        mockMvc.perform(get("/duplicatas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Authentification requise"));
    }

    @Test
    void getDuplicatas_avecTokenUser_doitRetourner200() throws Exception {
        String token = obtenirToken("user", "user");

        mockMvc.perform(get("/duplicatas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void creerDuplicata_avecRoleUser_doitRetourner403() throws Exception {
        String token = obtenirToken("user", "user");

        mockMvc.perform(post("/duplicatas_dto")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"u1\",\"montant\":2500}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Acces refuse"));
    }

    @Test
    void creerDuplicata_avecRoleAdmin_doitRetourner200() throws Exception {
        String token = obtenirToken("admin", "admin");

        mockMvc.perform(post("/duplicatas_dto")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"u1\",\"montant\":2500}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.montant").value(2500));
    }

    private String obtenirToken(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }
}
