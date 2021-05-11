package ru.ontology.service.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.ontology.entity.AttributeEntity;
import ru.ontology.entity.AttributeValueEntity;
import ru.ontology.entity.ClassEntity;
import ru.ontology.entity.InstanceEntity;
import ru.ontology.entity.OntologyEntity;
import ru.ontology.entity.RelationEntity;
import ru.ontology.entity.RelationInstanceEntity;
import ru.ontology.repository.AttributeRepository;
import ru.ontology.repository.AttributeValueRepository;
import ru.ontology.repository.ClassRepository;
import ru.ontology.repository.InstanceRepository;
import ru.ontology.repository.OntologyRepository;
import ru.ontology.repository.RelationInstanceRepository;
import ru.ontology.repository.RelationRepository;
import ru.ontology.service.dto.ClassDto;
import ru.ontology.service.dto.CompleteOntologyDto;
import ru.ontology.service.parser.OwlXmlParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OntologyService {
    private final OwlXmlParser xmlOwl2Parser;

    private final OntologyRepository ontologyRepository;
    private final ClassRepository classRepository;
    private final InstanceRepository instanceRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final RelationRepository relationRepository;
    private final RelationInstanceRepository relationInstanceRepository;

    @Transactional
    public void uploadOntology(MultipartFile file) {
        CompleteOntologyDto ontologyDto;

        try {
            ontologyDto = xmlOwl2Parser.parseXmlFile(new String(file.getBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        // saving ontology
        String iri = ontologyDto.getOntologyIri();
        String name = iri.contains(".owl")
                ? iri.substring(iri.lastIndexOf("/") + 1, iri.lastIndexOf(".owl"))
                : iri.substring(iri.lastIndexOf("/") + 1);
        OntologyEntity ontologyEntity = OntologyEntity.builder()
                .iri(iri)
                .name(name)
                .build();
        OntologyEntity ontologyWithId = ontologyRepository.save(ontologyEntity);

        // saving classes
        List<ClassEntity> classEntities = ontologyDto.getClasses().stream()
                .map(x -> ClassEntity.builder()
                        .name(x.getName())
                        .ontology(ontologyWithId)
                        .build())
                .collect(Collectors.toList());
        List<ClassEntity> classesWithId = classRepository.saveAll(classEntities);
        // setting superclasses
        for (ClassEntity classEntity : classesWithId) {
            List<ClassDto> superclassDtos = ontologyDto.getClasses().stream()
                    .filter(x -> x.getName().equals(classEntity.getName()))
                    .findFirst().orElseThrow(NoSuchElementException::new)
                    .getSuperclasses();
            if (superclassDtos != null && !superclassDtos.isEmpty()) {
                List<String> superclassNames = superclassDtos.stream()
                        .map(ClassDto::getName)
                        .collect(Collectors.toList());
                List<ClassEntity> superclassEntities = classRepository.findAll().stream()
                        .filter(x -> superclassNames.contains(x.getName()))
                        .collect(Collectors.toList());
                classEntity.setSuperclasses(superclassEntities);
            }
        }
        classRepository.saveAll(classesWithId);

        // saving attributes
        List<AttributeEntity> attributeEntities = ontologyDto.getAttributes().stream()
                .map(x -> AttributeEntity.builder()
                        .name(x.getName())
                        .domain(classRepository.findByName(x.getDomain().getName())
                                .orElseThrow(NoSuchElementException::new))
                        .range(x.getRangeType())
                        .build())
                .collect(Collectors.toList());
        attributeRepository.saveAll(attributeEntities);

        // saving relations
        List<RelationEntity> relationEntities = ontologyDto.getRelations().stream()
                .map(x -> RelationEntity.builder()
                        .name(x.getName())
                        .domain(classRepository.findByName(x.getDomain().getName())
                                .orElseThrow(NoSuchElementException::new))
                        .range(classRepository.findByName(x.getRange().getName())
                                .orElseThrow(NoSuchElementException::new))
                        .build())
                .collect(Collectors.toList());
        relationRepository.saveAll(relationEntities);

        // saving instances
        List<InstanceEntity> instanceEntities = ontologyDto.getInstances().stream()
                .map(x -> InstanceEntity.builder()
                        .name(x.getName())
                        .typeClass(classRepository.findByName(x.getClassType().getName())
                                .orElseThrow(NoSuchElementException::new))
                        .build())
                .collect(Collectors.toList());
        instanceRepository.saveAll(instanceEntities);

        // saving instance relations
        List<RelationInstanceEntity> relationInstanceEntities = ontologyDto.getRelationInstances().stream()
                .map(x -> RelationInstanceEntity.builder()
                        .relation(relationRepository.findByName(x.getRelation().getName())
                                .orElseThrow(NoSuchElementException::new))
                        .domain(instanceRepository.findByName(x.getDomain().getName())
                                .orElseThrow(NoSuchElementException::new))
                        .range(instanceRepository.findByName(x.getRange().getName())
                                .orElseThrow(NoSuchElementException::new))
                        .build())
                .collect(Collectors.toList());
        relationInstanceRepository.saveAll(relationInstanceEntities);

        // saving attribute values for instances
        List<AttributeValueEntity> attributeValueEntities = ontologyDto.getAttributeValues().stream()
                .map(x -> AttributeValueEntity.builder()
                        .attribute(attributeRepository.findByName(x.getAttribute().getName())
                                .orElseThrow(NoSuchElementException::new))
                        .instance(instanceRepository.findByName(x.getInstance().getName())
                                .orElseThrow(NoSuchElementException::new))
                        .value(x.getValue())
                        .build())
                .collect(Collectors.toList());
        attributeValueRepository.saveAll(attributeValueEntities);
    }

}
