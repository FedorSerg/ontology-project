package ru.ontology;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@Slf4j
@SuppressWarnings("HideUtilityClassConstructor")
@EntityScan(basePackages = {"ru.ontology.entity"})
public class OntologyApplication {

    public static void main(final String[] args) {
        SpringApplication.run(OntologyApplication.class, args);
        log.info("http://localhost:8080/swagger-ui/");
    }
}
