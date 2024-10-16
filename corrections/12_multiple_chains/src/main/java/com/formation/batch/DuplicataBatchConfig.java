package com.formation.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.formation.batch.tasklet.Tache1;
import com.formation.batch.tasklet.Tache2;
import com.formation.batch.tasklet.Tache3;
import com.formation.domain.Duplicata;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class DuplicataBatchConfig {
	
	private JobRepository jobRepository;
	private PlatformTransactionManager transactionManager;
	private EntityManagerFactory entityManagerFactory;
	
	@Autowired
	ProcessorMultithread processorMultithread;
	
	public DuplicataBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			EntityManagerFactory entityManagerFactory) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.entityManagerFactory = entityManagerFactory;
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
		lineTokenizer.setNames("numeroFiscal", "montant", "pdfUrl", "annee");

		// mapping entre une ligne et un objet de type Duplicata
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
	public JpaItemWriter<Duplicata> writer() {
		 // ItemWriter adapté à JPA
		 JpaItemWriter<Duplicata> jpaItemWriter = new JpaItemWriter<>();
		    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
		    return jpaItemWriter;
	}
	
	
	@Bean
	public Step stepImportMoreThan4500() {
		return new StepBuilder("step-csv-import", jobRepository)
				.<Duplicata, Duplicata>chunk(10, transactionManager)
				.reader(reader())
				.processor(processorMoreThan4500())
				.writer(writer())
				.build();
	}
	
	@Bean("jobFilterMoreThan4500")
	public Job jobFilterMoreThan4500() {
		return new JobBuilder("importDuplicatasMoreThan4500", jobRepository)
				.flow(stepImportMoreThan4500())
				.end()
				.build();
	}
	
	
	/////////////////////////// config pour le job multithread /////////////////////
	
	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(10);
		return asyncTaskExecutor;
	}
	 
	
	// writer pour le job multithreadé
		@Bean
		public ItemWriter<Duplicata> writerMultithread(){
			return new ItemWriter<Duplicata>() {
				@Override
				public void write(Chunk<? extends Duplicata> chunk) throws Exception {
					System.out.println("Writing Duplicata : "+chunk.toString());
				}
			};
		}
		
		// step pour le job multithread
		@Bean
		public Step stepMultiThread() {
			return new StepBuilder("step-multipthread", jobRepository)
					.<Duplicata, Duplicata>chunk(10, transactionManager)
					.reader(reader())
					.processor(processorMultithread)
					.writer(writerMultithread()) 
					.taskExecutor(taskExecutor()) //// déclenche le multithreading
					.build();
		}
		
		@Bean("jobMultithread")
		public Job jobMultithread() {
			return new JobBuilder("jobMultithread", jobRepository)
					.flow(stepMultiThread())
					.end()
					.build();
		}
		
		///////////////// config pour le job de parallélisation des steps ///////////////////////
		
		@Autowired
		Tache1 tache1;
		
		@Autowired
		Tache2 tache2;
		
		@Autowired
		Tache3 tache3;
		
		@Bean
		public Flow flow1() {
			return new FlowBuilder<Flow>("flow1")
					.start(new StepBuilder("step1", jobRepository)
							.tasklet(tache1, transactionManager)
							.build())
					.build();
		}
		
		@Bean
		public Flow flow2() {
			return new FlowBuilder<Flow>("flow2")
					.start(new StepBuilder("step2", jobRepository)
							.tasklet(tache2, transactionManager)
							.build())
					.build();
		}
		
		@Bean
		public Flow flow3() {
			return new FlowBuilder<Flow>("flow3")
					.start(new StepBuilder("step3", jobRepository)
							.tasklet(tache3, transactionManager)
							.build())
					.build();
		}
		
		@Bean("jobParallel")
		public Job jobParallel() {
			return new JobBuilder("jobParallel", jobRepository)
					.start(flow1())
					.split(new SimpleAsyncTaskExecutor()).add(flow2(), flow3()).build()
					.build();
		}
		
		

}
