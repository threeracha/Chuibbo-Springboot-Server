package com.sriracha.ChuibboServer.batch.jobs;

import com.sriracha.ChuibboServer.batch.tasklets.BatchTasklet;
import com.sriracha.ChuibboServer.service.jobPost.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory; // Job 빌더 생성용
    private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성용
    private final JobPostService jobPostService;
    @Value("${saramin.access-key}")
    private String accessKey;

    // JobBuilderFactory를 통해서 Job을 생성
    @Bean
    public Job Job() {
        return jobBuilderFactory.get("Job")
                .start(Step())  // Step 설정
                .build();
    }

    // StepBuilderFactory를 통해서 Step을 생성
    @Bean
    public Step Step() {
        return stepBuilderFactory.get("Step")
                .tasklet(new BatchTasklet(jobPostService, accessKey)) // Tasklet 설정
                .build();
    }
}
