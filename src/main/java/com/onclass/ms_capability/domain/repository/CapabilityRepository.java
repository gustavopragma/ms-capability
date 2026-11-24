package com.onclass.ms_capability.domain.repository;

import com.onclass.ms_capability.domain.model.Capability;
import reactor.core.publisher.Mono;

public interface CapabilityRepository {
    Mono<Capability> save(Capability capability);
    Mono<Boolean> existsByName(String name);
    Mono<Void> deleteById(Long id);
}