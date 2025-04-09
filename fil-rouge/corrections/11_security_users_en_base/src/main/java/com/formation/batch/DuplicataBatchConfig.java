package com.formation.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.formation.domain.Duplicata;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class DuplicataBatchConfig {
	
	private JobRepository jobRepository;
	private PlatformTransactionManager transactionManager;
	private EntityManagerFactory entityManagerFactory;
	
//	@Autowired
//	ItemProcessorMoreThan4500 itemProcessorMoreThan4500;
//	
	public DuplicataBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			EntityManagerFactory entityManagerFactory) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.entityManagerFactory = entityManagerFactory;
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(10);
		return asyncTaskExecutor;
	}

	
	@Bean
	public FlatFileItemReader<Duplicata> reader() {
		FlatFileItemReader<Duplicata> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/duplicatas.csv"));
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
		lineTokenizer.setNames("id", "userId", "montant", "pdfurl");

		BeanWrapperFieldSetMapper<Duplicata> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Duplicata.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;

	}
	
	@Bean
	public ItemProcessor<Duplicata,Duplicata> processorMoreThan4500(){
		return ((duplicata) -> {
			if(duplicata.getMontant()> 4500) {
	            return duplicata;
	        }else{
	            return null;
	        }
		});
	}
	
	@Bean
	public ItemProcessor<Duplicata,Duplicata> processorMultiThread(){
		return ((duplicata) -> {
	       System.out.println(" duplicata en cours : "+duplicata.toString());
	       return duplicata;
		});
	}
	
	
	
	@Bean
	public JpaItemWriter<Duplicata> writer() {
		 // ItemWriter adapté à JPA; utiliser le writer adapté à la destination des données
		 JpaItemWriter<Duplicata> jpaItemWriter = new JpaItemWriter<>();
		    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
		    return jpaItemWriter;
	}
	
	@Bean
	public ItemWriter<Duplicata> writerMultiThread() {
		return new ItemWriter<Duplicata>() {

			@Override
			public void write(Chunk<? extends Duplicata> duplicata) throws Exception {
				System.out.println(("Ecriture duplicata "+duplicata.toString()));
			}
			
		};
	}
	
	// declaration d'un step : ce step va permettre d'importer les duplicatas dont le montant est superieur à 4500 en BD à partir du csv
	@Bean
	public Step stepImportMoreThan4500() {
		return new StepBuilder("step-csv-import", jobRepository)
				.<Duplicata, Duplicata>chunk(10, transactionManager) // on lit par tranche de 10 entrées dans le fichier csv
				.reader(reader()) // lire les données du fichier csv
				.processor(processorMoreThan4500()) // filtrer uniquement les duplicatas dont montant < 4500
				.writer(writer()) // sauvergarder les résultats en base de données
				.build();
	}
	
	// configuration du job (travail à faire)
	@Bean("jobFilterMoreThan4500")
	public Job jobFilterMoreThan4500() {
		return new JobBuilder("importDuplicatasMoreThan4500", jobRepository)
				.flow(stepImportMoreThan4500()) // donner le step à executer au job
				.end()
				.build();
	}
	
	// step multi thread
	@Bean
	public Step stepMultiThread() {
		return new StepBuilder("step-multitthread", jobRepository)
				.<Duplicata, Duplicata>chunk(10, transactionManager) // on lit par tranche de 10 entrées dans le fichier csv
				.reader(reader()) // lire les données du fichier csv
				.processor(processorMultiThread()) 
				.writer(writerMultiThread()) 
				.taskExecutor(taskExecutor()) // va permettre le multithreadring 
				.build();
	}
	
	// configuration du job (travail à faire)
	@Bean("jobMultiThread")
	public Job jobMultiThread() {
		return new JobBuilder("jobMultiThread", jobRepository)
				.flow(stepMultiThread()) // donner le step à executer au job
				.end()
				.build();
	}
	
	

}
