import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.io.File;
import java.util.Set;

public class OntologyLoader {
    private OWLOntology ontology; // loaded ontology.
    private OWLReasoner reasoner; // for reasoninng over the ontology
    private OWLDataFactory dataFactory; // to create owl api objects.

    public OntologyLoader(String ontologyPath) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager(); // Manages ontology creation, loading, and saving.
        File file = new File(ontologyPath);
        this.ontology = manager.loadOntologyFromOntologyDocument(file);
        this.dataFactory = manager.getOWLDataFactory();
        OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory(); // create a HermiT reasoner
        this.reasoner = reasonerFactory.createReasoner(ontology);
    }
    //getters
    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLReasoner getReasoner() {
        return reasoner;
    }

    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }
    public Set<OWLNamedIndividual> getGamesByCriteria(String genre, String platform, boolean multiplayer, String publisher) {
        OWLClass genreClass = dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#" + genre));
        OWLClass platformClass = dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#" + platform));
        OWLClass publisherClass = dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#" + publisher));

        OWLObjectProperty hasGenre = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#has_genre"));
        OWLObjectProperty playableOn = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#playable_on"));
        OWLObjectProperty publishedBy = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#published_by"));
        OWLDataProperty isMultiplayer = dataFactory.getOWLDataProperty(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#multiplayer"));

        OWLClassExpression criteria = dataFactory.getOWLObjectIntersectionOf(
                dataFactory.getOWLObjectSomeValuesFrom(hasGenre, genreClass),
                dataFactory.getOWLObjectSomeValuesFrom(playableOn, platformClass),
                dataFactory.getOWLObjectSomeValuesFrom(publishedBy, publisherClass),
                dataFactory.getOWLDataHasValue(isMultiplayer, dataFactory.getOWLLiteral(multiplayer))
        );
        //using the reasoner to find instances of the intersection
        NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(criteria, false);
        return individualsNodeSet.getFlattened();
    }


    public Set<OWLNamedIndividual> getGamesByGenre(String genre) {
        OWLClass genreClass = dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#" + genre));
        //OWLClass platformClass = dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#" + platform));

        OWLObjectProperty hasGenre = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#has_genre"));
        OWLObjectProperty playableOn = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#playable_on"));
        OWLDataProperty isMultiplayer = dataFactory.getOWLDataProperty(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#multiplayer"));

        OWLClassExpression criteria = dataFactory.getOWLObjectIntersectionOf(
                dataFactory.getOWLObjectSomeValuesFrom(hasGenre, genreClass)
                //dataFactory.getOWLObjectSomeValuesFrom(playableOn, platformClass)
        );

        NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(criteria, false);
        return individualsNodeSet.getFlattened();
    }

    public String getPublisher(OWLNamedIndividual game) {
        OWLObjectProperty publishedBy = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/denislav/ontologies/2024/3/untitled-ontology-8#published_by"));
        NodeSet<OWLNamedIndividual> publishers = reasoner.getObjectPropertyValues(game, publishedBy);
        if (!publishers.isEmpty()) {
            return getShortForm(publishers.getFlattened().iterator().next().getIRI());
        }
        return "Unknown";
    }

    public String getShortForm(IRI iri) {
        String iriString = iri.toString();
        int index = iriString.lastIndexOf('#');
        if (index != -1) {
            return iriString.substring(index + 1);
        } else {
            index = iriString.lastIndexOf('/');
            if (index != -1) {
                return iriString.substring(index + 1);
            }
        }
        return iriString;
    }
}
