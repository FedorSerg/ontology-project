package ru.ontology.service.project;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.AttributeViewDto;
import org.openapitools.model.ClassCreateUpdateDto;
import org.openapitools.model.ClassViewDto;
import org.openapitools.model.RelationViewDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.ontology.entity.AttributeEntity;
import ru.ontology.entity.AttributeRangeType;
import ru.ontology.entity.ClassEntity;
import ru.ontology.entity.OntologyEntity;
import ru.ontology.entity.RelationEntity;
import ru.ontology.repository.AttributeRepository;
import ru.ontology.repository.ClassRepository;
import ru.ontology.repository.OntologyRepository;
import ru.ontology.repository.RelationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository classRepository;
    private final AttributeRepository attributeRepository;
    private final RelationRepository relationRepository;
    private final OntologyRepository ontologyRepository;

    @Transactional
    public void deleteClass(Long ontologyId, Long classId) {
        classRepository.deleteById(classId);
    }

    @Transactional
    public void createClass(Long ontologyId, ClassCreateUpdateDto dto) {
        OntologyEntity ontologyEntity = ontologyRepository.findById(ontologyId).orElseThrow();

        ClassEntity entity = ClassEntity.builder()
                .ontology(ontologyEntity)
                .name(dto.getName())
                .superclasses(Collections.emptyList())
                .build();
        ClassEntity entityWithId = classRepository.save(entity);

        List<String> relationNames = Arrays.asList(StringUtils.delimitedListToStringArray(
                dto.getRelations().replace(" ", ""), ","));
        List<String> attributeNames = Arrays.asList(StringUtils.delimitedListToStringArray(
                dto.getAttributes().replace(" ", ""), ","));

        for (String name : relationNames) {
            RelationEntity relationEntity = relationRepository.findByName(name).orElse(
                    RelationEntity.builder().ontology(ontologyEntity)
                            .name(name).domain(entityWithId).range(entityWithId).build()
            );
            relationRepository.save(relationEntity);
        }
        for (String name : attributeNames) {
            AttributeEntity attributeEntity = attributeRepository.findByName(name).orElse(
                    AttributeEntity.builder().ontology(ontologyEntity)
                            .name(name).domain(entityWithId).range(AttributeRangeType.TEXT).build()
            );
            attributeRepository.save(attributeEntity);
        }
    }

    @Transactional(readOnly = true)
    public List<ClassViewDto> getClassList(Long ontologyId) {
        List<ClassEntity> classesOfOntology = classRepository.findAll().stream()
                .filter(x -> x.getOntology().getId().equals(ontologyId))
                .collect(Collectors.toList());

        return classesOfOntology.stream()
                .sorted(Comparator.comparing(ClassEntity::getId))
                .map(this::mapToView)
                .collect(Collectors.toList());
    }

    private ClassViewDto mapToView(ClassEntity classEntity) {
        List<Long> classAndItsSuperclassesIds = getAllSuperclassesIds(classEntity);

        return new ClassViewDto()
                .id(classEntity.getId())
                .name(classEntity.getName())
                .superclassName(classEntity.getSuperclasses().stream()
                        .map(ClassEntity::getName)
                        .collect(Collectors.joining(", ")))
                .attributes(attributeRepository.findAll().stream()
                        .filter(y -> classAndItsSuperclassesIds.contains(y.getDomain().getId()))
                        .map(y -> new AttributeViewDto()
                                .id(y.getId())
                                .name(y.getName())
                                .range(y.getRange().toString().toLowerCase())
                        ).collect(Collectors.toList()))
                .relations(relationRepository.findAll().stream()
                        .filter(y -> classAndItsSuperclassesIds.contains(y.getDomain().getId()))
                        .map(y -> new RelationViewDto()
                                .name(y.getName())
                                .domain(y.getDomain().getName())
                                .range(y.getRange().getName())
                        ).collect(Collectors.toList()));
    }

    private List<Long> getAllSuperclassesIds(ClassEntity theClass) {
        List<Long> result = new ArrayList<>();
        if (theClass.getSuperclasses() != null) {
            for (ClassEntity superclass : theClass.getSuperclasses()) {
                result.addAll(getAllSuperclassesIds(superclass));
            }
        }
        result.add(theClass.getId());
        return result;
    }
}
