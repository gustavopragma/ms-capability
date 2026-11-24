package com.onclass.ms_capability.infrastructure.adapter.rest.mapper;

import com.onclass.ms_capability.domain.model.Capability;
import com.onclass.ms_capability.infrastructure.adapter.rest.dto.CapabilityResponseDto;
import com.onclass.ms_capability.infrastructure.adapter.rest.dto.RegisterCapabilityRequestDto;
import org.springframework.stereotype.Component;

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
}
