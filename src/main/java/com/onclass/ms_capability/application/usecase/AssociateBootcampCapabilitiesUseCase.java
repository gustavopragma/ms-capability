package com.onclass.ms_capability.application.usecase;

import com.onclass.ms_capability.domain.exception.BusinessException;
import com.onclass.ms_capability.domain.exception.ErrorCode;
import com.onclass.ms_capability.domain.repository.CapabilityRepository;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AssociateBootcampCapabilitiesUseCase {
    private CapabilityRepository capabilityRepository;

    public AssociateBootcampCapabilitiesUseCase(CapabilityRepository capabilityRepository) {
        this.capabilityRepository = capabilityRepository;
    }

    public Mono<Void> associate(Long bootcampId, List<Long> capabilityIds) {
        Set<Long> uniqueIds = new HashSet<>(capabilityIds);

        return capabilityRepository.findExistingIds(uniqueIds)
            .collect(Collectors.toSet())
            .flatMap(existingIds -> {
                if(existingIds.size() != uniqueIds.size()) {
                    Set<Long> missingIds = new HashSet<>(uniqueIds);
                    missingIds.removeAll(existingIds);
                    return Mono.error(
                        new BusinessException(ErrorCode.CAPABILITY_NOT_FOUND,
                            "Capabilities not found with ids: " + missingIds));
                }

                return capabilityRepository.associateBootcampWithCapabilities(bootcampId, List.copyOf(uniqueIds));
            });
    }
}
