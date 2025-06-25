package com.formation;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {
	
	@Bean
	public FlatFileItemReader<Person> reader(){
		return new FlatFileItemReaderBuilder<Person>()
				.name("personReader")
				.resource(new ClassPathResource("input.csv"))
				.delimited()
				.names("id", "name")
				.fieldSetMapper(new BeanWrapperFieldSetMapper<>(){{
					setTargetType(Person.class);
				}})
				.linesToSkip(1)
				.build();
	}
	
	@Bean
	public ItemWriter<Person> writer(){
		return persons -> {
			System.out.println("Chunk reçu avec " + persons.size() + " personnes ");
			for(Person p : persons) {
				System.out.println("=>" + p);
			} 
		};
	}
	
	
	@Bean
	public Step step(JobRepository jobRepository, FlatFileItemReader<Person> reader,
			ItemWriter<Person> writer, PlatformTransactionManager transactionManager) {
		
		return new StepBuilder("csv_reader-step", jobRepository)
				.<Person, Person>chunk(5, transactionManager)
				.reader(reader)
				.writer(writer)
				.build();
	}
	
	@Bean
	public Job job(JobRepository jobRepository, Step step) {
		return new JobBuilder("csv-reader-job-job", jobRepository)
				.start(step)
				.build();
	}
	

}
