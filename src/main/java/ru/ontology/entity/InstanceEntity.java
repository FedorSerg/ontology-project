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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "instance")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstanceEntity {

    @Id
    @SequenceGenerator(name = "instance_id_seq",
            sequenceName = "instance_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "instance_id_seq")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "instance_superclasses")
    private List<ClassEntity> classes;

    @ManyToOne
    @JoinColumn(name = "ontology_id")
    private OntologyEntity ontology;
}
