package com.formation;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {
	
	@Bean
	public JdbcCursorItemReader<Person> reader(DataSource dataSource){
		return new JdbcCursorItemReaderBuilder<Person>()
				.name("personreader")
				.dataSource(dataSource)
				.sql("SELECT id, name FROM person")
				.rowMapper((rs, rowNum) -> {
					Person p = new Person();
					p.setId(rs.getInt("id"));
					p.setName(rs.getString("name"));
					return p;
				})
				.build();
	}
	
	@Bean
	public JdbcBatchItemWriter<Person> writer(DataSource dataSource){
		return new JdbcBatchItemWriterBuilder<Person>()
				.dataSource(dataSource)
				.sql("INSERT INTO person_archive (id, name) VALUES (:id, :name)")
				.beanMapped()
				.build();
			
	}
	
	@Bean
	public Step step(JobRepository jobRepository, JdbcCursorItemReader<Person> reader,
			JdbcBatchItemWriter<Person> writer, PlatformTransactionManager transactionManager) {
		
		return new StepBuilder("db_rw-step", jobRepository)
				.<Person, Person>chunk(2, transactionManager)
				.reader(reader)
				.writer(writer)
				.build();
	}
	
	@Bean
	public Job job(JobRepository jobRepository, Step step) {
		return new JobBuilder("db_rw-job", jobRepository)
				.start(step)
				.build();
	}
	

}
