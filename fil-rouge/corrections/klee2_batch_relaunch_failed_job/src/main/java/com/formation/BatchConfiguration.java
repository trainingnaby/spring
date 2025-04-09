package com.formation;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfiguration {

    private static boolean simulerErreur = false;

    @Bean
    public Job job(JobRepository jobRepository, Step step1, JobExecutionListener listener) {
        return new JobBuilder("myJob", jobRepository)
                .start(step1)
               // .listener(listener) // Ajout du listener pour relancer le job en cas d'err
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!simulerErreur) {
                        simulerErreur = true;
                        throw new RuntimeException("Erreur lancée pour tester le restart du job");
                    }
                    System.out.println("Après le restart du job, la tâche s'est bien exécutée");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
    
}
