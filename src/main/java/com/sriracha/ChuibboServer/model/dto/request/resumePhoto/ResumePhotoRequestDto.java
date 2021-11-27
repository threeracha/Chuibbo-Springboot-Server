package com.sriracha.ChuibboServer.model.dto.request.resumePhoto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumePhotoRequestDto {

    private MultipartFile multipartFile;

    private String optionFaceShape;

    private String optionHair;

    private String optionSuit;
}
