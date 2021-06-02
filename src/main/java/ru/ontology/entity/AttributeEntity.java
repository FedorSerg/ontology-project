package ru.ontology.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "attribute")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeEntity {

    @Id
    @SequenceGenerator(name = "attribute_id_seq",
            sequenceName = "attribute_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "attribute_id_seq")
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "domain_id")
    private ClassEntity domain;

    @Enumerated(EnumType.STRING)
    private AttributeRangeType range;

    @ManyToOne
    @JoinColumn(name = "ontology_id")
    private OntologyEntity ontology;
}
