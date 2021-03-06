package com.sriracha.ChuibboServer.model.dto.response.resumePhoto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumePhotoResponseDto {

    private Long id;

    private String photoUrl;

    private Long userId;

    private String optionFaceShape;

    private String optionHair;

    private String optionSuit;

}
