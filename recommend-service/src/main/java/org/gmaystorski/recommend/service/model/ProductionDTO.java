package org.gmaystorski.recommend.service.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductionDTO {

    @JsonProperty
    private String id;
    @JsonProperty
    private String description;
    @JsonProperty
    private String title;
    @JsonIgnore
    private List<String> categories;
    @JsonProperty
    private String duration;
    @JsonProperty
    private String rating;
    @JsonProperty
    private String country;
    @JsonProperty
    private String type;

    public ProductionDTO() {

    }

    public ProductionDTO(String id, String description, String title, List<String> categories) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getCategories() {
        return categories;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ProductionDTO) {
            return this.id.equals(((ProductionDTO) other).id);
        }
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

}
