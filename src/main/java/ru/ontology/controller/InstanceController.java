package ru.ontology.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.InstanceApi;
import org.openapitools.model.InstanceCreateUpdateDto;
import org.openapitools.model.InstanceViewDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.ontology.service.project.InstanceService;

import javax.validation.Valid;
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

    @Override
    public ResponseEntity<InstanceViewDto> getInstanceById(Long ontologyId, Long instanceId) {
        return ResponseEntity.of(Optional.of(
                service.getInstanceById(instanceId)
        ));
    }

    @Override
    public ResponseEntity<Void> createInstance(Long ontologyId, @Valid InstanceCreateUpdateDto instanceCreateUpdateDto) {
        service.createInstance(instanceCreateUpdateDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateInstanceById(Long ontologyId, Long instanceId, @Valid InstanceCreateUpdateDto instanceCreateUpdateDto) {
        service.updateInstance(instanceCreateUpdateDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteInstanceById(Long ontologyId, Long instanceId) {
        service.deleteInstance(instanceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
