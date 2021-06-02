package ru.ontology.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.OntologyApi;
import org.openapitools.model.OntologyCreateUpdateDto;
import org.openapitools.model.OntologyViewDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.ontology.service.project.OntologyService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Api(tags = "ontology")
public class OntologyController implements OntologyApi {

    private final OntologyService service;

    @Override
    public ResponseEntity<Long> createOntology(OntologyCreateUpdateDto dto) {
        return ResponseEntity.of(Optional.of(
                service.createOntology(dto)
        ));
    }

    @Override
    public ResponseEntity<Void> updateOntologyById(Long ontologyId, OntologyCreateUpdateDto dto) {
        service.updateOntology(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteOntologyById(Long ontologyId) {
        service.deleteOntology(ontologyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OntologyViewDto> getOntologyById(Long ontologyId) {
        return ResponseEntity.of(Optional.of(
                service.getOntologyById(ontologyId)
        ));
    }

    @Override
    public ResponseEntity<List<OntologyViewDto>> getOntologyList() {
        return ResponseEntity.of(Optional.of(
                service.getOntologyList()
        ));
    }

    @Override
    public ResponseEntity<Void> uploadOntologyOwlXml(@Valid MultipartFile file) {

        service.uploadOntology(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
