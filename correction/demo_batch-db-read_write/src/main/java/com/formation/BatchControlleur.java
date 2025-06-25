
package com.formation;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BatchControlleur {

	@Autowired
	private JobLauncher jobLauncher; // permet de lancer des jobs
	
	@Autowired
	private Job job;	
	
	@GetMapping("/start")
	public String startJob() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		
		// Ajouter des paramètres au job avec JobParameters
		JobParameters jobParameters = new JobParametersBuilder()
				.addDate("startAt", new Date())
				.toJobParameters();
		
		JobExecution jobExecution = jobLauncher.run(job, jobParameters);
		return "job lancé :" +jobExecution.getStatus();
	}
	
	
	
}
