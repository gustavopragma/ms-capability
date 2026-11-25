package com.onclass.ms_capability.application.usecase;

import com.onclass.ms_capability.domain.model.Capability;
import com.onclass.ms_capability.domain.repository.CapabilityRepository;
import com.onclass.ms_capability.domain.repository.TechnologyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ListCapabilitiesByBootcampUseCase {

    private final static int CONCURRENCY_LIMIT = 10;

    private final CapabilityRepository capabilityRepository;
    private final TechnologyRepository technologyRepository;

    public ListCapabilitiesByBootcampUseCase(
        CapabilityRepository capabilityRepository,
        TechnologyRepository technologyRepository
    ) {
        this.capabilityRepository = capabilityRepository;
        this.technologyRepository = technologyRepository;
    }

    public Flux<Capability> getCapabilitiesByBootcampId(Long bootcampId) {
        return capabilityRepository.findAllByBootcampId(bootcampId)
            .flatMap(this::enrichWithTechnologies, CONCURRENCY_LIMIT);
    }

    private Mono<Capability> enrichWithTechnologies(Capability capability) {
        return technologyRepository.findTechnologyByCapabilityId(capability.getId())
            .collectList()
            .map(technologies -> {
                capability.setTechnologies(technologies);
                return capability;
            });

    }
}
