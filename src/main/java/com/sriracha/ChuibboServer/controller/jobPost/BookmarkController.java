package com.sriracha.ChuibboServer.controller.jobPost;

import com.sriracha.ChuibboServer.common.responseEntity.Message;
import com.sriracha.ChuibboServer.common.responseEntity.StatusEnum;
import com.sriracha.ChuibboServer.model.dto.response.JobPostResponseDto;
import com.sriracha.ChuibboServer.model.entity.Bookmark;
import com.sriracha.ChuibboServer.service.jobPost.BookmarkService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/job_post")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @ApiOperation(value = "채용공고 북마크 조회", notes = "관심있는 채용공고 북마크 조회")
    @GetMapping("/bookmarks")
    public ResponseEntity<Message<List<JobPostResponseDto>>> getBookmarks(@RequestHeader("Authorization") String jwt) {
        List<JobPostResponseDto> bookmarks = bookmarkService.getBookmarks(jwt);

        HttpHeaders headers= new HttpHeaders(); // TODO: header 중복 코드 처리
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Message message = new Message();
        message.setStatus(StatusEnum.OK);
        message.setMessage("OK");
        message.setData(bookmarks);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @ApiOperation(value = "채용공고 북마크 저장", notes = "관심있는 채용공고 북마크 저장")
    @PostMapping("/{jobPostId}/bookmark")
    public ResponseEntity saveBookmark(@RequestHeader("Authorization") String jwt, @PathVariable Long jobPostId) {
        bookmarkService.saveBookmark(jwt, jobPostId);

        HttpHeaders headers= new HttpHeaders(); // TODO: header 중복 코드 처리
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Message message = new Message();
        message.setStatus(StatusEnum.OK);
        message.setMessage("OK");

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @ApiOperation(value = "채용공고 북마크 삭제", notes = "관심있는 채용공고 북마크 삭제")
    @DeleteMapping("/{jobPostId}/bookmark")
    public ResponseEntity deleteBookmark(@RequestHeader("Authorization") String jwt, @PathVariable Long jobPostId) {
        bookmarkService.deleteBookmark(jwt, jobPostId);

        HttpHeaders headers= new HttpHeaders(); // TODO: header 중복 코드 처리
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Message message = new Message();
        message.setStatus(StatusEnum.OK);
        message.setMessage("OK");

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }
}
