package com.onclass.ms_capability.infrastructure.adapter.webclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTechnologiesByCapabilitiesRequestDto {
    private List<Long> capabilityIds;
}
