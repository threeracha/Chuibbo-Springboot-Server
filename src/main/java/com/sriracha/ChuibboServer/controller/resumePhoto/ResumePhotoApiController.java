package com.sriracha.ChuibboServer.controller.resumePhoto;

import com.sriracha.ChuibboServer.service.resumePhoto.ResumePhotoService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/resume-photo")
public class ResumePhotoApiController {

    private final ResumePhotoService resumePhotoService;

    @ApiOperation("취업사진 저장")
    @PostMapping(path = "/")
    public ResponseEntity<?> savePhoto(@RequestHeader("CHUIBBO-TOKEN") String token,
                                                        @RequestParam("image") MultipartFile multipartFile){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(resumePhotoService.save(multipartFile, token));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation("취업사진 조회(1장)")
    @GetMapping("/photo")
    public ResponseEntity<?> getPhoto(@RequestParam Long photoId){
        // TODO : TOKEN 도 받아와서 해당 유저의 사진인지 검증하기
        try {
            return ResponseEntity.status(HttpStatus.OK).body(resumePhotoService.getPhoto(photoId));
        } catch (Exception e) {
            throw e;
        }
    }

    @ApiOperation("취업사진 조회(N장)")
    @GetMapping("/photos")
    public ResponseEntity<?> getPhotos(@RequestHeader("CHUIBBO-TOKEN") String token){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(resumePhotoService.getPhotos(token));
        } catch (Exception e) {
            throw e;
        }
    }

    @ApiOperation("취업사진 삭제")
    @DeleteMapping("/")
    public ResponseEntity<?> deletePhoto(@RequestParam Long photoId){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(resumePhotoService.delete(photoId));
        } catch (Exception e) {
            throw e;
        }
    }

}
