package com.onclass.ms_capability.infrastructure.adapter.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("bootcamp_capabilities")
public class BootcampCapabilityEntity {
    @Id
    private Long id;

    @Column("id_bootcamp")
    private Long bootcampId;

    @Column("id_capability")
    private Long capabilityId;
}
