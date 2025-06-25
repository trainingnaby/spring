package com.formation;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {
	
	// définir le itemReader (lit les données dans la source sur la liste)
	@Bean
	public ItemReader<String> reader(){
		List<String> data = Arrays.asList("toto", "titi", "tata", "tutu");
		return new ListItemReader<>(data);
	}
	
	// définit le processor : transforme les données lues par le reader
	@Bean
	public UpperCaseProcessor processor() {
		return new UpperCaseProcessor();
	}
	
	// définit le writer :  écrit les données lues par le reader et tranbsformées par le procressor
	@Bean
	public ListItemWriter<String> writer(){
		return new ListItemWriter<String>();
	}
	
	// définition du step
	@Bean 
	public Step step(ItemReader<String> reader, UpperCaseProcessor processor,
			ItemWriter<String> writer, PlatformTransactionManager transactionManager, 
			JobRepository jobRepository) {
		
		return new StepBuilder("helloStep", jobRepository)
				.<String, String>chunk(2, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
	
	
	// définition du job
	@Bean
	public Job job(Step step, JobRepository jobRepository) {
		return new JobBuilder("helloJob", jobRepository)
				.start(step)
				.build();
	}
	
	

}
