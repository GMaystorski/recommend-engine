package org.gmaystorski.recommend.service.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInput {

    @JsonProperty("actors")
    private List<String> actors;
    @JsonProperty("movies")
    private List<String> movies;
    @JsonProperty("categories")
    private List<String> categories;

    public List<String> getActors() {
        return actors;
    }

    public List<String> getMovies() {
        return movies;
    }

    public List<String> getCategories() {
        return categories;
    }

}
