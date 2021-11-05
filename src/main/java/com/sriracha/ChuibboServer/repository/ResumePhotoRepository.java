/**
 * ResumeImageRepository
 *
 * @author jy
 * @version 1.0
 * @see None
 */
package com.sriracha.ChuibboServer.repository;

import com.sriracha.ChuibboServer.model.entity.ResumePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumePhotoRepository extends JpaRepository<ResumePhoto, Long> {
    List<ResumePhoto> findByUserId(Long userId);
}
