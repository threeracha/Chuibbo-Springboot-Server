package com.sriracha.ChuibboServer.service.resumePhoto;

import com.sriracha.ChuibboServer.model.entity.ResumePhoto;
import com.sriracha.ChuibboServer.common.Header;
import com.sriracha.ChuibboServer.model.dto.request.resumePhoto.ResumePhotoRequestDto;
import com.sriracha.ChuibboServer.model.dto.response.resumePhoto.ResumePhotoResponseDto;
import com.sriracha.ChuibboServer.common.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ResumePhotoService extends BaseService<ResumePhotoRequestDto, ResumePhotoResponseDto, ResumePhoto> {
    // TODO 에러 처리
    @Override
    public Header<ResumePhotoResponseDto> create(Header<ResumePhotoRequestDto> request) {
        ResumePhotoRequestDto body = request.getData();

        ResumePhoto resumePhoto = ResumePhoto.builder()
                .userId(body.getUserId())
                .photoUrl(body.getPhotoUrl())
                .build();
        ResumePhoto newResumePhoto = baseRepository.save(resumePhoto);

        return response(newResumePhoto);
    }

    /** 사진을 유저 아이디로 조회  repository에 findByUserID */
    @Override
    public Header<ResumePhotoResponseDto> read(Long id) {
        return baseRepository.findById(id)
                .map(userPhoto -> response(userPhoto))
                .orElseGet(() -> Header.ERROR("no data"));
    }

    @Override
    public Header<ResumePhotoResponseDto> update(Header<ResumePhotoRequestDto> request) {
        return null;
    }

    /** 사진 1개 삭제 */
    @Override
    public Header delete(Long id) {

        // TODO AWS S3 권한 설정에 대한 고민하
        // TODO 1. 지우고자하는 photo id 가져오기
        // TODO 2. DB 에서 AWS 경로 읽어오기기
        // TODO AWS 에서 삭제하는 로직
        // TODO DB 에서도 삭제해주기
        // TODO 성공 메세지 반

        return null;
    }

    // TODO 사진 여러개 삭제 ( 선택한 항목에 대하여 )
    // TODO 유저의 모든 사진 삭제

    private Header<ResumePhotoResponseDto> response(ResumePhoto resumePhoto){

        ResumePhotoResponseDto body = ResumePhotoResponseDto.builder()
                .id(resumePhoto.getId())
                .userId(resumePhoto.getUserId())
                .photoUrl(resumePhoto.getPhotoUrl())
                .build();
        return Header.OK(body);
    }
}
