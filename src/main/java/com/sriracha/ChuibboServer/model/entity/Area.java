package com.sriracha.ChuibboServer.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobType;

    @ManyToMany
    @JoinTable(
            name = "job_post_has_area",
            joinColumns = @JoinColumn(name = "area_id")
    )
    private List<JobPost> jobPosts = new ArrayList<>();

}
