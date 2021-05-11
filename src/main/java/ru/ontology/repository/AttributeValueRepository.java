package ru.ontology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ontology.entity.AttributeValueEntity;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValueEntity, Long> {
}
