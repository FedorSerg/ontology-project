package ru.ontology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ontology.entity.OntologyEntity;

@Repository
public interface OntologyRepository extends JpaRepository<OntologyEntity, Long> {
}
