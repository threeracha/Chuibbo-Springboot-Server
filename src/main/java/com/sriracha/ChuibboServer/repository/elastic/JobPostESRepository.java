package com.sriracha.ChuibboServer.repository.elastic;

import com.sriracha.ChuibboServer.model.document.JobPostModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostESRepository extends ElasticsearchRepository<JobPostModel, Long> {
}
