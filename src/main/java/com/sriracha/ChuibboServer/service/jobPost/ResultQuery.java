package com.sriracha.ChuibboServer.service.jobPost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResultQuery {

    private Float timeTook;

    private Long numberOfResults;

    private ArrayList<Long> elements;

}
