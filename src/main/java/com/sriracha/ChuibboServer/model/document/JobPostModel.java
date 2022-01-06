package com.sriracha.ChuibboServer.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document(indexName = "job_post")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostModel {

    private Long id;

    private String logoUrl;

    private String companyName;

    private String subject;

    private String descriptionUrl;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<String> areas;

    private List<String> jobs;

    private List<String> careerTypes;

    private Date modificationDate;
}
