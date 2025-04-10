package com.formation.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formation.config.WebSecurityConfigTest;
import com.formation.domain.Duplicata;
import com.formation.dto.DuplicataDto;
import com.formation.repository.DuplicataRepository;
import com.formation.service.DuplicataService;

@Import(WebSecurityConfigTest.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // chargetout le contexte Spring et démarre l'application sur un port aléatoire
public class DuplicataEndpointsIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private DuplicataRepository duplicataRepository;

	@Autowired
	private DuplicataService duplicataService;

	private static HttpHeaders headers;

	private final ObjectMapper objectMapper = new ObjectMapper();

	//@BeforeAll
	public static void init() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}
	private String generateUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
	@Sql(statements = "INSERT INTO duplicata(id, user_id, montant, pdf_url) VALUES ('1110', 'Leonidas', 7500, 'https://pdf.com')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM duplicata WHERE user_id='Leonidas'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testListDuplicatas() {
		headers = new HttpHeaders();
		
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<List<Duplicata>> response = restTemplate.exchange(
				generateUrl()+"/listDuplicatas", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Duplicata>>(){});
		List<Duplicata> duplicatasLit = response.getBody();
		
		assert duplicatasLit != null;
		assertEquals(response.getStatusCodeValue(), 200);
		assertEquals(duplicatasLit.size(), duplicataService.listDuplicatas().size()); // la liste renvoyée par l'appel du endpoint doit correspondre à la liste qu'on aura si on appelait la couche service directement
		assertEquals(duplicatasLit.size(), duplicataRepository.findAll().size());
	}
	
	@Test
    @Sql(statements = "DELETE FROM duplicata WHERE user_id='Leonidas'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreateOrder() throws JsonProcessingException {
		
		DuplicataDto duplicataDto = new DuplicataDto();
		duplicataDto.setMontant(2000);
		duplicataDto.setUserId("Leonidas");
		
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
    	
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(duplicataDto), headers);
        ResponseEntity<Duplicata> response = restTemplate.exchange(
        		generateUrl()+"/duplicatas_dto", HttpMethod.POST, entity, Duplicata.class);
        
        assertEquals(response.getStatusCodeValue(), 200);
        
        Duplicata duplicata = Objects.requireNonNull(response.getBody());
        assertEquals(duplicata.getUserId(), "FR_Leonidas");
        assertEquals(duplicata.getUserId(), duplicataRepository.save(duplicata).getUserId());
    }
}