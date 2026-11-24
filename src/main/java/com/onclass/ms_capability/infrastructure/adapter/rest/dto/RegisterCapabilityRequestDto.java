package com.onclass.ms_capability.infrastructure.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCapabilityRequestDto {

    @NotBlank(message = "name is required")
    @Size(max = 50, message = "name cannot exceed 50 characters")
    private String name;

    @NotBlank(message = "description is required")
    @Size(max = 90, message = "description cannot exceed 90 characters")
    private String description;

    @NotNull(message = "Technology ids list is required")
    private List<Long> technologyIds;
}
