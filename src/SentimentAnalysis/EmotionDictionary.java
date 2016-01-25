package SentimentAnalysis;

import Common.Emotion;
import InputOutput.WordLoader;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class uses the JAWS api to find synonyms for the secondary emotions.
 * These words will be used to assign sentiment scores for all the tweets.
 * 
 */
public class EmotionDictionary {
    
    HashMap<Emotion,HashSet<String>> data;

    public EmotionDictionary() {
        data = getSecondaryEmotions();
    }
    
    public Emotion representativeEmotion(String word){
        
        for(Emotion em : data.keySet()){
            if(data.get(em).contains(word))
                return em;
        }
       
        return Emotion.NO_EMOTION;
    }
    
    /*
        remove some nonprintable characters found in the words 
        of the WordNet database.
    */
    public static String removeNonPrintable(String x)
    {
        String filtered="";
        for(int i=0;i<x.length();i++)
        {
           
            if(32<=x.codePointAt(i) && x.codePointAt(i)<127)
            {
                filtered += x.charAt(i);
            }
        }
        return filtered;
    }
    
    /*
        return a hashmap with where key is a primary Emotion (i.e anger,joy.. ) and the value is 
        a set with all the synonyms that was found for the secondary emotions. For completeness,
        we also     add their relative hyponyms.
    */
    
    private static HashMap<Emotion,HashSet<String>> getSecondaryEmotions()
    {
        WordNetDatabase database = WordNetDatabase.getFileInstance();
        System.setProperty("wordnet.database.dir", "./WordNet-3.0/dict");
        String[] basicEmotions = {"anger","joy","surprise","sadness","disgust","fear"};
        HashMap<String,String[]> secondaryEmotions = WordLoader.loadWordsAndGroup("secondary_emotions.txt");
        String[] group;
        HashMap<Emotion,HashSet<String>> expandedEmotions = new HashMap<Emotion,HashSet<String>>();
        HashMap<String,Emotion> stringToEnum = new HashMap<String,Emotion>();
        
        stringToEnum.put("anger", Emotion.ANGER);
        stringToEnum.put("joy",Emotion.JOY);
        stringToEnum.put("surprise",Emotion.SURPRISE);
        stringToEnum.put("sadness",Emotion.SADNESS);
        stringToEnum.put("disgust", Emotion.DISGUST);
        stringToEnum.put("fear", Emotion.FEAR);
        
        for(String emotion : basicEmotions)
        {
            group = secondaryEmotions.get(emotion);
            //System.out.println(emotion.toUpperCase());
            HashSet<String> expansion = new HashSet<String>();
            expansion.add(emotion);
            for(String secondaryEmotion : group)
            {
                Synset[] synsets = database.getSynsets(secondaryEmotion, SynsetType.NOUN); 
                NounSynset nounSynset=null; 
                //System.out.println("\t"+secondaryEmotion.toUpperCase());
                expansion.add(removeNonPrintable(secondaryEmotion));
                for(int i=0;i<synsets.length;i++)
                {
                    nounSynset = (NounSynset)(synsets[i]);
                    String[] synonyms = nounSynset.getWordForms();
                    for(String word : synonyms)
                    {
                        String filtered = word.replaceAll(secondaryEmotion,"");
                        if(!filtered.equals(""))
                        {
                            //System.out.println("\t\t"+filtered);
                            expansion.add(removeNonPrintable(filtered));
                        }

                    }
                    NounSynset[] hyponyms = nounSynset.getHyponyms();
                    for(NounSynset hyponym : hyponyms)
                    {
                         String[] words =  hyponym.getWordForms();
                         for(String word : words)
                         {
                             //System.out.println("\t\t\t"+word);
                             expansion.add(removeNonPrintable(word));
                         }
                    }

                }
               
                
            }
            
            
            expandedEmotions.put(stringToEnum.get(emotion), expansion); 
        }
        return expandedEmotions;
    }
    
   
    public static void main(String[] args)
    {
        System.out.println("TEST EMOTION EXPANDER");
        
        HashMap<Emotion,HashSet<String>> secondaryEmotions = getSecondaryEmotions();
        /*
        for(Map.Entry<Emotion, HashSet<String>> entry : secondaryEmotions.entrySet()) {
            Emotion emotion = entry.getKey();
            HashSet<String> emotionList = entry.getValue();
            System.out.println(emotion);
            for(String secondary:emotionList)
            {
                System.out.println("\t"+secondary);
            }
            
        }
        */
        EmotionDictionary dict = new EmotionDictionary();
        
        System.out.println(dict.representativeEmotion("happy"));
        
  
    }
}
