package com.onclass.ms_capability.infrastructure.adapter.r2dbc.repository;

import com.onclass.ms_capability.infrastructure.adapter.r2dbc.entity.CapabilityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface MySqlCapabilityRepository extends ReactiveCrudRepository<CapabilityEntity, Long> {
    Mono<Boolean> existsByNameIgnoreCase(String name);

    @Query("SELECT id FROM capabilities WHERE id IN (:ids)")
    Flux<Long> findExistingIds(Collection<Long> ids);

}
