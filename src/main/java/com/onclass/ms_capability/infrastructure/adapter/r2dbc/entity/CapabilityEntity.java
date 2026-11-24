package com.onclass.ms_capability.infrastructure.adapter.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("capabilities")
public class CapabilityEntity {
    @Id
    private Long id;
    private String name;
    private String description;
}
