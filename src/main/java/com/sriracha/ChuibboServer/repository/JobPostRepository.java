package com.sriracha.ChuibboServer.repository;

import com.sriracha.ChuibboServer.model.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    Optional<JobPost> findByOpenApiJobPostId(Long openApiJobPostId);

    List<JobPost> findAll();
}
