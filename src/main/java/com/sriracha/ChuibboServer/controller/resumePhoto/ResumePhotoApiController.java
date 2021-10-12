package com.sriracha.ChuibboServer.controller.resumePhoto;

import com.sriracha.ChuibboServer.common.controller.CrudController;
import com.sriracha.ChuibboServer.model.entity.ResumePhoto;
import com.sriracha.ChuibboServer.model.dto.request.resumePhoto.ResumePhotoRequestDto;
import com.sriracha.ChuibboServer.model.dto.response.resumePhoto.ResumePhotoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/resume/photo")
public class ResumePhotoApiController extends CrudController<ResumePhotoRequestDto, ResumePhotoResponseDto, ResumePhoto> {

}
