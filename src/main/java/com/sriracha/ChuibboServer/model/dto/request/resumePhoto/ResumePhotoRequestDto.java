package com.sriracha.ChuibboServer.model.dto.request.resumePhoto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumePhotoRequestDto {

    private String photoUrl;

    private Long userId;
}
