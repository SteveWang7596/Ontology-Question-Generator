/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.ca.uct.cs.ontologyquestiongenerator;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

/**
 *
 * @author stevewang
 */
public class Type7Question {
    
    //Global variables 
    private static OWLOntologyManager manager;
    private static OWLOntology ontology;
    private static OWLDataFactory factory;
    private static OWLReasoner reasoner;
    private static OWLOntologyFormat format; 
    private static PrefixOWLOntologyFormat prefixFormat;
    private static PrefixManager prefixManager;
    private String sentence;
    
    
    /**
     * Constructor for Type 7 questions (Definition Questions)
     * @param manager
     * @param ontology
     * @param factory
     * @param reasoner
     * @param format
     * @param prefixFormat
     * @param prefixManager 
     */
    public Type7Question(OWLOntologyManager manager, OWLOntology ontology,OWLDataFactory factory,OWLReasoner reasoner,OWLOntologyFormat format, PrefixOWLOntologyFormat prefixFormat,PrefixManager prefixManager){
        this.manager = manager;
        this.ontology = ontology;
        this.factory = factory;
        this.reasoner = reasoner;
        this.format = format;
        this.prefixFormat = prefixFormat;
        this.prefixManager = prefixManager;
        sentence = "";
    }
    
    public String generateQuestion (String template){
        // Initialise the output question to ""
        sentence = "";
        
        // Split the template to words and store it to an array
        String[] arr = template.split(" ");
        
        String token = "";
        // For each word in the array
        for (int i=0; i<arr.length; i++){
            String word = arr[i];
            String article = "";
            if (i!=0){
                article = arr[i-1];
            }
            
            // If the word is in the form <word>
            if (word.charAt(0)=='<' && word.charAt(word.length()-1)=='>'){
                
                token = word.substring(1, word.length()-1);
                
                sentence = ProcessToken(sentence, article , token);
                
            // If the word is in the form <word>? or <word>. etc..
            } else if (word.charAt(0)=='<' && word.charAt(word.length()-2)=='>'){
                
                token = word.substring(1, word.length()-2);
                
                sentence = ProcessToken(sentence, article , token) + word.charAt(word.length()-1);
            
            // If word is a normal word and is the first word int he sentence
            }else if (sentence.equals("")){
                sentence = word;
                
            // if word is a normal word and is not the first word in the sentence.
            }else{
                sentence = sentence + " " + word;
            }
        }
        return sentence; 
        
    }

    /**
     * Process tokens and seperate top node with other cases
     * @param sentence
     * @param article
     * @param token
     * @return 
     */
    private String ProcessToken(String sentence, String article, String token) {
        String output = "";
        if (token.equals("Thing")){
            OWLClass thing = reasoner.getTopClassNode().getRepresentativeElement();
            output = sentence + LiguisticHandler.checkArticle(output, article, subToken(thing));
            
        }else{
            OWLClass tokenClass = factory.getOWLClass(token, prefixManager);
            output = sentence + LiguisticHandler.checkArticle(output, article , subToken(tokenClass));
        }
        return output;
    }

    /**
     * substitute OWLClass with any subclass if it is defined in the ontology
     * @param word
     * @return 
     */
    private String subToken(OWLClass word) {
        // Create an empty arraylist to store all valid OWLClasses
        ArrayList <OWLClassExpression> validOWLClasses = new ArrayList<OWLClassExpression>();
        
        
        // Get all subclasses of the token
        NodeSet<OWLClass> subclasses = reasoner.getSubClasses(word, false);
        
        // Create RDFS for the defined by annotation property
        OWLAnnotationProperty isDefinedBy = factory.getRDFSIsDefinedBy();
        
        // For each of the subclasses check if it satifies either one of 3 criteriors:
        for (Node<OWLClass> subclass : subclasses){
            
            // Get all annotations of the subclass
            Set<OWLAnnotation> DefinitionAnnotations = subclass.getRepresentativeElement().getAnnotations(ontology, isDefinedBy);
            
            // Get a set of all equivalence classes
            Set<OWLClassExpression> equivalentClasses= subclass.getRepresentativeElement().getEquivalentClasses(ontology);
            
            // Get a set of all superclasses except OWL:Thing
            Set<OWLClassExpression> superClasses = subclass.getRepresentativeElement().getSuperClasses(ontology);
            superClasses.remove(reasoner.getTopClassNode().getRepresentativeElement());
            
            // Get a set of all subclasses
            Set<OWLClassExpression> suClasses = subclass.getRepresentativeElement().getSubClasses(ontology);
            suClasses.remove(reasoner.getBottomClassNode().getRepresentativeElement());
            
            // If the token has isDefinedBy annotations
            // if token has equivalent classes
            // If token is superclass or subclass of any other class
            if ((!DefinitionAnnotations.isEmpty()) || (!equivalentClasses.isEmpty()) || (!suClasses.isEmpty()) || (!superClasses.isEmpty())){
                
                // Add this class to the arrayList of valid classes
                validOWLClasses.add(subclass.getRepresentativeElement());
            }
            
        }
        
        // randomly select a class 
        if (validOWLClasses.isEmpty()){
            return "No valid classes";
        }else{
            OWLClassExpression selected = randomSelect(validOWLClasses);
            return LiguisticHandler.stringProcess(selected.toString());
        }
    }

    /**
     * Select a random OWLClass from the given list
     * @param validOWLClasses
     * @return 
     */
    private OWLClassExpression randomSelect(ArrayList<OWLClassExpression> validOWLClasses) {
        Random rand = new Random();
        return validOWLClasses.get(rand.nextInt(validOWLClasses.size()));
    }
}
