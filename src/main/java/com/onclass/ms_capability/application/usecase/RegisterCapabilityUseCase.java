package com.onclass.ms_capability.application.usecase;

import com.onclass.ms_capability.domain.exception.BusinessException;
import com.onclass.ms_capability.domain.exception.ErrorCode;
import com.onclass.ms_capability.domain.model.Capability;
import com.onclass.ms_capability.domain.repository.CapabilityRepository;
import com.onclass.ms_capability.domain.repository.TechnologyRepository;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegisterCapabilityUseCase {

    private static final int MIN_TECHNOLOGIES = 3;
    private static final int MAX_TECHNOLOGIES = 20;

    private CapabilityRepository capabilityRepository;
    private TechnologyRepository technologyRepository;

    public RegisterCapabilityUseCase(CapabilityRepository capabilityRepository, TechnologyRepository technologyRepository) {
        this.capabilityRepository = capabilityRepository;
        this.technologyRepository = technologyRepository;
    }

    public Mono<Capability> registerCapability(Capability capability) {
        List<Long> techIds = capability.getTechnologyIds();

        if (techIds == null) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_TECHNOLOGY_COUNT));
        }

        Set<Long> uniqueIds = new HashSet<>(techIds);
        if (uniqueIds.size() != techIds.size()) {
            return Mono.error(new BusinessException(ErrorCode.DUPLICATE_TECHNOLOGIES));
        }

        if (uniqueIds.size() < MIN_TECHNOLOGIES || uniqueIds.size() > MAX_TECHNOLOGIES) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_TECHNOLOGY_COUNT));
        }

        return capabilityRepository.existsByName(capability.getName())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new BusinessException(ErrorCode.CAPABILITY_ALREADY_EXISTS));
                }
                return capabilityRepository.save(capability);
            })
            .flatMap(savedCapability ->
                technologyRepository.associateTechnologies(savedCapability.getId(), techIds)
                    .thenReturn(savedCapability)
                    .onErrorResume(error ->
                        capabilityRepository.deleteById(savedCapability.getId())
                            .then(Mono.error(error))
                    )
            );
    }
}
