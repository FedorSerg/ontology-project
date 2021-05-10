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
@Table(name = "relation_for_instances")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelationForInstancesEntity {
    @Id
    @SequenceGenerator(name = "relation_for_instances_id_seq",
            sequenceName = "relation_for_instances_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "relation_for_instances_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "relation_id")
    private RelationEntity relation;

    @ManyToOne
    @JoinColumn(name = "domain_id")
    private InstanceEntity domain;

    @ManyToOne
    @JoinColumn(name = "range_id")
    private InstanceEntity range;
}
