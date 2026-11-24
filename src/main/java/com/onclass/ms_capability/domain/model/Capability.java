package com.onclass.ms_capability.domain.model;

import java.util.List;

public class Capability {
    private Long id;
    private String name;
    private String description;
    private List<Long> technologyIds;

    public Capability() {}

    public Capability(Long id, String name, String description, List<Long> technologyIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.technologyIds = technologyIds;
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
}
