package com.onclass.ms_capability.domain.repository;

import com.onclass.ms_capability.domain.model.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyRepository {
    Mono<Void> associateTechnologies(Long capabilityId, List<Long> technologyIds);
    Flux<Technology> findTechnologyByCapabilityId(Long capabilityId);
    Mono<Void> deleteTechnologiesByCapabilityIds(List<Long> capabilityIds);
}
