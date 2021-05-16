package ru.ontology.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.InstanceApi;
import org.openapitools.model.InstanceViewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.ontology.service.project.InstanceService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Api(tags = "instance")
public class InstanceController implements InstanceApi {
    private final InstanceService service;

    @Override
    public ResponseEntity<List<InstanceViewDto>> getInstanceList(Long ontologyId) {
        return ResponseEntity.of(Optional.of(
                service.getInstanceList(ontologyId)
        ));
    }
}
