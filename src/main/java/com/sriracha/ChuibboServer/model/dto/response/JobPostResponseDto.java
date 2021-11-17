package com.sriracha.ChuibboServer.model.dto.response;

import com.sriracha.ChuibboServer.model.entity.Area;
import com.sriracha.ChuibboServer.model.entity.CareerType;
import com.sriracha.ChuibboServer.model.entity.Job;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostResponseDto {

    private Long id;

    private String logoUrl;

    private String companyName;

    private String subject;

    private String descriptionUrl;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<Area> areas;

    private List<Job> jobs;

    private List<CareerType> careerTypes;

    private boolean bookmark;
}
