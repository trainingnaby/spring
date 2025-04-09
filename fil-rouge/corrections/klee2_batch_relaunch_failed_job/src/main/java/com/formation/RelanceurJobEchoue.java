package com.formation;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// exemple de scheduler pour relancer un job échoué
@Component
public class RelanceurJobEchoue {

    @Autowired
    private JobOperator jobOperator;

    @Scheduled(fixedDelay = 15000) // toutes les 10 secondes
    public void retryFailedJobs() throws Exception {
    	try {
			// Récupérer les instances de job échouées
			for (Long instanceId : jobOperator.getJobInstances("myJob", 0, 5)) {
				for (Long execId : jobOperator.getExecutions(instanceId)) {
					if (jobOperator.getSummary(execId).contains("FAILED")) {
						System.out.println("Détection d'un job échoué. Relance...");
						jobOperator.restart(execId);
					}
				}
			}
		} catch (JobInstanceAlreadyCompleteException e) {
			System.err.println("Le job a déjà été exécuté avec succès.");
		}
    }
}
