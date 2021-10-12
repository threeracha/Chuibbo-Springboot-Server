/**
 * ResumePhoto
 * 생성된 취업사진
 *
 * @author jy
 * @version 1.0
 * @see None
 */
package com.sriracha.ChuibboServer.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ResumePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String photoUrl;

    private String userId;

    @CreatedDate
    private LocalDateTime createdAt;

}