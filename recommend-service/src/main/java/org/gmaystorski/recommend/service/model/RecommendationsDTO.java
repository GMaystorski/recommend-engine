package org.gmaystorski.recommend.service.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecommendationsDTO {

    @JsonProperty
    private Map<String, Double> recommendations;

    public RecommendationsDTO(Map<String, Double> recommendations) {
        this.recommendations = recommendations;
    }

}
