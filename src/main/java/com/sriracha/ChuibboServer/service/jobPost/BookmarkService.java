package com.sriracha.ChuibboServer.service.jobPost;

import com.sriracha.ChuibboServer.common.jwt.JwtTokenProvider;
import com.sriracha.ChuibboServer.model.dto.response.JobPostResponseDto;
import com.sriracha.ChuibboServer.model.entity.Bookmark;
import com.sriracha.ChuibboServer.model.entity.JobPost;
import com.sriracha.ChuibboServer.model.entity.User;
import com.sriracha.ChuibboServer.repository.BookmarkRepository;
import com.sriracha.ChuibboServer.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final JwtTokenProvider jwtTokenProvider;
    private final BookmarkRepository bookmarkRepository;
    private final JobPostRepository jobPostRepository;
    private final ModelMapper modelMapper;

    public List<JobPostResponseDto> getBookmarks(String jwt) {
        User user = (User) jwtTokenProvider.getAuthentication(jwt).getPrincipal();

        if (user != null) {
            List<JobPostResponseDto> bookmarkList = bookmarkRepository.findAllByUser(user)
                    .stream().map(bookmark -> addBookmark(modelMapper.map(bookmark.getJobPost(), JobPostResponseDto.class), true))
                    .collect(Collectors.toList());


            // JobPost의 EndDate를 기준(마감순)으로 정렬
            Collections.sort(bookmarkList, new Comparator<JobPostResponseDto>() {
                @Override
                public int compare(JobPostResponseDto b1, JobPostResponseDto b2) {
                    return b1.getEndDate().compareTo(b2.getEndDate());
                }
            });

            return bookmarkList;
        } else
            return null;
    }

    public JobPostResponseDto saveBookmark(String jwt, Long jobPostId) {
        User user = (User) jwtTokenProvider.getAuthentication(jwt).getPrincipal();
        Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);

        if (user != null && jobPost.isPresent()) {
            if (bookmarkRepository.findByUserAndJobPost(user, jobPost.get()).isEmpty()) {
                Bookmark bookmark = Bookmark.builder()
                        .user(user)
                        .jobPost(jobPost.get())
                        .createdAt(LocalDateTime.now())
                        .build();

                bookmarkRepository.save(bookmark);

                return modelMapper.map(bookmark, JobPostResponseDto.class);
            }
        }

        return null;
    }

    public Long deleteBookmark(String jwt, Long jobPostId) {
        User user = (User) jwtTokenProvider.getAuthentication(jwt).getPrincipal();
        Optional<JobPost> jobPost = jobPostRepository.findById(jobPostId);

        if (user != null && jobPost.isPresent()) {
            if (bookmarkRepository.findByUserAndJobPost(user, jobPost.get()).isPresent()) {
                bookmarkRepository.deleteByUserAndJobPost(user, jobPost.get());
                return jobPostId;
            }
        }

        return null;
    }

    public JobPostResponseDto addBookmark(JobPostResponseDto jobPostResponseDto, boolean bookmark) {
        jobPostResponseDto.setBookmark(bookmark);
        return jobPostResponseDto;
    }
}
