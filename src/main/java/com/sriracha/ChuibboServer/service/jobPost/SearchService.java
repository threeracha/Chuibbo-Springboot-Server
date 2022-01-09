package com.sriracha.ChuibboServer.service.jobPost;

import com.sriracha.ChuibboServer.common.jwt.JwtTokenProvider;
import com.sriracha.ChuibboServer.common.utils.Constants;
import com.sriracha.ChuibboServer.common.utils.HelperFunctions;
import com.sriracha.ChuibboServer.model.dto.response.JobPostResponseDto;
import com.sriracha.ChuibboServer.model.entity.JobPost;
import com.sriracha.ChuibboServer.model.entity.User;
import com.sriracha.ChuibboServer.repository.BookmarkRepository;
import com.sriracha.ChuibboServer.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchService implements ISearchService {

    @Value("${api.elasticsearch.uri}")
    private String elasticSearchUri;

    @Value("${api.elasticsearch.search}")
    private String elasticSearchSearchPrefix;

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
    private final JobPostRepository jobPostRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;

    @Override
    public ResultQuery searchFromQuery(String query) throws IOException {
        String body = HelperFunctions.buildMultiIndexMatchBody(query);
        return executeHttpRequest(body);
    }

    private ResultQuery executeHttpRequest(String body) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ResultQuery resultQuery = new ResultQuery();
            HttpPost httpPost = new HttpPost(HelperFunctions.buildSearchUri(elasticSearchUri
                    , "job_post", elasticSearchSearchPrefix));

            httpPost.setHeader(Constants.CONTENT_ACCEPT, Constants.APP_TYPE);
            httpPost.setHeader(Constants.CONTENT_TYPE, Constants.APP_TYPE);
            try {
                httpPost.setEntity(new StringEntity(body, Constants.ENCODING_UTF8));
                HttpResponse response = httpClient.execute(httpPost);
                String message = EntityUtils.toString(response.getEntity());

                JSONParser parser = new JSONParser();
                Object object = parser.parse(message);
                JSONObject myObject = (JSONObject) object;

                JSONObject hits, total;
                hits = (JSONObject) myObject.get(Constants.HITS);
                total = (JSONObject) hits.get(Constants.TOTAL_HITS);

                // TODO: null일때
                if((Long)total.get("value") != 0){

                    JSONArray hitsOfHits = (JSONArray) hits.get(Constants.HITS);
                    ArrayList<Long> array = new ArrayList<Long>();

                    for (int i = 0; i  < hitsOfHits.size(); i++) {
                        JSONObject hitsOfHit = (JSONObject) hitsOfHits.get(i);
                        JSONObject _source = (JSONObject) hitsOfHit.get("_source");
                        array.add((Long)_source.get("id"));
                    }

                    resultQuery.setElements(array);
                    resultQuery.setNumberOfResults((Long)total.get("value"));
                } else {
                    resultQuery.setElements(null);
                    resultQuery.setNumberOfResults(0L);
                }

                resultQuery.setTimeTook((float) (((Long) myObject.get(Constants.TOOK)).doubleValue() / Constants.TO_MS));
            } catch (IOException | ParseException e) {
                LOGGER.error("Error while connecting to elastic engine --> {}", e.getMessage());
                resultQuery.setNumberOfResults(0L);
            }

            return resultQuery;
        }
    }

    public List<JobPostResponseDto> getJobPostsById(String jwt, ArrayList<Long> idList) {
        List<JobPostResponseDto> jobPostResponseDtoList = new ArrayList<>();

        if (jwtTokenProvider.validateToken(jwt)) { // token이 valid하다면
            User user = (User) jwtTokenProvider.getAuthentication(jwt).getPrincipal();
            List<Long> bookmarkJobPostIdList = bookmarkRepository.findAllByUser(user)
                    .stream().map(bookmark -> bookmark.getJobPost().getId())
                    .collect(Collectors.toList());

            for (Long id : idList) {
                JobPost jobPost = jobPostRepository.findById(id).get();
                JobPostResponseDto jobPostResponseDto = bookmarkJobPostIdList.contains(jobPost.getId())
                        ? addBookmark(modelMapper.map(jobPost, JobPostResponseDto.class), true)
                        : addBookmark(modelMapper.map(jobPost, JobPostResponseDto.class), false);
                jobPostResponseDtoList.add(jobPostResponseDto);
            }
        } else { // token이 valid하지 않으면
            for (Long id : idList) {
                JobPostResponseDto jobPostResponseDto = addBookmark(modelMapper.map(jobPostRepository.findById(id).get(), JobPostResponseDto.class), false);
                jobPostResponseDtoList.add(jobPostResponseDto);
            }
        }

        return jobPostResponseDtoList;
    }

    public JobPostResponseDto addBookmark(JobPostResponseDto jobPostResponseDto, boolean bookmark) {
        jobPostResponseDto.setBookmark(bookmark);
        return jobPostResponseDto;
    }
}