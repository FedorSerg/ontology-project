package ru.ontology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ontology.entity.RelationInstanceEntity;

@Repository
public interface RelationInstanceRepository extends JpaRepository<RelationInstanceEntity, Long> {
}
