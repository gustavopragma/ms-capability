package com.onclass.ms_capability.domain.repository;

import com.onclass.ms_capability.domain.model.Capability;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

public interface CapabilityRepository {
    Mono<Capability> save(Capability capability);
    Mono<Boolean> existsByName(String name);
    Mono<Void> deleteById(Long id);
    Flux<Capability> findAll();
    Flux<Long> findExistingIds(Collection<Long> capabilityIds);
    Mono<Void> associateBootcampWithCapabilities(Long bootcampId, List<Long> capabilityIds);
    Flux<Capability> findAllByBootcampId(Long bootcampId);
    Flux<Long> findCapabilityIdsByBootcampId(Long bootcampId);
    Mono<Void> deleteAllByBootcampId(Long bootcampId);
    Mono<Long> countByCapabilityId(Long capabilityId);
    Mono<Void> deleteAllById(List<Long> ids);
}