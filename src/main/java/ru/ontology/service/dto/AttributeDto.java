package ru.ontology.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ontology.entity.AttributeRangeType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeDto {
    private String name;
    private ClassDto domain;
    private AttributeRangeType rangeType;
}
