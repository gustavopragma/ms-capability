package com.onclass.ms_capability.infrastructure.adapter.r2dbc.repository;

import com.onclass.ms_capability.infrastructure.adapter.r2dbc.entity.CapabilityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MySqlCapabilityRepository extends ReactiveCrudRepository<CapabilityEntity, Long> {
    Mono<Boolean> existsByNameIgnoreCase(String name);
}
