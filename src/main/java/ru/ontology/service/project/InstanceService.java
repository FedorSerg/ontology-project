package ru.ontology.service.project;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.AttributeValueViewDto;
import org.openapitools.model.InstanceViewDto;
import org.openapitools.model.RelationInstanceViewDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ontology.entity.InstanceEntity;
import ru.ontology.repository.AttributeValueRepository;
import ru.ontology.repository.InstanceRepository;
import ru.ontology.repository.RelationInstanceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstanceService {
    private final InstanceRepository instanceRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final RelationInstanceRepository relationInstanceRepository;

    @Transactional(readOnly = true)
    public List<InstanceViewDto> getInstanceList(Long ontologyId) {
        List<InstanceEntity> instancesOfOntology = instanceRepository.findAll().stream()
                .filter(x -> x.getTypeClass().getOntology().getId().equals(ontologyId))
                .collect(Collectors.toList());

        return instancesOfOntology.stream()
                .map(x -> new InstanceViewDto()
                        .name(x.getName())
                        .classType(x.getTypeClass().getName())
                        .attributes(attributeValueRepository.findAll().stream()
                                .filter(y -> y.getInstance().getName().equals(x.getName()))
                                .map(y -> new AttributeValueViewDto()
                                        .name(y.getAttribute().getName())
                                        .value(y.getValue()))
                                .collect(Collectors.toList()))
                        .relations(relationInstanceRepository.findAll().stream()
                                .filter(y -> y.getDomain().getName().equals(x.getName()))
                                .map(y -> new RelationInstanceViewDto()
                                        .name(y.getRelation().getName())
                                        .classInstanceName(y.getRange().getName()))
                                .collect(Collectors.toList()))
                ).collect(Collectors.toList());
    }
}
