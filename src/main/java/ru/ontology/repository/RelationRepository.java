package ru.ontology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ontology.entity.RelationEntity;

import java.util.Optional;

@Repository
public interface RelationRepository extends JpaRepository<RelationEntity, Long> {
    Optional<RelationEntity> findByName(String name);
}
