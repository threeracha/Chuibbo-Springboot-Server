package com.sriracha.ChuibboServer.service.jobPost;

import com.sriracha.ChuibboServer.model.dto.response.JobPostResponseDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface ISearchService {
    ResultQuery searchFromQuery(String query) throws IOException;

    List<JobPostResponseDto> getJobPostsById(String jwt, ArrayList<Long> idList);
}