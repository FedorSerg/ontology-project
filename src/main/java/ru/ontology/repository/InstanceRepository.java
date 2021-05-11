package ru.ontology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ontology.entity.InstanceEntity;

import java.util.Optional;

@Repository
public interface InstanceRepository extends JpaRepository<InstanceEntity, Long> {

    Optional<InstanceEntity> findByName(String name);
}
