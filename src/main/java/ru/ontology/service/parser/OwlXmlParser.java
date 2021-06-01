package ru.ontology.service.parser;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.ontology.entity.AttributeRangeType;
import ru.ontology.exception.ParsingException;
import ru.ontology.service.dto.AttributeDto;
import ru.ontology.service.dto.AttributeValueDto;
import ru.ontology.service.dto.ClassDto;
import ru.ontology.service.dto.CompleteOntologyDto;
import ru.ontology.service.dto.InstanceDto;
import ru.ontology.service.dto.RelationDto;
import ru.ontology.service.dto.RelationInstanceDto;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class OwlXmlParser {
    private static final String IRI = "IRI";
    private static final String CLASS = "Class";
    private static final String OBJECT_PROPERTY = "ObjectProperty";
    private static final String DATA_PROPERTY = "DataProperty";

    public CompleteOntologyDto parseXmlFile(String data) {
        CompleteOntologyDto ontology = new CompleteOntologyDto();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(data)));
            doc.getDocumentElement().normalize();

            ontology.setOntologyIri(doc.getDocumentElement().getAttribute("ontologyIRI"));

            NodeList nodeList = doc.getElementsByTagName("Declaration");
            List<ClassDto> classes = new ArrayList<>();
            List<RelationDto> relations = new ArrayList<>();
            List<AttributeDto> attributes = new ArrayList<>();
            List<InstanceDto> instances = new ArrayList<>();

            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // parsing classes
                    Optional<Element> classElement = ofNullable((Element) element.getElementsByTagName(CLASS).item(0));
                    classElement.ifPresent(x -> {
                        String className = x.getAttribute(IRI).replace("#", "");
                        classes.add(ClassDto.builder().name(className).build());
                    });

                    // parsing relations
                    Optional<Element> objectPropertyElement = ofNullable((Element) element.getElementsByTagName(OBJECT_PROPERTY).item(0));
                    objectPropertyElement.ifPresent(x -> {
                        String relationName = x.getAttribute(IRI).replace("#", "");
                        relations.add(RelationDto.builder().name(relationName).build());
                    });

                    // parsing attributes
                    Optional<Element> dataPropertyElement = ofNullable((Element) element.getElementsByTagName(DATA_PROPERTY).item(0));
                    dataPropertyElement.ifPresent(x -> {
                        String attributeName = x.getAttribute(IRI).replace("#", "");
                        attributes.add(AttributeDto.builder().name(attributeName).build());
                    });

                    // parsing instances
                    Optional<Element> instanceElement = ofNullable((Element) element.getElementsByTagName("NamedIndividual").item(0));
                    instanceElement.ifPresent(x -> {
                        String instanceName = x.getAttribute(IRI).replace("#", "");
                        instances.add(InstanceDto.builder().name(instanceName).build());
                    });
                }

            }
            ontology.setClasses(classes);
            ontology.setRelations(relations);
            ontology.setAttributes(attributes);
            ontology.setInstances(instances);
            ontology.setRelationInstances(new ArrayList<>());
            ontology.setAttributeValues(new ArrayList<>());

            nodeList = doc.getElementsByTagName("SubClassOf");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Optional<Element> firstClassElement = ofNullable((Element) element.getElementsByTagName(CLASS).item(0));
                    Optional<Element> secondClassElement = ofNullable((Element) element.getElementsByTagName(CLASS).item(1));
                    Optional<Element> objectProperty = ofNullable((Element) element.getElementsByTagName(OBJECT_PROPERTY).item(0));

                    // parsing subclasses
                    if (firstClassElement.isPresent() && secondClassElement.isPresent() && objectProperty.isEmpty()) {
                        String subclassIri = firstClassElement.get().getAttribute(IRI).replace("#", "");
                        ClassDto subclass = ontology.getClasses().stream()
                                .filter(x -> x.getName().equals(firstClassElement.get().getAttribute(IRI).replace("#", "")))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException("No class was found for subclass by name " + subclassIri));
                        String rangeIri = secondClassElement.get().getAttribute(IRI).replace("#", "");
                        ClassDto superclass = ontology.getClasses().stream()
                                .filter(x -> x.getName().equals(secondClassElement.get().getAttribute(IRI).replace("#", "")))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException("No class was found for superclass by name " + rangeIri));
                        List<ClassDto> superclassList = subclass.getSuperclasses();
                        if (superclassList == null) {
                            superclassList = new ArrayList<>();
                            subclass.setSuperclasses(superclassList);
                        }
                        superclassList.add(superclass);
                    }

                }
            }

            nodeList = doc.getElementsByTagName("DataPropertyDomain");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    Optional<Element> propertyElement = ofNullable((Element) element.getElementsByTagName("DataProperty").item(0));
                    Optional<Element> domainElement = ofNullable((Element) element.getElementsByTagName(CLASS).item(0));

                    if (propertyElement.isPresent() && domainElement.isPresent()) {
                        String domainIri = domainElement.get().getAttribute(IRI).replace("#", "");
                        String propertyIri = propertyElement.get().getAttribute(IRI).replace("#", "");

                        ClassDto domain = ontology.getClasses().stream()
                                .filter(x -> x.getName().equals(domainIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No class was found for attribute domain by name " + domainIri));
                        AttributeDto attribute = ontology.getAttributes().stream()
                                .filter(x -> x.getName().equals(propertyIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No attribute was found by name " + propertyIri));
                        attribute.setDomain(domain);
                    }
                    Optional<Element> datatypeElement = ofNullable((Element) element.getElementsByTagName("Datatype").item(0));

                    if (propertyElement.isPresent() && datatypeElement.isPresent()) {
                        String propertyIri = propertyElement.get().getAttribute(IRI).replace("#", "");
                        String datatypeIri = datatypeElement.get().getAttribute("abbreviatedIRI").replace("xsd:", "");

                        AttributeDto attribute = ontology.getAttributes().stream()
                                .filter(x -> x.getName().equals(propertyIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No attribute was found by name " + propertyIri));
                        if (datatypeIri.contains("int")) {
                            attribute.setRangeType(AttributeRangeType.INTEGER);
                        } else if (datatypeIri.contains("string")) {
                            attribute.setRangeType(AttributeRangeType.TEXT);
                        }

                    }
                }
            }
            nodeList = doc.getElementsByTagName("DataPropertyRange");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    Optional<Element> propertyElement = ofNullable((Element) element.getElementsByTagName("DataProperty").item(0));
                    Optional<Element> datatypeElement = ofNullable((Element) element.getElementsByTagName("Datatype").item(0));

                    if (propertyElement.isPresent() && datatypeElement.isPresent()) {
                        String propertyIri = propertyElement.get().getAttribute(IRI).replace("#", "");
                        String datatypeIri = datatypeElement.get().getAttribute("abbreviatedIRI").replace("xsd:", "");

                        AttributeDto attribute = ontology.getAttributes().stream()
                                .filter(x -> x.getName().equals(propertyIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No attribute was found by name " + propertyIri));
                        if (datatypeIri.contains("int")) {
                            attribute.setRangeType(AttributeRangeType.INTEGER);
                        } else if (datatypeIri.contains("string")) {
                            attribute.setRangeType(AttributeRangeType.TEXT);
                        }
                    }
                }
            }

            nodeList = doc.getElementsByTagName("ObjectPropertyDomain");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    Optional<Element> propertyElement = ofNullable((Element) element.getElementsByTagName(OBJECT_PROPERTY).item(0));
                    Optional<Element> domainElement = ofNullable((Element) element.getElementsByTagName(CLASS).item(0));

                    if (propertyElement.isPresent() && domainElement.isPresent()) {
                        String domainIri = domainElement.get().getAttribute(IRI).replace("#", "");
                        String propertyIri = propertyElement.get().getAttribute(IRI).replace("#", "");

                        ClassDto domain = ontology.getClasses().stream()
                                .filter(x -> x.getName().equals(domainIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No class was found for relation domain by name " + domainIri));
                        RelationDto relation = ontology.getRelations().stream()
                                .filter(x -> x.getName().equals(propertyIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No relation was found by name " + propertyIri));
                        relation.setDomain(domain);
                    }
                }
            }
            nodeList = doc.getElementsByTagName("ObjectPropertyRange");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    Optional<Element> propertyElement = ofNullable((Element) element.getElementsByTagName(OBJECT_PROPERTY).item(0));
                    Optional<Element> rangeElement = ofNullable((Element) element.getElementsByTagName(CLASS).item(0));

                    if (propertyElement.isPresent() && rangeElement.isPresent()) {
                        String rangeIri = rangeElement.get().getAttribute(IRI).replace("#", "");
                        String propertyIri = propertyElement.get().getAttribute(IRI).replace("#", "");

                        ClassDto range = ontology.getClasses().stream()
                                .filter(x -> x.getName().equals(rangeIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No class was found for relation range by name " + rangeIri));
                        RelationDto relation = ontology.getRelations().stream()
                                .filter(x -> x.getName().equals(propertyIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No relation was found by name " + propertyIri));
                        relation.setRange(range);
                    }
                }
            }

            nodeList = doc.getElementsByTagName("ClassAssertion");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    Optional<Element> instanceElement = ofNullable((Element) element.getElementsByTagName("NamedIndividual").item(0));
                    Optional<Element> classElement = ofNullable((Element) element.getElementsByTagName(CLASS).item(0));

                    if (instanceElement.isPresent() && classElement.isPresent()) {
                        String classIri = classElement.get().getAttribute(IRI).replace("#", "");
                        String instanceIri = instanceElement.get().getAttribute(IRI).replace("#", "");

                        ClassDto classDto = ontology.getClasses().stream()
                                .filter(x -> x.getName().equals(classIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No class was found for instance assertion by name " + classIri));
                        InstanceDto instance = ontology.getInstances().stream()
                                .filter(x -> x.getName().equals(instanceIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No instance was found by name " + instanceIri));
                        instance.setClassType(classDto);
                    }
                }
            }

            nodeList = doc.getElementsByTagName("ObjectPropertyAssertion");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    Optional<Element> relationElement = ofNullable((Element) element.getElementsByTagName("ObjectProperty").item(0));
                    Optional<Element> instanceDomainElement = ofNullable((Element) element.getElementsByTagName("NamedIndividual").item(0));
                    Optional<Element> instanceRangeElement = ofNullable((Element) element.getElementsByTagName("NamedIndividual").item(1));

                    if (relationElement.isPresent() && instanceDomainElement.isPresent() && instanceRangeElement.isPresent()) {
                        String relationIri = relationElement.get().getAttribute(IRI).replace("#", "");
                        String domainIri = instanceDomainElement.get().getAttribute(IRI).replace("#", "");
                        String rangeIri = instanceRangeElement.get().getAttribute(IRI).replace("#", "");

                        RelationDto relation = ontology.getRelations().stream()
                                .filter(x -> x.getName().equals(relationIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No relation was found for assertion by name " + relationIri));
                        InstanceDto domain = ontology.getInstances().stream()
                                .filter(x -> x.getName().equals(domainIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No instance was found by name " + domainIri));
                        InstanceDto range = ontology.getInstances().stream()
                                .filter(x -> x.getName().equals(rangeIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No instance was found by name " + rangeIri));

                        ontology.getRelationInstances().add(RelationInstanceDto.builder()
                                .relation(relation)
                                .domain(domain)
                                .range(range)
                                .build());
                    }
                }
            }

            nodeList = doc.getElementsByTagName("DataPropertyAssertion");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    Optional<Element> attributeElement = ofNullable((Element) element.getElementsByTagName("DataProperty").item(0));
                    Optional<Element> instanceElement = ofNullable((Element) element.getElementsByTagName("NamedIndividual").item(0));
                    Optional<Element> valueElement = ofNullable((Element) element.getElementsByTagName("Literal").item(0));

                    if (attributeElement.isPresent() && instanceElement.isPresent() && valueElement.isPresent()) {
                        String attributeIri = attributeElement.get().getAttribute(IRI).replace("#", "");
                        String instanceIri = instanceElement.get().getAttribute(IRI).replace("#", "");
                        String value = valueElement.get().getTextContent().replace("$", "");

                        AttributeDto attribute = ontology.getAttributes().stream()
                                .filter(x -> x.getName().equals(attributeIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No attribute was found for assertion by name " + attributeIri));
                        InstanceDto instance = ontology.getInstances().stream()
                                .filter(x -> x.getName().equals(instanceIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                        "No instance was found by name " + instanceIri));

                        ontology.getAttributeValues().add(AttributeValueDto.builder()
                                .attribute(attribute)
                                .instance(instance)
                                .value(value)
                                .build());
                    }
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParsingException(String.format(
                    "Something went wrong while parsing OWL/XML file: %s", e.getMessage()));
        }

        return ontology;
    }
}
