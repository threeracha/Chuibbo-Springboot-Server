package com.sriracha.ChuibboServer.service.jobPost;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sriracha.ChuibboServer.common.jwt.JwtTokenProvider;
import com.sriracha.ChuibboServer.model.dto.OpenApiJobPost;
import com.sriracha.ChuibboServer.model.dto.response.JobPostResponseDto;
import com.sriracha.ChuibboServer.model.entity.*;
import com.sriracha.ChuibboServer.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostService {

    private final JobPostRepository jobPostRepository;
    private final AreaRepository areaRepository;
    private final JobTypeRepository jobTypeRepository;
    private final CareerTypeRepository careerTypeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;

    public void saveJobPosts(String jsonData) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            OpenApiJobPost openApiJobPost = objectMapper.readValue(jsonData, OpenApiJobPost.class);
            int size = openApiJobPost.getJobs().getJob().size();

            // TODO: 쿼리가 너무 많음

            // DB에 있는지 확인하고, 없으면 DB에 저장
            for (int i = 0; i < size; i++) {
                OpenApiJobPost.Jobs.Job job = openApiJobPost.getJobs().getJob().get(i);
                if (jobPostRepository.findByOpenApiJobPostId(job.getId()).isEmpty()) {
                    String imageSource = "";

                    // 회사 logo 이미지 불러오기
                    if (!job.getCompany().getDetail().getHref().isEmpty()) {
                        String url = job.getCompany().getDetail().getHref();
                        Document doc = Jsoup.connect(url).ignoreHttpErrors(true).get();
                        if (!doc.getElementsByClass("inner_thumb").isEmpty())
                            imageSource = doc.select(".inner_thumb img").attr("src");
                    }

                    // 여러개일 경우, ,(콤마)로 split
                    List<Area> areas = new ArrayList<>();
                    List<Job> jobs = new ArrayList<>();
                    List<CareerType> careerTypes = new ArrayList<>();
                    if (job.getPosition().getLocation().getCode() != null)
                        for (String list: job.getPosition().getLocation().getCode().split(",")) // TODO: 해외인 경우,
                            areas.add(areaRepository.findById(Long.parseLong(list)).get());
                    if (job.getPosition().getJobMidCode().getCode() != null)
                        for (String list: job.getPosition().getJobMidCode().getCode().split(","))
                            jobs.add(jobTypeRepository.findById(Long.parseLong(list)).get());
                    if (job.getPosition().getExperienceLevel().getCode() != null)
                        for (String list: job.getPosition().getExperienceLevel().getCode().split(","))
                            careerTypes.add(careerTypeRepository.findById(Long.parseLong(list)).get());

                    JobPost jobPost = JobPost.builder()
                            .openApiJobPostId(job.getId())
                            .logoUrl(imageSource)
                            .companyName(job.getCompany().getDetail().getName())
                            .subject(job.getPosition().getTitle())
                            .descriptionUrl(job.getUrl())
                            .startDate(getTimestampToDate(job.getOpeningTimestamp()))
                            .endDate(getTimestampToDate(job.getExpirationTimestamp()))
                            .areas(areas)
                            .jobs(jobs)
                            .careerTypes(careerTypes)
                            .build();

                    jobPostRepository.save(jobPost);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LocalDateTime getTimestampToDate(String timestampStr) {
        Long timestamp = Long.parseLong(timestampStr);
        Date date = new java.util.Date(timestamp*1000L);
        LocalDateTime localDateTime = date.toInstant() // Date -> Instant
                .atZone(ZoneId.of("Asia/Seoul")) // Instant -> ZonedDateTime
                .toLocalDateTime();

        return localDateTime;
    }

    public List<JobPostResponseDto> getJobPosts(String jwt) {
        if (jwtTokenProvider.validateToken(jwt)) { // token이 valid하다면
            User user = (User) jwtTokenProvider.getAuthentication(jwt).getPrincipal();
            List<Long> bookmarkJobPostIdList = bookmarkRepository.findAllByUser(user)
                    .stream().map(bookmark -> bookmark.getJobPost().getId())
                    .collect(Collectors.toList());

            return jobPostRepository.findTop8ByOrderByCreatedAtDesc()
                    .stream().map(jobPost -> bookmarkJobPostIdList.contains(jobPost.getId())
                            ? addBookmark(modelMapper.map(jobPost, JobPostResponseDto.class), true)
                            : addBookmark(modelMapper.map(jobPost, JobPostResponseDto.class), false))
                    .collect(Collectors.toList());
        } else // token이 valid하지 않으면
            return jobPostRepository.findTop8ByOrderByCreatedAtDesc()
                    .stream().map(jobPost -> addBookmark(modelMapper.map(jobPost, JobPostResponseDto.class), false))
                    .collect(Collectors.toList());
    }

    public List<JobPostResponseDto> getAllJobPosts(String jwt, int page) {
        Pageable paging = PageRequest.of(page-1, 10);
        if (jwtTokenProvider.validateToken(jwt)) { // token이 valid하다면
            User user = (User) jwtTokenProvider.getAuthentication(jwt).getPrincipal();
            List<Long> bookmarkJobPostIdList = bookmarkRepository.findAllByUser(user)
                    .stream().map(bookmark -> bookmark.getJobPost().getId())
                    .collect(Collectors.toList());

            return jobPostRepository.findAllByOrderByCreatedAtDesc(paging).getContent()
                    .stream().map(jobPost -> bookmarkJobPostIdList.contains(jobPost.getId())
                            ? addBookmark(modelMapper.map(jobPost, JobPostResponseDto.class), true)
                            : addBookmark(modelMapper.map(jobPost, JobPostResponseDto.class), false))
                    .collect(Collectors.toList());
        } else // token이 valid하지 않으면
            return jobPostRepository.findAllByOrderByCreatedAtDesc(paging).getContent()
                    .stream().map(jobPost -> addBookmark(modelMapper.map(jobPost, JobPostResponseDto.class), false))
                    .collect(Collectors.toList());
    }

    public JobPostResponseDto addBookmark(JobPostResponseDto jobPostResponseDto, boolean bookmark) {
        jobPostResponseDto.setBookmark(bookmark);
        return jobPostResponseDto;
    }
}
