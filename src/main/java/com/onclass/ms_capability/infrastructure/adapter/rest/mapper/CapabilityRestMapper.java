package com.onclass.ms_capability.infrastructure.adapter.rest.mapper;

import com.onclass.ms_capability.domain.model.Capability;
import com.onclass.ms_capability.domain.model.Technology;
import com.onclass.ms_capability.infrastructure.adapter.rest.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CapabilityRestMapper {
    public Capability toDomain(RegisterCapabilityRequestDto registerCapabilityRequestDto) {
        return new Capability(
            null,
            registerCapabilityRequestDto.getName(),
            registerCapabilityRequestDto.getDescription(),
            registerCapabilityRequestDto.getTechnologyIds()
        );
    }

    public CapabilityResponseDto toResponseDto(Capability capability) {
        return new CapabilityResponseDto(
            capability.getId(),
            capability.getName(),
            capability.getDescription(),
            capability.getTechnologyIds()
        );
    }

    public CapabilityWithTechnologiesDto toCapabilityWithTechnologiesDto(Capability capability) {
        return new CapabilityWithTechnologiesDto(
            capability.getId(),
            capability.getName(),
            capability.getDescription(),
            capability.getTechnologies().stream().map(technology ->
                new TechnologySummaryDto(technology.getId(), technology.getName())).toList()
        );
    }

    public BootcampCapabilityResponseDto toBootcampCapabilityResponseDto(Capability capability) {
        return new BootcampCapabilityResponseDto(
            capability.getId(),
            capability.getName(),
            capability.getTechnologies().stream().map(technology ->
                new TechnologySummaryDto(technology.getId(), technology.getName())).toList()
        );
    }
}
