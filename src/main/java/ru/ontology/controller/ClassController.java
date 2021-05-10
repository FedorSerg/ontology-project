package ru.ontology.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.ClassApi;
import org.openapitools.model.ClassViewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Api(tags = "class")
public class ClassController implements ClassApi {

    @Override
    public ResponseEntity<List<ClassViewDto>> getClassList() {
        return ResponseEntity.of(Optional.of(
                Collections.singletonList(new ClassViewDto().name("test"))
        ));
    }
}
