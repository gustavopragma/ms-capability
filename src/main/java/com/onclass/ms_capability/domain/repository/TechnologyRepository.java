package com.onclass.ms_capability.domain.repository;

import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyRepository {
    Mono<Void> associateTechnologies(Long capabilityId, List<Long> technologyIds);
}
