package com.sriracha.ChuibboServer.controller.jobPost;

import com.sriracha.ChuibboServer.common.responseEntity.StatusEnum;
import com.sriracha.ChuibboServer.common.responseEntity.Message;
import com.sriracha.ChuibboServer.model.dto.response.JobPostResponseDto;
import com.sriracha.ChuibboServer.service.jobPost.JobPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/job_posts")
public class JobPostController {

    private final JobPostService jobPostService;

    @GetMapping("/oapi")
    public void getOpenApi() throws IOException, ParseException {
        String accessKey = "";
        int count = 6;

        StringBuffer response = new StringBuffer();

        try {
            String text = URLEncoder.encode("", "UTF-8");
            String apiURL = "https://oapi.saramin.co.kr/job-search?access-key="+accessKey+"&keyword="+ text+"&count="+ count;

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

    @GetMapping("") // TODO: 홈에는 특정 기준에 따라 일부 jobPosts만 get하도록
    public ResponseEntity<Message<List<JobPostResponseDto>>> getJobPosts() {

        List<JobPostResponseDto> jobPostResponseDtos = jobPostService.getJobPosts();

        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Message message = new Message();
        message.setStatus(StatusEnum.OK);
        message.setMessage("OK");
        message.setData(jobPostResponseDtos);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }
}
