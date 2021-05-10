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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;

@Entity
@Table(name = "class",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassEntity {

    @Id
    @SequenceGenerator(name = "class_id_seq",
            sequenceName = "class_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "class_id_seq")
    private Long id;

    private String name;

    @ManyToMany
    private List<RestrictionEntity> restrictions;

    @ManyToMany
    private List<ClassEntity> superclasses;

    @ManyToOne
    @JoinColumn(name = "ontology_id")
    private OntologyEntity ontology;
}
