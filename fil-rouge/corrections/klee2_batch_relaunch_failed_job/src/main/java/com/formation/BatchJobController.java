package com.formation;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batch")
public class BatchJobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @PostMapping("/start")
    public String startJob(@RequestParam(required = false) String param) {
        try {
            JobParametersBuilder paramsBuilder = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis());

            if (param != null) {
                paramsBuilder.addString("paramJob", param);
            }

            JobExecution execution = jobLauncher.run(job, paramsBuilder.toJobParameters());
            return "Job lancé avec status : " + execution.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors du lancement du job : " + e.getMessage();
        }
    }
}
