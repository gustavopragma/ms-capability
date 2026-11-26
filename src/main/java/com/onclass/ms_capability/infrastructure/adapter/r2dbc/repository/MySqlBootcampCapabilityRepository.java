package com.onclass.ms_capability.infrastructure.adapter.r2dbc.repository;

import com.onclass.ms_capability.infrastructure.adapter.r2dbc.entity.BootcampCapabilityEntity;
import com.onclass.ms_capability.infrastructure.adapter.r2dbc.entity.CapabilityEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public interface MySqlBootcampCapabilityRepository extends ReactiveCrudRepository<BootcampCapabilityEntity, Long> {
    @Query(
        "SELECT c.* FROM capabilities c " +
            "INNER JOIN bootcamp_capabilities bc ON c.id = bc.id_capability " +
            "WHERE bc.id_bootcamp = :bootcampId"
    )
    Flux<CapabilityEntity> findCapabilitiesByBootcampId(Long bootcampId);

    @Query("SELECT id_capability FROM bootcamp_capabilities WHERE id_bootcamp = :bootcampId")
    Flux<Long> findCapabilityIdsByBootcampId(Long bootcampId);

    @Modifying
    @Query("DELETE FROM bootcamp_capabilities WHERE id_bootcamp = :bootcampId")
    Mono<Void> deleteAllByBootcampId(Long bootcampId);

    @Query("SELECT COUNT(*) FROM bootcamp_capabilities WHERE id_capability = :capabilityId")
    Mono<Long> countByCapabilityId(Long capabilityId);
}
