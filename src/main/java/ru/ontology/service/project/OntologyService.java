package ru.ontology.service.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.ontology.service.dto.CompleteOntologyDto;
import ru.ontology.service.parser.OwlXmlParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class OntologyService {
    private final OwlXmlParser xmlOwl2Parser;

    @Transactional
    public void uploadOntology(MultipartFile file) {
        CompleteOntologyDto ontologyDto;

        try {
            ontologyDto = xmlOwl2Parser.parseXmlFile(new String(file.getBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        System.out.println(ontologyDto);
    }
}
