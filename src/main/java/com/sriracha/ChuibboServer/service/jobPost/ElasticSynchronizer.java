package com.sriracha.ChuibboServer.service.jobPost;

import com.sriracha.ChuibboServer.common.utils.Constants;
import com.sriracha.ChuibboServer.model.document.JobPostModel;
import com.sriracha.ChuibboServer.model.entity.JobPost;
import com.sriracha.ChuibboServer.repository.JobPostRepository;
import com.sriracha.ChuibboServer.repository.elastic.JobPostESRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSynchronizer {

    private final JobPostRepository jobPostRepository;
    private final JobPostESRepository jobPostESRepository;
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSynchronizer.class);

    @Scheduled(cron = "0 */3 * * * *")
    @Transactional
    public void sync() {
        LOG.info("Start Syncing - {}", LocalDateTime.now());
        this.syncJobPosts();
        LOG.info(" End Syncing - {}", LocalDateTime.now());
    }

    private void syncJobPosts() {
        Specification<JobPost> jobPostSpecification = (root, criteriaQuery, criteriaBuilder) ->
                getModificationDatePredicate(criteriaBuilder, root);
        List<JobPost> jobPostList;
        if (jobPostESRepository.count() == 0) {
            jobPostList = jobPostRepository.findAll();
        } else {
            jobPostList = jobPostRepository.findAll(jobPostSpecification);
        }

        for(JobPost jobPost: jobPostList) {
            LOG.info("Syncing JobPost - {}", jobPost.getId());
            jobPostESRepository.save(JobPostModel.builder()
                    .id(jobPost.getId())
                    .logoUrl(jobPost.getLogoUrl())
                    .companyName(jobPost.getCompanyName())
                    .subject(jobPost.getSubject())
                    .descriptionUrl(jobPost.getDescriptionUrl())
                    .startDate(jobPost.getStartDate()) // TODO: 0000-00-00 00:00:00 형식으로 바꾸기
                    .endDate(jobPost.getEndDate()) // TODO: 0000-00-00 00:00:00 형식으로 바꾸기
                    .areas(jobPost.getAreas().stream().map(area -> area.getArea()).collect(Collectors.toList()))
                    .jobs(jobPost.getJobs().stream().map(job -> job.getJobType()).collect(Collectors.toList()))
                    .careerTypes(jobPost.getCareerTypes().stream().map(careerType -> careerType.getCareerType()).collect(Collectors.toList()))
                    .modificationDate(jobPost.getModificationDate())
                    .build());
        }
    }

    private static Predicate getModificationDatePredicate(CriteriaBuilder cb, Root<?> root) {
        Expression<Timestamp> currentTime;
        currentTime = cb.currentTimestamp();
        Expression<Timestamp> currentTimeMinus = cb.literal(
                new Timestamp(System.currentTimeMillis() -
                        (Constants.INTERVAL_IN_MILLISECONDE)));
        return cb.between(root.<Date>get(Constants.MODIFICATION_DATE),
                currentTimeMinus,
                currentTime
        );
    }
}
