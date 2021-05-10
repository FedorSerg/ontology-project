package ru.ontology.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOntologyDto {
    private String ontologyIri;

    private List<ClassDto> classes;
    private List<AttributeDto> attributes;
    private List<RelationDto> relations;
}
