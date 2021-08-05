/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.ca.uct.cs.ontologyquestiongenerator;

import java.util.ArrayList;

/**
 *
 * @author stevewang
 */
public class LiguisticHandler {
    public static String[] wordList (String template){
        String [] words = template.split(" ");
        return words;
    }

    static ArrayList<String> getTokens(String[] words) {
        ArrayList<String> tokens = new ArrayList<>();
        for (String word : words) {
            if (word.charAt(0)=='<' && word.charAt(word.length()-1)=='>'){
                tokens.add(word.substring(1, word.length()-1));
            
            // In cases of tokens at the end of the sentence
            }else if (word.charAt(0)=='<' && word.charAt(word.length()-2)=='>'){
                tokens.add(word.substring(1, word.length()-2));
            }
        }
        return tokens;
    }
    
    /**
     * Process String representations of OWLClass and OWLObjectPorperty to understandable word
     * @param input
     * @return 
     */
    public static String stringProcess (String input){
        String output = "";
        
        // return only chars of the '#'
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int x = input.indexOf('>');
            if (c == '#') {
                output = input.substring(i+1, x);
            }
        }
        
        output = output.replace("-", " ");
        output = output.toLowerCase();
        return output;
    }
    
    /**
     * Assuming all articles in the template is either "The" or "a"
     * replace "a" with "an" if the word follows starts with a vow
     * otherwise append " "+newWord to the sentence
     * and return the new sentence
     * @param output
     * @param newWord
     * @return new sentence
     */
    public static String checkArticle(String output, String article,String newWord) {
        //if the newWord strats with a vow and the article before it is not "The" or "the"
        if (checkVow(newWord) && (article.equals("a")|| article.equals("A") ) ) {
            // Change the article to "an"
            output = output.substring(0, output.length()-1) + "an "+newWord;
        } else if (article.equals("")){
            String firstLetter = ""+newWord.charAt(0);
            output = firstLetter.toUpperCase()+newWord.substring(1);
        }else{
            output = output + " " + newWord; 
        }
        return output;
    }
    
    /**
     * Checks if the given word starts with a vow or not
     * @param word
     * @return boolean: true if word starts with a vow and
     * false if the word doesn't start with a vow
     */
    public static boolean checkVow(String word){
        if (word.charAt(0)=='a' || word.charAt(0)=='e' || word.charAt(0)=='i' || word.charAt(0)=='o' || word.charAt(0)=='u'
                || word.charAt(0)=='A' || word.charAt(0)=='E' || word.charAt(0)=='I' || word.charAt(0)=='O' || word.charAt(0)=='U') {
            return true;
        } else {
            return false;
        }
    }
    
    public static String generateType1Sentence(Type1ResultSet result, String template) {
        // Initiate the output string to noting 
        String line = "";
        
        // Create a String array that contains only the words of the sentence.
        String [] words = template.split(" ");
        
        // Initial the article to nothing 
        String article = "";
        
        // Create integer count to keep track of which part of the result to be used
        int count = 0;
        
        // For each for in the words array 
        for (int i = 0; i<words.length; i++) {
            
            // Update the article to the previous word
            if (i!=0){
                article = words[i-1];
            }
            
            // If the word is a token in the middle of the sentence
            if (words[i].charAt(0)=='<' && words[i].charAt(words[i].length()-1)=='>'){
                
                // Check the article and replace with the correction section of the result 
                line = checkArticle(line, article, subType1Word(count, result));
                count ++;
                
            // If the word is a token at the end of the sentence
            }else if (words[i].charAt(0)=='<' && words[i].charAt(words[i].length()-2)=='>'){
                
                // Check the article and replace with the correction section of the result 
                line = checkArticle(line, article, subType1Word(count, result)) + words[i].charAt(words[i].length()-1);
                count ++;
            
            // If it is the first word in the sentence
            }else if (line.equals("")){
                line = words[i];
            
            // if it is any other word
            }else{
                line = line + " " + words[i];
            }
        }
        
        // Make sure the first word in the sentence is a capital letter
        String firstLetter = ""+line.charAt(0);
        line = firstLetter.toUpperCase()+line.substring(1);
        return line;
    }

    private static String subType1Word(int count, Type1ResultSet result) {
        // Type 1 result set is in the format of :
        // OWLClass X, OWLClass Y, OWLObjectProperty objectProperty
        
        // If count is 0 then return OWLCLass X
        if (count == 0 ) {
            return stringProcess(result.getX().toString());
            
        // If count is 1 then return OWLObjectProperty objectProperty 
        } else if (count == 1 ) {
            return stringProcess(result.getOP().toString());
            
        // If count is 2 the return OWLClass Y
        } else if (count == 2 ) {
            return stringProcess(result.getY().toString());
            
        // If anything else then it is an invalid token
        } else{
            return "<invalid>";
        }
    }

    static String generateType2Sentence(Type2ResultSet result, String template) {
        // Initiate the output string to noting 
        String line = "";
        
        // Create a String array that contains only the words of the sentence.
        String [] words = template.split(" ");
        
        // Initial the article to nothing 
        String article = "";
        
        // Create integer count to keep track of which part of the result to be used
        int count = 0;
        
        // For each for in the words array 
        for (int i = 0; i<words.length; i++) {
            
            // Update the article to the previous word
            if (i!=0){
                article = words[i-1];
            }
            
            // If the word is a token in the middle of the sentence
            if (words[i].charAt(0)=='<' && words[i].charAt(words[i].length()-1)=='>'){
                
                // Check the article and replace with the correction section of the result 
                line = checkArticle(line, article, subType2Word(count, result));
                count ++;
                
            // If the word is a token at the end of the sentence
            }else if (words[i].charAt(0)=='<' && words[i].charAt(words[i].length()-2)=='>'){
                
                // Check the article and replace with the correction section of the result 
                line = checkArticle(line, article, subType2Word(count, result)) + words[i].charAt(words[i].length()-1);
                count ++;
            
            // If it is the first word in the sentence
            }else if (line.equals("")){
                line = words[i];
            
            // if it is any other word
            }else{
                line = line + " " + words[i];
            }
        }
        
        // Make sure the first word in the sentence is a capital letter
        String firstLetter = ""+line.charAt(0);
        line = firstLetter.toUpperCase()+line.substring(1);
        return line;
    }
    
    private static String subType2Word(int count, Type2ResultSet result) {
        // Type 2 result set is in the format of :
        // OWLClass X, OWLClass Y, OWLObjectProperty objectProperty
        
        // If count is 0 then return OWLCLass X
        if (count == 0 ) {
            return stringProcess(result.getX().toString());
            
        // If count is 1 then return OWLClass Y
        } else if (count == 1 ) {
            return stringProcess(result.getY().toString());
            
        // If anything else then it is an invalid token
        } else{
            return "<invalid>";
        }
    }

    public static String generateType3Sentence(Type3ResultSet result, String template) {
        // Initiate the output string to noting 
        String line = "";
        
        // Create a String array that contains only the words of the sentence.
        String [] words = template.split(" ");
        
        // Initial the article to nothing 
        String article = "";
        
        // Create integer count to keep track of which part of the result to be used
        int count = 0;
        
        // For each for in the words array 
        for (int i = 0; i<words.length; i++) {
            
            // Update the article to the previous word
            if (i!=0){
                article = words[i-1];
            }
            
            // If the word is a token in the middle of the sentence
            if (words[i].charAt(0)=='<' && words[i].charAt(words[i].length()-1)=='>'){
                
                // Check the article and replace with the correction section of the result 
                line = checkArticle(line, article, subType3Word(count, result));
                count ++;
                
            // If the word is a token at the end of the sentence
            }else if (words[i].charAt(0)=='<' && words[i].charAt(words[i].length()-2)=='>'){
                
                // Check the article and replace with the correction section of the result 
                line = checkArticle(line, article, subType3Word(count, result)) + words[i].charAt(words[i].length()-1);
                count ++;
            
            // If it is the first word in the sentence
            }else if (line.equals("")){
                line = words[i];
            
            // if it is any other word
            }else{
                line = line + " " + words[i];
            }
        }
        
        // Make sure the first word in the sentence is a capital letter
        String firstLetter = ""+line.charAt(0);
        line = firstLetter.toUpperCase()+line.substring(1);
        return line;
    }
    
    private static String subType3Word(int count, Type3ResultSet result) {
        // Type 3 result set is in the format of :
        // OWLClass X, OWLClass Y, OWLObjectProperty objectProperty, String Quantifier
        
        // If count is 0 then return OWLCLass X
        if (count == 0 ) {
            return stringProcess(result.getX().toString());
            
        // If count is 1 then return OWLObjectProperty objectProperty 
        } else if (count == 1 ) {
            return stringProcess(result.getOP().toString());
            
        // If count is 2 then return String quantifier
        } else if (count == 2 ) {
            return result.getQuantifier();
            
        // If count is 3 the return OWLClass Y
        } else if (count == 3 ) {
            return stringProcess(result.getY().toString());
            
        // If anything else then it is an invalid token
        } else{
            return "<invalid>";
        }
    }

    static String generateType5Sentence(Type5ResultSet result, String template) {
        // Initate the output to an empty String 
        String line = "";
        
        // Create a String array that contains only the words of the sentence.
        String [] words = template.split(" ");
        
        // Initiate article to an empty string
        String article = "";
        
        // Create a counter to keep track of which part of the result to use
        int count = 0;
        
        // For each word in the tempate
        for (int i = 0; i<words.length; i++) {
            
            // Update the article to the previous word
            if (i!=0){
                article = words[i-1];
            }
            
            // For token in the midddle of the sentence 
            if (words[i].charAt(0)=='<' && words[i].charAt(words[i].length()-1)=='>'){
                
                // Check the article and replace token with the appropriate part of the result
                line = checkArticle(line, article, subType5Word(count, result));
                count ++;
                
            // For token at the end of the sentence
            }else if (words[i].charAt(0)=='<' && words[i].charAt(words[i].length()-2)=='>'){
                
                // Check the article and replace token with the appropriate part of the result
                line = checkArticle(line, article, subType5Word(count, result)) + words[i].charAt(words[i].length()-1);
                count ++;
                
            // If it is the first word of the sentence
            }else if (line.equals("")){
                line = words[i];
            
            // If it is any other word
            }else{
                
                line = line + " " + words[i];
            }
        }
        
        // Make sure that the first word of the sentence is a capital letter
        String firstLetter = ""+line.charAt(0);
        line = firstLetter.toUpperCase()+line.substring(1);
        return line;
    }

    private static String subType5Word(int count, Type5ResultSet result) {
        // Type 5 result set in the format: 
        // OWLClass X, OWLObjectProject objectProperty
        
        // If count is 0 then return OWLClass X
        if (count == 0 ) {
            return stringProcess(result.getX().toString());
        
        // If count is 1 then return OWLObjectProperty objectProperty
        } else if (count == 1 ) {
            return stringProcess(result.getOP().toString());
            
        // If count is anything else, then token is invalid.
        } else{
            return "<invalid>";
        }
    }

    static String generateType0Sentence(Type0ResultSet resultSet, String template) {
        // Initate the output to an empty String 
        String line = "";
        
        // Create a String array that contains only the words of the sentence.
        String [] words = template.split(" ");
        
        // Initiate article to an empty string
        String article = "";
        
        // Create a counter to keep track of which part of the result to use
        int count = 0;
        
        // For each word in the tempate
        for (int i = 0; i<words.length; i++) {
            
            // Update the article to the previous word
            if (i!=0){
                article = words[i-1];
            }
            
            // For token in the midddle of the sentence 
            if (words[i].charAt(0)=='<' && words[i].charAt(words[i].length()-1)=='>'){
                
                // Check the article and replace token with the appropriate part of the result
                line = checkArticle(line, article, subType0Word(count, resultSet));
                count ++;
                
            // For token at the end of the sentence
            }else if (words[i].charAt(0)=='<' && words[i].charAt(words[i].length()-2)=='>'){
                
                // Check the article and replace token with the appropriate part of the result
                line = checkArticle(line, article, subType0Word(count, resultSet)) + words[i].charAt(words[i].length()-1);
                count ++;
                
            // If it is the first word of the sentence
            }else if (line.equals("")){
                line = words[i];
            
            // If it is any other word
            }else{
                
                line = line + " " + words[i];
            }
        }
        
        // Make sure that the first word of the sentence is a capital letter
        String firstLetter = ""+line.charAt(0);
        line = firstLetter.toUpperCase()+line.substring(1);
        return line;
    }

    private static String subType0Word(int count, Type0ResultSet resultSet) {
        // Type 5 result set in the format: 
        // OWLClass X, OWLObjectProject objectProperty
        
        // If count is 0 then return OWLClass X
        if (count == 0 ) {
            return stringProcess(resultSet.getX().toString());
        
        // If count is 1 then return OWLObjectProperty objectProperty
        } else if (count == 1 ) {
            return stringProcess(resultSet.getY().toString());
            
        // If count is anything else, then token is invalid.
        } else{
            return "<invalid>";
        }
    }
    
}
