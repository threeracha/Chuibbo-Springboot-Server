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

@Repository
public interface ResumeimageRepository extends JpaRepository<ResumePhoto, Long> {
}
