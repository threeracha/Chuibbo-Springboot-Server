package com.sriracha.ChuibboServer.service.jobPost;

import com.sriracha.ChuibboServer.model.entity.*;
import com.sriracha.ChuibboServer.repository.AreaRepository;
import com.sriracha.ChuibboServer.repository.CareerTypeRepository;
import com.sriracha.ChuibboServer.repository.JobPostRepository;
import com.sriracha.ChuibboServer.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostService {

    private final JobPostRepository jobPostRepository;
    private final AreaRepository areaRepository;
    private final JobRepository jobRepository;
    private final CareerTypeRepository careerTypeRepository;

    public ResponseEntity saveJobPosts(String jsonData) throws ParseException {

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
                    Document doc = Jsoup.parse(url);
                    imageSource = doc.getElementsByClass("inner_thumb").select("img").attr("src");
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
}
