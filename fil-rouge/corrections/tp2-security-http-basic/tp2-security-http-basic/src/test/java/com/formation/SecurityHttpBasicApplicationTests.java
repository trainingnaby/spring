package com.formation;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityHttpBasicApplicationTests {

    @Autowired
    MockMvc mvc;

    @Test
    void accueilEstPublic() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void clientSansAuthentificationRetourne401() throws Exception {
        mvc.perform(get("/api/client")).andExpect(status().isUnauthorized());
    }

    @Test
    void aliceAccedeEspaceClient() throws Exception {
        mvc.perform(get("/api/client").with(httpBasic("alice", "alice123")))
                .andExpect(status().isOk());
    }

    @Test
    void aliceNePeutPasAccederAdmin() throws Exception {
        mvc.perform(get("/api/admin").with(httpBasic("alice", "alice123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminAccedeAuxDeuxEspaces() throws Exception {
        mvc.perform(get("/api/admin").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
        mvc.perform(get("/api/client").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }
}
