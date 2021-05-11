package ru.ontology.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ontology",
        uniqueConstraints = @UniqueConstraint(columnNames = {"iri", "name"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OntologyEntity {

    @Id
    @SequenceGenerator(name = "ontology_id_seq",
            sequenceName = "ontology_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "ontology_id_seq")
    private Long id;

    private String iri;

    private String name;
}
