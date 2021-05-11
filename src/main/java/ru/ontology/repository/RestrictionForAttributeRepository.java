package ru.ontology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ontology.entity.RestrictionForAttributeEntity;

@Repository
public interface RestrictionForAttributeRepository extends JpaRepository<RestrictionForAttributeEntity, Long> {
}
