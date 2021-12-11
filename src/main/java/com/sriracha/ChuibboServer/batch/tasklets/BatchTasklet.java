package com.sriracha.ChuibboServer.batch.tasklets;

import com.sriracha.ChuibboServer.controller.jobPost.JobPostController;
import com.sriracha.ChuibboServer.service.jobPost.JobPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Slf4j
public class BatchTasklet implements Tasklet {

    private JobPostService jobPostService;
    private String accessKey;

    public BatchTasklet (JobPostService jobPostService, String accessKey){
        this.jobPostService = jobPostService;
        this.accessKey = accessKey;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        JobPostController.SaraminOpenApi(accessKey, jobPostService);

        log.debug("executed tasklet !!");
        return RepeatStatus.FINISHED;
    }
}
