package ru.ontology.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ontology_prefix")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OntologyPrefixEntity {

    @Id
    @SequenceGenerator(name = "ontology_prefix_id_seq",
            sequenceName = "ontology_prefix_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "ontology_prefix_id_seq")
    private Long id;

    private String name;

    private String iri;

    @ManyToOne
    @JoinColumn(name = "ontology_id")
    private OntologyEntity ontology;
}
