package com.formation.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler {
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	//@Qualifier("jobFilterMoreThan4500")
	private Job jobFilterMoreThan4500;
	
	
	// @Scheduled(fixedRate = 6000)
	public void scheduleImportDuplicataMoreThan4500() {
		
		JobParameters jobParameters = new JobParametersBuilder().addLong("Debut", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobFilterMoreThan4500, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}
		
	}
	
	

}
