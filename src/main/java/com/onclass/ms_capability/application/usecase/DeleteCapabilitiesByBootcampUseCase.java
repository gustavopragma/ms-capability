package com.onclass.ms_capability.application.usecase;

import com.onclass.ms_capability.domain.repository.CapabilityRepository;
import com.onclass.ms_capability.domain.repository.TechnologyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DeleteCapabilitiesByBootcampUseCase {
    private final CapabilityRepository capabilityRepository;
    private final TechnologyRepository technologyRepository;

    public DeleteCapabilitiesByBootcampUseCase(CapabilityRepository capabilityRepository, TechnologyRepository technologyRepository) {
        this.capabilityRepository = capabilityRepository;
        this.technologyRepository = technologyRepository;
    }

    public Mono<Void> deleteByBootcampId(Long bootcampId) {
        if (bootcampId == null) {
            return Mono.empty();
        }

        return capabilityRepository.findCapabilityIdsByBootcampId(bootcampId)
            .distinct()
            .collectList()
            .flatMap(capabilityIdsCandidateToDelete -> {
                if (capabilityIdsCandidateToDelete.isEmpty()) {
                    return Mono.empty();
                }
                return capabilityRepository.deleteAllByBootcampId(bootcampId)
                    .thenMany(Flux.fromIterable(capabilityIdsCandidateToDelete))
                    .filterWhen(capabilityId ->
                        capabilityRepository.countByCapabilityId(capabilityId).map(count -> count == 0))
                    .collectList()
                    .flatMap(deleteCapabilityIds -> {
                        if (deleteCapabilityIds.isEmpty()) {
                            return Mono.empty();
                        }
                        return technologyRepository.deleteTechnologiesByCapabilityIds(deleteCapabilityIds)
                            .then(capabilityRepository.deleteAllById(deleteCapabilityIds));
                    });
            });
    }
}