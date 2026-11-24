package com.onclass.ms_capability.infrastructure.adapter.webclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private String code;
    private String description;
}
