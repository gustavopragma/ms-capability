package com.onclass.ms_capability.infrastructure.adapter.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload to associate capabilities with a bootcamp")
public class AssociateBootcampRequestDto {

    @Schema(description = "id of the bootcamp", example = "1")
    @NotNull(message = "bootcamp id is required")
    private Long bootcampId;

    @Schema(description = "List of capability ids to associate", example = "[1, 2]")
    @NotEmpty(message = "capability ids list cannot be empty")
    private List<Long> capabilityIds;
}
