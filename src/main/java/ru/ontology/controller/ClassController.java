package ru.ontology.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.ClassApi;
import org.openapitools.model.ClassCreateUpdateDto;
import org.openapitools.model.ClassViewDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.ontology.service.project.ClassService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Api(tags = "class")
public class ClassController implements ClassApi {
    private final ClassService service;

    @Override
    public ResponseEntity<List<ClassViewDto>> getClassList(Long ontologyId) {
        return ResponseEntity.of(Optional.of(
                service.getClassList(ontologyId)
        ));
    }

    @Override
    public ResponseEntity<Void> createClass(Long ontologyId, ClassCreateUpdateDto dto) {
        service.createClass(ontologyId, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteClassById(Long ontologyId, Long classId) {
        service.deleteClass(ontologyId, classId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateClassById(Long ontologyId, Long classId, @Valid ClassCreateUpdateDto classCreateUpdateDto) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
