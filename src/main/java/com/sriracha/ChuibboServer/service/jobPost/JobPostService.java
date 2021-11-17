package com.sriracha.ChuibboServer.service.jobPost;

import com.sriracha.ChuibboServer.common.jwt.JwtTokenProvider;
import com.sriracha.ChuibboServer.model.dto.response.JobPostResponseDto;
import com.sriracha.ChuibboServer.model.entity.*;
import com.sriracha.ChuibboServer.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final JobRepository jobRepository;
    private final CareerTypeRepository careerTypeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseEntity saveJobPosts(String jsonData) throws ParseException, IOException {

        JSONObject jsonObject, position, jobMidCode, location, experienceLevel, company, detail;

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParser.parse(jsonData);
        JSONObject parseJobs = (JSONObject) jsonObj.get("jobs");
        JSONArray array = (JSONArray) parseJobs.get("job");

        // TODO: 쿼리가 너무 많음

        // DB에 있는지 확인하고, 없으면 DB에 저장
        for (int i = 0; i < array.size(); i++) {
            jsonObject = (JSONObject) array.get(i);

            if (jobPostRepository.findByOpenApiJobPostId(Long.parseLong(jsonObject.get("id").toString())).isEmpty()) {
                position = (JSONObject) jsonObject.get("position");
                jobMidCode = (JSONObject) position.get("job-mid-code");
                location = (JSONObject) position.get("location");
                experienceLevel = (JSONObject) position.get("experience-level");
                company = (JSONObject) jsonObject.get("company");
                detail = (JSONObject) company.get("detail");

                String imageSource = "";

                // 회사 logo 이미지 불러오기
                if (!detail.get("href").toString().isEmpty()) {
                    String url = detail.get("href").toString();
                    Document doc = Jsoup.connect(url).ignoreHttpErrors(true).get();
                    if (!doc.getElementsByClass("inner_thumb").isEmpty())
                        imageSource = doc.select(".inner_thumb img").attr("src");
                }

                // 여러개일 경우, ,(콤마)로 split
                String[] areaCodeList = location.get("code").toString().split(",");
                String[] jobCodeList = jobMidCode.get("code").toString().split(",");
                String[] careerTypeCodeList = experienceLevel.get("code").toString().split(",");

                List<Area> areas = new ArrayList<>();
                List<Job> jobs = new ArrayList<>();
                List<CareerType> careerTypes = new ArrayList<>();

                for (String list: areaCodeList) {
                    areas.add(areaRepository.findById(Long.parseLong(list)).get());
                }

                for (String list: jobCodeList) {
                    jobs.add(jobRepository.findById(Long.parseLong(list)).get());
                }

                for (String list: careerTypeCodeList) {
                    careerTypes.add(careerTypeRepository.findById(Long.parseLong(list)).get());
                }

                JobPost jobPost = JobPost.builder()
                        .openApiJobPostId(Long.parseLong(jsonObject.get("id").toString()))
                        .logoUrl(imageSource)
                        .companyName(detail.get("name").toString())
                        .subject(position.get("title").toString())
                        .descriptionUrl(jsonObject.get("url").toString())
                        .startDate(getTimestampToDate(jsonObject.get("opening-timestamp").toString()))
                        .endDate(getTimestampToDate(jsonObject.get("expiration-timestamp").toString()))
                        .areas(areas)
                        .jobs(jobs)
                        .careerTypes(careerTypes)
                        .build();

                jobPostRepository.save(jobPost);
            }
        }

        return ResponseEntity.ok(HttpStatus.OK); // TODO
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
                            ? entityToDto(jobPost, true)
                            : entityToDto(jobPost, false))
                    .collect(Collectors.toList());
        } else // token이 valid하지 않으면
            return jobPostRepository.findTop8ByOrderByCreatedAtDesc()
                    .stream().map(jobPost -> entityToDto(jobPost, false))
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
                            ? entityToDto(jobPost, true)
                            : entityToDto(jobPost, false))
                    .collect(Collectors.toList());
        } else // token이 valid하지 않으면
            return jobPostRepository.findAllByOrderByCreatedAtDesc(paging).getContent()
                    .stream().map(jobPost -> entityToDto(jobPost, false))
                    .collect(Collectors.toList());
    }

    private JobPostResponseDto entityToDto(JobPost jobPost, boolean bookmark) {
        JobPostResponseDto jobPostResponseDto = JobPostResponseDto.builder()
                .id(jobPost.getId())
                .logoUrl(jobPost.getLogoUrl())
                .companyName(jobPost.getCompanyName())
                .subject(jobPost.getSubject())
                .descriptionUrl(jobPost.getDescriptionUrl())
                .startDate(jobPost.getStartDate())
                .endDate(jobPost.getEndDate())
                .areas(jobPost.getAreas())
                .jobs(jobPost.getJobs())
                .careerTypes(jobPost.getCareerTypes())
                .bookmark(bookmark)
                .build();

        return jobPostResponseDto;
    }
}
