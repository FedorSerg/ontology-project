package ru.ontology.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationDto {
    private String name;
    private ClassDto domain;
    private ClassDto range;
}
