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
@Table(name = "restriction_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestrictionForRelationEntity {

    @Id
    @SequenceGenerator(name = "restriction_relation_id_seq",
            sequenceName = "restriction_relation_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "restriction_relation_id_seq")
    private Long id;

    private String name;

}
