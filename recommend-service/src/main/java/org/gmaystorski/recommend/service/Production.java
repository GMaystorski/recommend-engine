package org.gmaystorski.recommend.service;

import java.util.List;

public class Production {

    private String id;
    private String description;
    private String title;
    private List<String> categories;

    public Production(String id, String description, String title, List<String> categories) {
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
        if (other instanceof Production) {
            return this.id.equals(((Production) other).id);
        }
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

}
