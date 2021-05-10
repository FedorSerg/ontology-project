package ru.ontology.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.AttributeApi;
import org.openapitools.model.AttributeViewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Api(tags = "attribute")
public class AttributeController implements AttributeApi {

    @Override
    public ResponseEntity<List<AttributeViewDto>> getAttributeList() {
        return ResponseEntity.of(Optional.of(
                Collections.singletonList(new AttributeViewDto().name("test"))
        ));
    }
}
