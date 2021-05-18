package ru.ontology.service.project;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.AttributeValueViewDto;
import org.openapitools.model.InstanceCreateUpdateDto;
import org.openapitools.model.InstanceViewDto;
import org.openapitools.model.RelationInstanceViewDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ontology.entity.ClassEntity;
import ru.ontology.entity.InstanceEntity;
import ru.ontology.repository.AttributeValueRepository;
import ru.ontology.repository.ClassRepository;
import ru.ontology.repository.InstanceRepository;
import ru.ontology.repository.RelationInstanceRepository;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstanceService {
    private final InstanceRepository instanceRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final RelationInstanceRepository relationInstanceRepository;
    private final ClassRepository classRepository;

    @Transactional
    public void createInstance(InstanceCreateUpdateDto dto) {
        ClassEntity classEntity = classRepository.findById(dto.getClassTypeId()).orElse(null);
        instanceRepository.save(InstanceEntity.builder()
                .name(dto.getName())
                .typeClass(classEntity)
                .build());
    }

    @Transactional
    public void updateInstance(InstanceCreateUpdateDto dto) {
        ClassEntity classEntity = classRepository.findById(dto.getClassTypeId()).orElse(null);
        instanceRepository.save(InstanceEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .typeClass(classEntity)
                .build());
    }

    @Transactional
    public void deleteInstance(Long instanceId) {
        instanceRepository.deleteById(instanceId);
    }

    @Transactional(readOnly = true)
    public List<InstanceViewDto> getInstanceList(Long ontologyId) {
        List<InstanceEntity> instancesOfOntology = instanceRepository.findAll().stream()
                .filter(x -> x.getTypeClass().getOntology().getId().equals(ontologyId))
                .collect(Collectors.toList());

        return instancesOfOntology.stream()
                .sorted(Comparator.comparing(InstanceEntity::getId))
                .map(this::mapToView)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InstanceViewDto getInstanceById(Long instanceId) {
        InstanceEntity instance = instanceRepository.findById(instanceId).orElseThrow(NoSuchElementException::new);
        return mapToView(instance);
    }

    private InstanceViewDto mapToView(InstanceEntity instance) {
        return new InstanceViewDto()
                .id(instance.getId())
                .name(instance.getName())
                .classType(instance.getTypeClass().getName())
                .attributes(attributeValueRepository.findAll().stream()
                        .filter(y -> y.getInstance().getName().equals(instance.getName()))
                        .map(y -> new AttributeValueViewDto()
                                .name(y.getAttribute().getName())
                                .value(y.getValue()))
                        .collect(Collectors.toList()))
                .relations(relationInstanceRepository.findAll().stream()
                        .filter(y -> y.getDomain().getName().equals(instance.getName()))
                        .map(y -> new RelationInstanceViewDto()
                                .name(y.getRelation().getName())
                                .classInstanceName(y.getRange().getName()))
                        .collect(Collectors.toList()));
    }
}
