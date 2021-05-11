package ru.ontology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ontology.entity.AttributeEntity;

import java.util.Optional;

@Repository
public interface AttributeRepository extends JpaRepository<AttributeEntity, Long> {

    Optional<AttributeEntity> findByName(String name);
}
