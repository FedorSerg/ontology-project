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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "relation",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelationEntity {

    @Id
    @SequenceGenerator(name = "relation_id_seq",
            sequenceName = "relation_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "relation_id_seq")
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "domain_id")
    private ClassEntity domain;

    @ManyToOne
    @JoinColumn(name = "range_id")
    private ClassEntity range;
}
