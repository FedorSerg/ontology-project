package ru.ontology.service.parser;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.ontology.exception.ParsingException;
import ru.ontology.service.dto.ClassDto;
import ru.ontology.service.dto.CompleteOntologyDto;
import ru.ontology.service.dto.RelationDto;

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

            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // parsing classes
                    Optional<Element> classElement = ofNullable((Element) element.getElementsByTagName(CLASS).item(0));
                    classElement.ifPresent(x -> {
                        String className = x.getAttribute(IRI);
                        classes.add(ClassDto.builder().name(className).build());
                    });

                    // parsing attributes
                    Optional<Element> propertyObjElement = ofNullable((Element) element.getElementsByTagName(OBJECT_PROPERTY).item(0));
                    propertyObjElement.ifPresent(x -> {
                        String relationName = x.getAttribute(IRI);
                        relations.add(RelationDto.builder().name(relationName).build());
                    });
                }

                // TODO
            }
            ontology.setClasses(classes);
            ontology.setRelations(relations);

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
                        String subclassIri = firstClassElement.get().getAttribute(IRI);
                        ClassDto subclass = ontology.getClasses().stream()
                                .filter(x -> x.getName().equals(firstClassElement.get().getAttribute(IRI)))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException("No class was found for subclass by name " + subclassIri));
                        String rangeIri = secondClassElement.get().getAttribute(IRI);
                        ClassDto superclass = ontology.getClasses().stream()
                                .filter(x -> x.getName().equals(secondClassElement.get().getAttribute(IRI)))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException("No class was found for superclass by name " + rangeIri));
                        List<ClassDto> superclassList = subclass.getSuperclasses();
                        if (superclassList == null) {
                            superclassList = new ArrayList<>();
                            subclass.setSuperclasses(superclassList);
                        }
                        superclassList.add(superclass);
                    }

                    // parsing relation domain and range
                    if (firstClassElement.isPresent() && secondClassElement.isPresent() && objectProperty.isPresent()) {
                        String domainIri = firstClassElement.get().getAttribute(IRI);
                        ClassDto domain = ontology.getClasses().stream()
                                .filter(x -> x.getName().equals(domainIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException("No class was found for attribute domain by name " + domainIri));
                        String rangeIri = secondClassElement.get().getAttribute(IRI);
                        ClassDto range = ontology.getClasses().stream()
                                .filter(x -> x.getName().equals(rangeIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException("No class was found for attribute range by name " + rangeIri));
                        String relationIri = objectProperty.get().getAttribute(IRI);
                        RelationDto relationDto = ontology.getRelations().stream()
                                .filter(x -> x.getName().equals(relationIri))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException("No relation was found for name " + relationIri));
                        relationDto.setDomain(domain);
                        relationDto.setRange(range);
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
