package com.formation.actuator;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.formation.repository.DuplicataRepository;

@Component
public class DuplicataProbe implements HealthIndicator {

	@Autowired
	private final DuplicataRepository duplicataRepository;
	
	public DuplicataProbe(DuplicataRepository duplicataRepository) {
		this.duplicataRepository =  duplicataRepository;
	}
	
	private static final int SEUIL = 2;


	@Override
	public Health health() {
		
		// compter le nombre de duplicatas en base
		long totalDuplicatas = duplicataRepository.count();
		
		// map pour avoir plus d'informations
		Map<String,String> statusCheckDuplicatas = new HashMap<>();
		
		if(totalDuplicatas <= SEUIL) {
			statusCheckDuplicatas.put("CheckTotalDuplicatas" , "Le nombre de duplicatas est trop bas !  Nombre actuel " + totalDuplicatas);
			return Health.down().withDetails(statusCheckDuplicatas).build();
		} else {
			statusCheckDuplicatas.put("TotalDuplicatasOk" , "Le nombre de duplicatas est correct  Nombre actuel " + totalDuplicatas);
			return Health.up().withDetail("CheckTotalDuplicatas", statusCheckDuplicatas).build();
		}
		
	}
}
