package ru.ontology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ontology.entity.RestrictionForRelationEntity;

@Repository
public interface RestrictionForRelationRepository extends JpaRepository<RestrictionForRelationEntity, Long> {
}
