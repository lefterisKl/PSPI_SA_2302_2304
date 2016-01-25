package Preprocessing;

import Common.Emoticon;
import InputOutput.EmoticonLoader;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


/**
 * This class is responsible for the basic preprocessing for the
 * sentiment analysis.
 * 
 * Things such as URL, mention, stopword, punctuation removal on the
 * raw text of the tweets happen here.
 * 
 * Also the stemming of the words happens here.
 * 
 */
public class Preprocessor {
    
    private HashSet<String>      stopwords;
    private ArrayList<Character> punctiation;

    private ArrayList<Emoticon>  emoticons;
    Stemmer stemmer;

    public Preprocessor() {
        
        stopwords = StopWordGenerator.getStopwords();
        emoticons = EmoticonLoader.loadEmoticons("emoticons.txt");
        stemmer = new Stemmer();
        
        Character[] a = (new Character[]{ ',','.','!','?','"',';','#'});
        punctiation = new ArrayList<>(Arrays.asList(a));        
    }
    
    
    
    private boolean isURL(String str){  
        boolean isUrl = true;
        try {
            URL url = new URL(str);
        } catch (MalformedURLException ex) {
            isUrl = false;
        }
       return isUrl; 
    }
    
    private boolean isNumber(String str){
        return str.matches("\\d*");
    }
    
    private boolean isMention(String str){
        if(str.length() != 0)
            return str.charAt(0) == '@';
        return false;
    }
    
    
    private boolean isEmoticon(String word){
        
        int emoticonsCount = emoticons.size();  
        word = word.trim();
        
        for(int i = 0; i < emoticonsCount; i++)
            if(word.equals(emoticons.get(i).getEmoticon()))
                    return true;
        
        return false;
        
    }
    
    private boolean isPunctiation(char c){
        for(int i = 0; i < punctiation.size();i++)
            if( c == punctiation.get(i))
                return true;
        return false;
    }
    
    private String[] removePunctiation(String word){

        String out = "";
        
        for(int i = 0; i < word.length(); i++){
            if(isPunctiation(word.charAt(i))){
                if(i != 0 && i != word.length() - 1)
                    out += " ";
                
            }
            else
                out += word.charAt(i);
            
        }
        
        String[] words = out.split("\\s");

        return words;
    }
        
    
    private String preprocessWord(String word){
        
        word = word.toLowerCase();
        
        if(isMention(word) || isURL(word) || isNumber(word))
            return "";    
        if(stopwords.contains(word))
            return "";

        if(isEmoticon(word))
            return word;

        return word;
        
    }
    
    
    
    public String preprocessTweet(String tweet){
        
        tweet = tweet.trim();
        String[] words = tweet.trim().split("\\s+");
        String   output = "";
        
        for(String word : words){
            
            //These two must happen in this order.
            String processedWord = preprocessWord(word);
            String[] breaked = removePunctiation(processedWord);
            
            for(int i = 0; i< breaked.length; i++){
             
                processedWord = preprocessWord(breaked[i]);
                
                stemmer.add(processedWord.toCharArray(),processedWord.length());
                stemmer.stem();
                output += preprocessWord(stemmer.toString()) + " ";            
                
            }
                
            
        }
        
        return output;
        
    }
    
    
}
