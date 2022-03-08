package com.sriracha.ChuibboServer.repository;

import com.sriracha.ChuibboServer.model.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor<JobPost> {
    Optional<JobPost> findByOpenApiJobPostId(Long openApiJobPostId);

    Page<JobPost> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<JobPost> findTop8ByOrderByCreatedAtDesc();
}
