package com.formation.web;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formation.config.WebSecurityConfigTest;
import com.formation.domain.Duplicata;
import com.formation.dto.DuplicataDto;
import com.formation.service.DuplicataService;

@Import(WebSecurityConfigTest.class) // bean pour whitelister les urls spring security
@ExtendWith(SpringExtension.class) // charger le runner de Spring
@WebMvcTest(DuplicataControlleur.class) // permet d’initialiser un contexte partiel
public class DuplicataControlleurTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DuplicataService duplicataService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testListDuplicatas() throws Exception {
		
		// setup 
		Duplicata duplicata1 = new Duplicata();
		duplicata1.setId("AA");
		duplicata1.setMontant(3000);
		duplicata1.setPdfUrl("https://pdf.url");
		duplicata1.setUserId("AAB");
		
		Duplicata duplicata2 = new Duplicata();
		duplicata2.setId("AA");
		duplicata2.setMontant(3000);
		duplicata2.setPdfUrl("https://pdf.url");
		duplicata2.setUserId("AAB");
		
		List<Duplicata> duplicatas = new ArrayList<Duplicata>();
		duplicatas.add(duplicata1);
		duplicatas.add(duplicata2);
		
		// définir comportement duplicataService
		when(duplicataService.listDuplicatas()).thenReturn(Arrays.asList(duplicata1, duplicata2));
		
		// appel au endpoint à tester et assertions
		mockMvc.perform(get("/listDuplicatas"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$").isArray());
		
	}
	
	@Test
	public void testCreateDuplicata_request_params() throws Exception {
		
		Duplicata duplicata1 = new Duplicata();
		duplicata1.setId("toto");
		duplicata1.setMontant(2000);
		duplicata1.setPdfUrl("https://cdn.dev.impots/images/default/sample.pdf");
		duplicata1.setUserId("toto");
		
		
		 when(duplicataService.createDuplicata(anyString(), anyInt())).thenReturn(duplicata1);
		    mockMvc.perform(
		        post("/duplicatas?user_id=toto&montant=2000"))
		        .andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		        .andExpect(jsonPath("$.id", is("toto")))
		        .andExpect(jsonPath("$.montant", is(2000)))
		        .andExpect(jsonPath("$.userId", is("toto")))
		        .andExpect(jsonPath("$").isNotEmpty());
		
		
	}
	
	@Test
	public void testCreateDuplicata_dto() throws Exception {
		
		Duplicata duplicata1 = new Duplicata();
		duplicata1.setId("naby");
		duplicata1.setMontant(3000);
		duplicata1.setPdfUrl("https://cdn.dev.impots/images/default/sample.pdf");
		duplicata1.setUserId("naby");
		
		DuplicataDto duplicataDto = new DuplicataDto();
		duplicataDto.setMontant(2000);
		duplicataDto.setUserId("naby");
		
		 when(duplicataService.createDuplicata(anyString(), anyInt())).thenReturn(duplicata1);
		    mockMvc.perform(
		        post("/duplicatas_dto")
		            .content(objectMapper.writeValueAsString(duplicataDto))
		            .contentType(MediaType.APPLICATION_JSON)
		        )
		        .andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		        .andExpect(jsonPath("$.id", is("naby")))
		        .andExpect(jsonPath("$.montant", is(3000)))
		        .andExpect(jsonPath("$.userId", is("naby")))
		        .andExpect(jsonPath("$").isNotEmpty());
		
		
	}
	
	
}	
