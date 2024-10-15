package com.formation.config;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.formation.domain.Duplicata;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class DuplicataBatchConfig {

	private JobRepository jobRepository;
	private PlatformTransactionManager transactionManager;
	private EntityManagerFactory entityManagerFactory;

	public DuplicataBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			EntityManagerFactory entityManagerFactory) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.entityManagerFactory = entityManagerFactory;
	}

	
	// lecture des données
	@Bean
	public FlatFileItemReader<Duplicata> reader() {

		FlatFileItemReader<Duplicata> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("/src/main/resources/duplicatas.csv"));
		itemReader.setName("csvReader");
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	private LineMapper<Duplicata> lineMapper() {
		DefaultLineMapper<Duplicata> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("numeroFiscal", "montant", "pdfUrl", "annee");

		// mapping entre une ligne et un objet de type Duplicata
		BeanWrapperFieldSetMapper<Duplicata> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Duplicata.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;

	}
	
	// régles métier du batch
	@Bean
	public ItemProcessor<Duplicata, Duplicata> processorMoreThan4500(){
		
		return ((duplicata) -> {
			if(duplicata.getMontant() > 4500) {
				return duplicata;
			} else {
				return null;
			}
		});
	}
	
	// écriture des données
	@Bean
	public JpaItemWriter<Duplicata> writer(){
		JpaItemWriter<Duplicata> jpaItemWriter = new JpaItemWriter<Duplicata>();
		jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
		return jpaItemWriter;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
