package ru.ontology.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationInstanceDto {

    private RelationDto relation;
    private InstanceDto domain;
    private InstanceDto range;
}
