package com.sriracha.ChuibboServer.repository;

import com.sriracha.ChuibboServer.model.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTypeRepository extends JpaRepository<Job, Long> {
}
