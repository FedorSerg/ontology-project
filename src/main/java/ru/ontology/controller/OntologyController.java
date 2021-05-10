package ru.ontology.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.OntologyApi;
import org.openapitools.model.OntologyViewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Api(tags = "ontology")
public class OntologyController implements OntologyApi {

    @Override
    public ResponseEntity<List<OntologyViewDto>> getOntologyList() {
        return ResponseEntity.of(Optional.of(
                Collections.singletonList(new OntologyViewDto().name("test"))
        ));
    }
}
