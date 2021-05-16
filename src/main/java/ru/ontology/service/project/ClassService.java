package ru.ontology.service.project;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.AttributeViewDto;
import org.openapitools.model.ClassViewDto;
import org.openapitools.model.RelationViewDto;
import org.springframework.stereotype.Service;
import ru.ontology.entity.ClassEntity;
import ru.ontology.repository.AttributeRepository;
import ru.ontology.repository.ClassRepository;
import ru.ontology.repository.RelationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository classRepository;
    private final AttributeRepository attributeRepository;
    private final RelationRepository relationRepository;

    public List<ClassViewDto> getClassList(Long ontologyId) {
        List<ClassEntity> classesOfOntology = classRepository.findAll().stream()
                .filter(x -> x.getOntology().getId().equals(ontologyId))
                .collect(Collectors.toList());

        return classesOfOntology.stream()
                .map(x -> new ClassViewDto()
                        .id(x.getId())
                        .name(x.getName())
                        .superclassName(x.getSuperclasses().stream()
                                .map(ClassEntity::getName)
                                .collect(Collectors.joining(", ")))
                        .attributes(attributeRepository.findAll().stream()
                                .filter(y -> y.getDomain().getName().equals(x.getName()))
                                .map(y -> new AttributeViewDto()
                                        .id(y.getId())
                                        .name(y.getName())
                                ).collect(Collectors.toList()))
                        .relations(relationRepository.findAll().stream()
                                .filter(y -> y.getDomain().getName().equals(x.getName()))
                                .map(y -> new RelationViewDto()
                                        .name(y.getName())
                                        .domain(y.getDomain().getName())
                                        .range(y.getRange().getName())
                                ).collect(Collectors.toList()))
                ).collect(Collectors.toList());
    }
}
