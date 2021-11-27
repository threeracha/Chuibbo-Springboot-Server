package com.sriracha.ChuibboServer.batch.schedulers;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final Job job;
    private final JobLauncher jobLauncher;

    @Scheduled(fixedDelay = 10 * 60 * 1000L) // 10분 마다 실행
    public void executeJob () {
        try {
            jobLauncher.run(
                    job,
                    new JobParametersBuilder()
                            .addString("datetime", LocalDateTime.now().toString())
                            .toJobParameters()  // job parameter 설정
            );
        } catch (JobExecutionException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }
}
