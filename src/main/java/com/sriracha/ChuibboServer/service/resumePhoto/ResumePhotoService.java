package com.sriracha.ChuibboServer.service.resumePhoto;

import com.sriracha.ChuibboServer.common.jwt.JwtTokenProvider;
import com.sriracha.ChuibboServer.model.dto.request.resumePhoto.ResumePhotoRequestDto;
import com.sriracha.ChuibboServer.model.dto.response.resumePhoto.ResumePhotoResponseDto;
import com.sriracha.ChuibboServer.model.entity.ResumePhoto;
import com.sriracha.ChuibboServer.repository.ResumePhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResumePhotoService {

    private final JwtTokenProvider jwtTokenProvider;
    private final ResumePhotoRepository resumePhotoRepository;
    private final S3Uploader s3Uploader;

    /** 사진 1장 저장 */
    public ResumePhotoResponseDto save(ResumePhotoRequestDto resumePhotoRequestDto, String token) {
        try{
            String imageUrl = s3Uploader.upload(resumePhotoRequestDto.getMultipartFile(), "resumePhoto");
            ResumePhoto resumePhoto = ResumePhoto.builder()
                    .userId(jwtTokenProvider.getUserPk(token))
                    .photoUrl(imageUrl)
                    .optionFaceShape(resumePhotoRequestDto.getOptionFaceShape())
                    .optionHair(resumePhotoRequestDto.getOptionHair())
                    .optionSuit(resumePhotoRequestDto.getOptionSuit())
                    .build();
            ResumePhoto savedResumePhoto = resumePhotoRepository.save(resumePhoto);

            return ResumePhotoResponseDto.builder().photoUrl(imageUrl).id(savedResumePhoto.getId()).userId(savedResumePhoto.getUserId())
                    .optionFaceShape(savedResumePhoto.getOptionFaceShape()).optionHair(savedResumePhoto.getOptionHair()).optionSuit(savedResumePhoto.getOptionSuit())
                    .build();
        }catch (Exception e){
            log.info("error : " + e);
            // TODO : 예외 처
            return null;
        }
    }

    /** 사진 아이디로 조회 (1장)  repository에 findByUserID */
    public String getPhoto(Long photoId) {
        if (!resumePhotoRepository.findById(photoId).isPresent()) {
            return null; // TODO : 예외 처리
        }
        ResumePhoto resumePhoto = resumePhotoRepository.findById(photoId).orElseThrow(() -> new IllegalArgumentException("사진이 존재하지 않습니다."));
        return resumePhoto.getPhotoUrl();
    }

    /** 사진을 유저 아이디로 조회 (N장) */
    public List<ResumePhoto> getPhotos(String token) {
        Long userId = jwtTokenProvider.getUserPk(token);
        if (resumePhotoRepository.findByUserId(userId).isEmpty()) {
            return null; // TODO : 예외 처리
        }
        return resumePhotoRepository.findByUserId(userId);
    }

    /** 사진 1개 삭제 */
    public String delete(Long photoId) {
        try {
            // TODO AWS S3 권한 설정에 대한 고민하기
            // TODO TOKEN 추가해서 해당 사용자의 사진이 맞는지 확인
            ResumePhoto resumephoto = resumePhotoRepository.findById(photoId).orElseThrow(IllegalArgumentException::new);
            s3Uploader.delete(resumephoto.getPhotoUrl());
            resumePhotoRepository.delete(resumephoto);
            return "success";
        } catch (Exception e){
            return null;
        }
    }

    // TODO 사진 여러개 삭제 ( 선택한 항목에 대하여 )
    // TODO 유저의 모든 사진 삭제
}
