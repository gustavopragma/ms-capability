package com.onclass.ms_capability.infrastructure.adapter.r2dbc.mapper;

import com.onclass.ms_capability.domain.model.Capability;
import com.onclass.ms_capability.infrastructure.adapter.r2dbc.entity.CapabilityEntity;
import org.springframework.stereotype.Component;

@Component
public class CapabilityEntityMapper {

    public CapabilityEntity toEntity(Capability capability) {
        return new CapabilityEntity(
            capability.getId(),
            capability.getName(),
            capability.getDescription()
        );
    }

    public Capability toDomain(CapabilityEntity capabilityEntity) {
        return new Capability(
            capabilityEntity.getId(),
            capabilityEntity.getName(),
            capabilityEntity.getDescription(),
            null
        );
    }
}
