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
public class AttributeValueDto {
    private AttributeDto attribute;
    private InstanceDto instance;
    private String value;
}
