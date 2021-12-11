package com.sriracha.ChuibboServer.controller.jobPost;

import com.sriracha.ChuibboServer.common.responseEntity.ResponseData;
import com.sriracha.ChuibboServer.common.responseEntity.ResponseError;
import com.sriracha.ChuibboServer.common.responseEntity.StatusEnum;
import com.sriracha.ChuibboServer.model.dto.response.JobPostResponseDto;
import com.sriracha.ChuibboServer.service.jobPost.BookmarkService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/job_post")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @ApiOperation(value = "채용공고 북마크 조회", notes = "관심있는 채용공고 북마크 조회")
    @GetMapping("/bookmarks")
    public ResponseEntity getBookmarks(@RequestHeader("Authorization") String jwt) {

        List<JobPostResponseDto> bookmarkList = bookmarkService.getBookmarks(jwt);

        if (bookmarkList == null) {
            ResponseError responseError = ResponseError.builder()
                    .status(StatusEnum.NOT_FOUND)
                    .message("user가 null")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseError);
        }

        ResponseData responseData = ResponseData.builder()
                .data(bookmarkList)
                .build();

        return ResponseEntity.ok()
                .body(responseData);
    }

    @ApiOperation(value = "채용공고 북마크 저장", notes = "관심있는 채용공고 북마크 저장")
    @PostMapping("/{jobPostId}/bookmark")
    public ResponseEntity saveBookmark(@RequestHeader("Authorization") String jwt, @PathVariable Long jobPostId) {

        JobPostResponseDto jobPostResponseDto = bookmarkService.saveBookmark(jwt, jobPostId);

        if (jobPostResponseDto == null) {
            ResponseError responseError = ResponseError.builder()
                    .status(StatusEnum.NOT_FOUND)
                    .message("null")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseError);
        }

        ResponseData responseData = ResponseData.builder()
                .data(jobPostResponseDto)
                .build();

        return ResponseEntity.ok()
                .body(responseData);
    }

    @ApiOperation(value = "채용공고 북마크 삭제", notes = "관심있는 채용공고 북마크 삭제")
    @DeleteMapping("/{jobPostId}/bookmark")
    public ResponseEntity deleteBookmark(@RequestHeader("Authorization") String jwt, @PathVariable Long jobPostId) {
        Long id = bookmarkService.deleteBookmark(jwt, jobPostId);

        if (id == null) {
            ResponseError responseError = ResponseError.builder()
                    .status(StatusEnum.NOT_FOUND)
                    .message("null")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseError);
        }

        ResponseData responseData = ResponseData.builder()
                .data(id)
                .build();

        return ResponseEntity.ok()
                .body(responseData);
    }
}
