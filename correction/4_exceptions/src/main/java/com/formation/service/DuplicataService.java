package com.formation.service;

import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.formation.domain.Duplicata;
import com.formation.exception.DuplicataException;

@Service
public class DuplicataService {
	
	List<Duplicata> duplicatas  = new CopyOnWriteArrayList<>();
	
	public List<Duplicata> listDuplicatas(){
		return duplicatas;
	}
	
	public void supprimerDuplicata() {
		
	}
	
	public Duplicata getDuplicata(String id) throws Exception{
		for(Duplicata currentDuplicata : duplicatas) {
			if(currentDuplicata.getId().equals(id)) {
				return currentDuplicata;
			}
		}
		throw new DuplicataException("Duplica with  " + id + "not found !");
	}
	
	public Duplicata createDuplicata(String numeroFiscal, int montant, int annee) {
		Duplicata createdDuplicata = new Duplicata();
		String randomId = UUID.randomUUID().toString();
		
		createdDuplicata.setId(randomId);
		createdDuplicata.setAnnee(Year.of(annee));
		createdDuplicata.setMontant(montant);
		createdDuplicata.setNumeroFiscal(numeroFiscal);
		
		this.duplicatas.add(createdDuplicata);
		
		return createdDuplicata;
		
	}

}
