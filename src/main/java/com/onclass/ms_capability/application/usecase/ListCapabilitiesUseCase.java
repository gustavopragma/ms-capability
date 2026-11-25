package com.onclass.ms_capability.application.usecase;

import com.onclass.ms_capability.domain.exception.BusinessException;
import com.onclass.ms_capability.domain.exception.ErrorCode;
import com.onclass.ms_capability.domain.model.Capability;
import com.onclass.ms_capability.domain.repository.CapabilityRepository;
import com.onclass.ms_capability.domain.repository.TechnologyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;


public class ListCapabilitiesUseCase {

    private static final Integer CONCURRENCY_LIMIT = 10;
    private static final String NAME_PARAM = "name";
    private static final String TECHNOLOGY_COUNT_PARAM = "technologyCount";
    private static final String ASC_SORT_DIRECTION = "ASC";
    private static final String DESC_SORT_DIRECTION = "DESC";

    private final CapabilityRepository capabilityRepository;
    private final TechnologyRepository technologyRepository;

    public ListCapabilitiesUseCase(
        CapabilityRepository capabilityRepository,
        TechnologyRepository technologyRepository) {
        this.capabilityRepository = capabilityRepository;
        this.technologyRepository = technologyRepository;
    }

    public Flux<Capability> getCapabilities(Integer page, Integer size, String sortBy, String sortDirection) {
        if (page == null || size == null || page < 0 || size <= 0) {
            return Flux.error(new BusinessException(ErrorCode.INVALID_PAGINATION_PARAMS));
        }
        if (!NAME_PARAM.equalsIgnoreCase(sortBy) && !TECHNOLOGY_COUNT_PARAM.equalsIgnoreCase(sortBy)) {
            return Flux.error(new BusinessException(ErrorCode.INVALID_SORT_PARAMS));
        }
        if (!ASC_SORT_DIRECTION.equalsIgnoreCase(sortDirection) && !DESC_SORT_DIRECTION.equalsIgnoreCase(sortDirection)) {
            return Flux.error(new BusinessException(ErrorCode.INVALID_SORT_PARAMS));
        }

        return capabilityRepository.findAll()
            .flatMap(this::enrichWithTechnologies, CONCURRENCY_LIMIT)
            .sort(buildComparator(sortBy, sortDirection))
            .skip((long) page * size)
            .take(size);
    }

    private Mono<Capability> enrichWithTechnologies(Capability capability) {
        return technologyRepository.findTechnologyByCapabilityId(capability.getId())
            .collectList()
            .map(technologies -> {
                capability.setTechnologies(technologies);
                return capability;
            });
    }

    private Comparator<Capability> buildComparator(String sortBy, String sortDirection) {
        Comparator<Capability> comparator;

        if(TECHNOLOGY_COUNT_PARAM.equalsIgnoreCase(sortBy)){
            comparator = Comparator.comparingInt(capability ->
                capability.getTechnologies() == null ? 0 : capability.getTechnologies().size());
        } else {
            comparator =  Comparator.comparing(capability ->
                capability.getName(), String.CASE_INSENSITIVE_ORDER);
        }

        if (DESC_SORT_DIRECTION.equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}
