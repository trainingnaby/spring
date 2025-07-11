package com.formation.repository;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.formation.domain.Duplicata;

@DataJpaTest // charge un contexte partiel associé à la couche repository avec jpa
@ExtendWith(SpringExtension.class) // utilisation du runner de Spring
public class DuplicataRepositoryTest {
	
	@Autowired
	DuplicataRepository duplicataRepository;
	
	@BeforeEach
	public void setUp() {
		

		Duplicata duplicata1 = new Duplicata();
		duplicata1.setId("naby");
		duplicata1.setMontant(3000);
		duplicata1.setPdfUrl("https://pdf.url");
		duplicata1.setUserId("naby");
		
		Duplicata duplicata2 = new Duplicata();
		duplicata2.setId("alain");
		duplicata2.setMontant(3000);
		duplicata2.setPdfUrl("https://pdf.url");
		duplicata2.setUserId("alain");
		
		duplicataRepository.save(duplicata1);
		duplicataRepository.save(duplicata2);
	}
	
	@AfterEach
	public void destroy() {
		duplicataRepository.deleteAll();
	}
	
	@Test
	public void testGetAllOrders() {
	    List<Duplicata> duplicatas = duplicataRepository.findAll();
	   assertThat(duplicatas.size()).isEqualTo(2);
	   assertThat(duplicatas.get(0).getMontant()).isNotNegative();
	   assertThat(duplicatas.get(0).getMontant()).isGreaterThan(0);
	   assertThat(duplicatas.get(0).getUserId()).isEqualTo("naby");
	}
	

}