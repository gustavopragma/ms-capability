package com.onclass.ms_capability.domain.model;

import java.util.List;

public class Capability {
    private Long id;
    private String name;
    private String description;
    private List<Long> technologyIds;
    private List<Technology> technologies;

    public Capability() {}

    public Capability(Long id, String name, String description, List<Long> technologyIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.technologyIds = technologyIds;
    }

    public Capability(String name, Long id, String description, List<Long> technologyIds, List<Technology> technologies) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.technologyIds = technologyIds;
        this.technologies = technologies;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Long> getTechnologyIds() {
        return technologyIds;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTechnologyIds(List<Long> technologyIds) {
        this.technologyIds = technologyIds;
    }

    public List<Technology> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<Technology> technologies) {
        this.technologies = technologies;
    }
}
