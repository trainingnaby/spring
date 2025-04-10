package com.formation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.formation.domain.Duplicata;
import com.formation.repository.DuplicataRepository;

@ExtendWith(MockitoExtension.class) // pas besoin d'utiliser le runner de Spring : test sur une classe java simple avec des dépendances
public class DuplicataServiceTest {
	
	@Mock // utilisation mocks de Mockito
	DuplicataRepository duplicataRepository;
	
	@InjectMocks // initialise la classe à tester et injecter les mocks
	DuplicataService duplicataService ;
	
	@Test
	public void testListDuplicatas() throws Exception {
		
		// Given
		
		Duplicata duplicata1 = new Duplicata();
		duplicata1.setId("luc");
		duplicata1.setMontant(2000);
		duplicata1.setPdfUrl("https://pdf.url");
		duplicata1.setUserId("luc");
		
		Duplicata duplicata2 = new Duplicata();
		duplicata2.setId("andre");
		duplicata2.setMontant(3000);
		duplicata2.setPdfUrl("https://pdf.url");
		duplicata2.setUserId("andre");
		
		
		when(duplicataRepository.findAll()).thenReturn(Arrays.asList(duplicata1, duplicata2));
		
		// When
		List<Duplicata> listDuplicatas = duplicataService.listDuplicatas();
		
		// Then
		assertEquals(listDuplicatas.size(), 2);
		assertEquals(listDuplicatas.get(0).getId(), "luc");
		assertEquals(listDuplicatas.get(1).getId(), "andre");
		assertEquals(listDuplicatas.get(0).getMontant(), 2000);
		assertEquals(listDuplicatas.get(1).getMontant(), 3000);
		
	}
	
	@Test
	public void testCreateDuplicar_ta_params() throws Exception {
		
		Duplicata duplicata1 = new Duplicata();
		duplicata1.setId("luc");
		duplicata1.setMontant(2000);
		duplicata1.setPdfUrl("https://pdf.url");
		duplicata1.setUserId("luc");
		
		
		when(duplicataRepository.save(any(Duplicata.class))).thenReturn(duplicata1);
		Duplicata savedDuplicata = duplicataService.createDuplicata("toto", 2000);
		
	    assertEquals(savedDuplicata.getUserId(), "toto");
	    assertEquals(savedDuplicata.getMontant(), 2000);
		
	}

}