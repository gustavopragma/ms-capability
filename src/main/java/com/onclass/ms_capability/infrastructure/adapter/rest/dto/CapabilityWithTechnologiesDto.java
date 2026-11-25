package com.onclass.ms_capability.infrastructure.adapter.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapabilityWithTechnologiesDto {
    private Long id;
    private String name;
    private String description;
    private List<TechnologySummaryDto> technologies;
}
