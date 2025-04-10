package com.formation.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DuplicataStatusChecker {
	
	@Autowired
	ObjectMapper objectMapper;
	
	
	//@Scheduled(fixedDelay = 3000)
	public void sendMailWhenDuplicataIsLow() throws JsonMappingException, JsonProcessingException {
		
		RestTemplate restTemplate = new RestTemplate();
		
		try {
			ResponseEntity<HealthData>  serviceResponse = 
					restTemplate.getForEntity("http://localhost:8080/actuator/health", HealthData.class);
			HealthData data = serviceResponse.getBody();
			
			System.out.println("Health status is : "+data.getStatus());
		
		} catch(HttpServerErrorException e) {
			
			HealthData data = objectMapper.readValue(e.getResponseBodyAsString(), HealthData.class);
			System.out.println("Health status is : "+data.getStatus());
			System.out.println("Sending alert notification with Data : "+e.getResponseBodyAsString());
		}	
		
	}

}