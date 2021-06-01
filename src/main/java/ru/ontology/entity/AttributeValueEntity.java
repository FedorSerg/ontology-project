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
@Table(name = "attribute_value")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeValueEntity {

    @Id
    @SequenceGenerator(name = "attribute_value_id_seq",
            sequenceName = "attribute_value_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "attribute_value_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private AttributeEntity attribute;

    @ManyToOne
    @JoinColumn(name = "instance_id")
    private InstanceEntity instance;

    private String value;

    @ManyToOne
    @JoinColumn(name = "ontology_id")
    private OntologyEntity ontology;
}
