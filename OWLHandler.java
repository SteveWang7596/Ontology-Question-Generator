/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.ca.uct.cs.ontologyquestiongenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

/**
 *
 * @author stevewang
 */
public class OWLHandler {
    // Declare an OWL ontology manager and an OWL ontology
    private static OWLOntologyManager manager;
    private static OWLOntology ontology;
    private static OWLDataFactory factory;
    private static OWLReasoner reasoner;
    private static OWLOntologyFormat format; 
    private static PrefixOWLOntologyFormat prefixFormat;
    private static PrefixManager prefixManager;
    private static int currentQuestionType;
    
    public static final String YES_NO_QUESTION_TYPE1 = "Yes-No 2 particular 1 relation";
    public static final String YES_NO_QUESTION_TYPE1_QUANTIFIED = "Yes-No 2 particular 1 relation + quantifier";
    public static final String YES_NO_QUESTION_TYPE2 = "Yes-No 1 particular 1 relation";
    public static final String EQUIVALENCE_QUESTIONS = "Equivalence";
    public static final String TRUE_FALSE_QUESTIONS = "True-False";
    public static final String TRUE_FALSE_QUESTIONS_QUANTIFIED = "True-False + quantifier";
    public static final String WHAT_QUESTIONS_TYPE1 = "What 2 particular 1 relation";
    public static final String WHAT_QUESTIONS_QUANTIFIED = "What 2 particular 1 relation + quantifier";
    public static final String WHAT_QUESTIONS_TYPE2 = "What 1 particular 1 relation";
    public static final String DEFINE_QUESTIONS = "Define";
    
    
    /**
     * Generate a list of questions from a given question template and an ontology
     * @param QuestionTemplate
     * @param OntologyFile
     * @return
     * @throws OWLOntologyCreationException
     * @throws FileNotFoundException 
     */
    static ArrayList<String> generateQuestions(File QuestionTemplate, File OntologyFile) throws OWLOntologyCreationException, FileNotFoundException {
        
        
        // Initialise the manager and the ontology
        manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(OntologyFile);
        factory = manager.getOWLDataFactory();
        reasoner = new Reasoner(ontology);
        format = manager.getOntologyFormat(ontology);
        prefixFormat = format.asPrefixOWLOntologyFormat();
        prefixManager = new DefaultPrefixManager(prefixFormat);
        currentQuestionType = 0;
        
        
        // Declare an new empty arraylist to store the generated questions
        ArrayList<String> generatedQuestions = new ArrayList<String>();
        
        // Declare scanner to read question template file
        Scanner scanner = new Scanner (QuestionTemplate);
        
        if (reasoner.isConsistent()) {
            System.out.println("reasoner is consistent");
            
            // For each line in question template file, generate a question and add it to the arraylist
            while (scanner.hasNext()){
                
                // Read line from the scanner
                String line = scanner.nextLine();
                
                // Declare an output with an empty String
                String newLine = "";
                
                // If it is Yes/No or True/False question with 2 particulars and a single relation
                // then generate question type 1
                if (line.equals(YES_NO_QUESTION_TYPE1) || line.equals(TRUE_FALSE_QUESTIONS)){
                    currentQuestionType = 1;
                    line = scanner.nextLine();
                    
                // If it is Yes/No question with 1 particular and a single relation
                // Then generate Type 2 questions
                }else if (line.equals(YES_NO_QUESTION_TYPE2)){
                    currentQuestionType = 2;
                    line = scanner.nextLine();
                    
                // If it is quantified Yes/No or True/False question with 2 particulars and a single relation
                // Then generate Type 3 question
                }else if (line.equals(YES_NO_QUESTION_TYPE1_QUANTIFIED) || line.equals(TRUE_FALSE_QUESTIONS_QUANTIFIED)){
                    currentQuestionType = 3;
                    line = scanner.nextLine();
                    
                // If it is what questions with 2 particular and a single relation
                // Then generate Type 4 questions
                }else if (line.equals(WHAT_QUESTIONS_TYPE1)){
                    currentQuestionType = 4;
                    line = scanner.nextLine();
                    
                // If it is what question with 1 particular and a single relation 
                // Then generate Type 5 question 
                }else if (line.equals(WHAT_QUESTIONS_TYPE2)){
                    currentQuestionType = 5;
                    line = scanner.nextLine();
                    
                // If it is quantified what questions with 2 particulars and a single relation 
                // Then generate Type 6 question 
                } else if (line.equals(WHAT_QUESTIONS_QUANTIFIED)){
                    currentQuestionType = 6;
                    line = scanner.nextLine();
                
                } else if (line.equals(DEFINE_QUESTIONS) ){
                    currentQuestionType = 7;
                    line = scanner.nextLine();
                    
                // If it is any other question types
                // Then generate normal questions 
                }else if (line.equals(EQUIVALENCE_QUESTIONS)){
                    currentQuestionType=0;
                    line = scanner.nextLine();
                }

                // Generate questions acrrodingly 
                if (currentQuestionType == 1) {
                    newLine = generateYesNoType1(line);
                } else if (currentQuestionType == 2){
                    newLine = generateYesNoType2(line);
                } else if (currentQuestionType == 3){
                    newLine = generateYesNoQuantified(line);
                } else if (currentQuestionType == 4){
                    newLine = generateWhatType1(line);
                } else if (currentQuestionType == 5){
                    newLine = generateWhatType2 (line);
                } else if (currentQuestionType ==6 ){
                    newLine =  generateWhatQuantified(line);
                } else if (currentQuestionType == 7){
                    newLine = generateDefinitionQuestion(line);
                } else if (currentQuestionType ==0){
                    newLine = generateEquivalenceQuestions(line);
                }
                generatedQuestions.add(newLine);
                System.out.println(""+newLine);
            }
            
        } else {
            generatedQuestions.add("Ontology is inconsistent! \n Reasoner cannot be used.");
        }
        
        // Return the resulting arrayList.
        return generatedQuestions;
    }

