package ru.ontology.entity;

import lombok.Getter;

public enum AttributeRangeType {
    INTEGER("http://www.w3.org/2001/XMLSchema#int"),
    TEXT("http://www.w3.org/2001/XMLSchema#string");

    AttributeRangeType(String abbreviatedIRI) {
        this.abbreviatedIRI = abbreviatedIRI;
    }

    @Getter
    private final String abbreviatedIRI;
}
