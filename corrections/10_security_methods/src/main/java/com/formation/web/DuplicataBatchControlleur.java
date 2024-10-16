package com.formation.web;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class DuplicataBatchControlleur {
	
	@Autowired
	private JobLauncher jobLauncher;
	
	// Chercher dans le contexte le job à lancer
	@Autowired
	private Job jobFilterMoreThan4500;
	
	@Autowired
	private Job jobMultithread;
	
	@Autowired
	private Job jobParallel;
	
	@GetMapping("/importDuplicatasMoreThan4500")
	public void importDuplicatasMoreThan4500() {
		// si le job tombe, pour le relancer il faut connaitre le nom de l'exection en cours === jobParameters
		JobParameters jobParameters = new JobParametersBuilder().addLong("Debut", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobFilterMoreThan4500, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/jobMultithread")
	public void jobMultithread() {
		JobParameters jobParameters = new JobParametersBuilder().addLong("Debut", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobMultithread, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/jobParallel")
	public void jobParallel() {
		JobParameters jobParameters = new JobParametersBuilder().addLong("Debut", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobParallel, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}
	}

}