    private static String generateYesNoType1(String template) {
        String output = "";
        Type1Question q1 = new Type1Question(manager, ontology, factory, reasoner, format, prefixFormat, prefixManager);
        output = q1.generateQuestion(template);
        return output;
    }

    private static String generateYesNoType2(String template) {
        String output = "";
        Type2Question q2 = new Type2Question(manager, ontology, factory, reasoner, format, prefixFormat, prefixManager);
        output = q2.generateQuestion(template);
        return output;
    }
    
    private static String generateYesNoQuantified(String template) {
        String output = "";
        Type3Question q3 = new Type3Question(manager, ontology, factory, reasoner, format, prefixFormat, prefixManager);
        output = q3.generateQuestion(template);
        return output;
    }
    
    private static String generateWhatType1(String template) {
        String output = "";
        Type4Question q4 = new Type4Question(manager, ontology, factory, reasoner, format, prefixFormat, prefixManager);
        output = q4.generateQuestion(template);
        return output;
    }

    private static String generateWhatType2(String template) {
        String output = "";
        Type5Question q5 = new Type5Question(manager, ontology, factory, reasoner, format, prefixFormat, prefixManager);
        output = q5.generateQuestion(template);
        return output;
    }
    
    private static String generateWhatQuantified(String template) {
        String output = "";
        Type6Question q6 = new Type6Question(manager, ontology, factory, reasoner, format, prefixFormat, prefixManager);
        output = q6.generateQuestion(template);
        return output;
    }

    private static String generateDefinitionQuestion(String template) {
        String output = "";
        Type7Question q7 = new Type7Question(manager, ontology, factory, reasoner, format, prefixFormat, prefixManager);
        output = q7.generateQuestion(template);
        return output;
    }

    private static String generateEquivalenceQuestions(String template) {
        String output = "";
        Type0Question q0 = new Type0Question(manager, ontology, factory, reasoner, format, prefixFormat, prefixManager);
        output = q0.generateQuestion(template);
        return output;
    }


}
