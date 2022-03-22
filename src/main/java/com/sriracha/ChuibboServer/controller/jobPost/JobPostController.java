package com.sriracha.ChuibboServer.controller.jobPost;

import com.sriracha.ChuibboServer.common.responseEntity.ResponseData;
import com.sriracha.ChuibboServer.model.dto.response.JobPostResponseDto;
import com.sriracha.ChuibboServer.service.jobPost.JobPostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/job-posts")
public class JobPostController {

    private final JobPostService jobPostService;

    @Value("${saramin.access-key}")
    private String accessKey;

    @GetMapping("/oapi")
    @ApiOperation(value = "채용공고 저장", notes = "4시간마다 채용공고를 저장한다.")
    public ResponseEntity getOpenApi() throws IOException, ParseException {

        SaraminOpenApi(accessKey, jobPostService);

        ResponseData responseData = ResponseData.builder()
                .build();

        return ResponseEntity.ok()
                .body(responseData);

    }

    public static void SaraminOpenApi(String accessKey, JobPostService jobPostService) throws ParseException, IOException {
        int count = 110;

        StringBuffer response = new StringBuffer();

        try {
            String text = URLEncoder.encode("", "UTF-8");
            String apiURL = "https://oapi.saramin.co.kr/job-search?access-key="+ accessKey +"&keyword="+ text+"&count="+ count;

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            int responseCode = con.getResponseCode();
            BufferedReader br;

            if(responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        jobPostService.saveJobPosts(response.toString());
    }

    @GetMapping("")
    @ApiOperation(value = "채용공고 홈 조회", notes = "최신 8개의 채용공고를 조회한다.")
    public ResponseEntity getJobPosts(@RequestHeader("Authorization") String jwt) {

        List<JobPostResponseDto> jobPostResponseDtoList = jobPostService.getJobPosts(jwt);

        ResponseData responseData = ResponseData.builder()
                .data(jobPostResponseDtoList)
                .build();

        return ResponseEntity.ok()
                .body(responseData);
    }

    @GetMapping("/more")
    @ApiOperation(value = "채용공고 더보기 조회", notes = "전체 채용공고를 페이지별 조회한다.")
    public ResponseEntity getAllJobPosts(@RequestHeader("Authorization") String jwt, @RequestParam int page) {

        List<JobPostResponseDto> jobPostResponseDtoList = jobPostService.getAllJobPosts(jwt, page);

        ResponseData responseData = ResponseData.builder()
                .data(jobPostResponseDtoList)
                .build();

        return ResponseEntity.ok()
                .body(responseData);
    }
}
