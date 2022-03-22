package com.sriracha.ChuibboServer.controller.jobPost;

import com.sriracha.ChuibboServer.common.responseEntity.ResponseData;
import com.sriracha.ChuibboServer.model.dto.response.JobPostResponseDto;
import com.sriracha.ChuibboServer.service.jobPost.ISearchService;
import com.sriracha.ChuibboServer.service.jobPost.ResultQuery;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/job-posts/search")
public class JobPostSearchController {

    private final ISearchService searchService;

    @ApiOperation("채용공고 검색")
    @GetMapping("")
    public ResponseEntity searchQuery(@RequestHeader("Authorization") String jwt, @RequestParam String query) throws IOException {

        ResultQuery resultQuery = searchService.searchFromQuery(query.trim().toLowerCase());

        ResponseData responseData = ResponseData.builder()
                .build();

        if (!resultQuery.getElements().isEmpty()) {
            List<JobPostResponseDto> jobPostResponseDtoList = searchService.getJobPostsById(jwt, resultQuery.getElements());

            responseData.setData(jobPostResponseDtoList);
            return ResponseEntity.ok()
                    .body(responseData);
        }

        return ResponseEntity.ok().body(responseData);
    }
}


