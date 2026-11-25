package com.onclass.ms_capability.infrastructure.adapter.r2dbc.repository;

import com.onclass.ms_capability.domain.model.Capability;
import com.onclass.ms_capability.domain.repository.CapabilityRepository;
import com.onclass.ms_capability.infrastructure.adapter.r2dbc.entity.BootcampCapabilityEntity;
import com.onclass.ms_capability.infrastructure.adapter.r2dbc.mapper.CapabilityEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CapabilityRepositoryImpl implements CapabilityRepository {

    private final MySqlCapabilityRepository mySqlCapabilityRepository;
    private final MySqlBootcampCapabilityRepository mySqlBootcampCapabilityRepository;
    private final CapabilityEntityMapper capabilityEntityMapper;

    @Override
    public Mono<Capability> save(Capability capability) {
        return mySqlCapabilityRepository.save(capabilityEntityMapper.toEntity(capability))
            .map(savedEntity -> {
                Capability savedCapability = capabilityEntityMapper.toDomain(savedEntity);
                savedCapability.setTechnologyIds(capability.getTechnologyIds());
                return savedCapability;
            });
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return mySqlCapabilityRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return mySqlCapabilityRepository.deleteById(id);
    }

    @Override
    public Flux<Capability> findAll() {
        return mySqlCapabilityRepository.findAll()
            .map(capabilityEntityMapper::toDomain);
    }

    @Override
    public Flux<Long> findExistingIds(Collection<Long> capabilityIds) {
        return mySqlCapabilityRepository.findExistingIds(capabilityIds);
    }

    @Override
    public Mono<Void> associateBootcampWithCapabilities(Long bootcampId, List<Long> capabilityIds) {
        List<BootcampCapabilityEntity> bootcampCapabilityEntities = capabilityIds.stream()
            .map(capabilityId -> new BootcampCapabilityEntity(null, bootcampId, capabilityId))
            .toList();

        return mySqlBootcampCapabilityRepository.saveAll(bootcampCapabilityEntities)
            .then();
    }
}
