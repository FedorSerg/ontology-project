package ru.ontology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ontology.entity.ClassEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {

    Optional<ClassEntity> findByName(String name);

    @Query(value = "select * from class where name in :classNames",
            nativeQuery = true)
    List<ClassEntity> findAllByNames(@Param("classNames") Iterable<String> names);
}
