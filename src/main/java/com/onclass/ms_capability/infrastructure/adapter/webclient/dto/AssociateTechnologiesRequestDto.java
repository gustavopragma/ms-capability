package com.onclass.ms_capability.infrastructure.adapter.webclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AssociateTechnologiesRequestDto {
    private Long capabilityId;
    private List<Long> technologyIds;
}
