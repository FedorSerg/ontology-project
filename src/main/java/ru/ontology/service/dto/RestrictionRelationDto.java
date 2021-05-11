package ru.ontology.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestrictionRelationDto {
    private RelationDto domain;
    private CardinalityRule cardinalityRule;
    private String value;
}
