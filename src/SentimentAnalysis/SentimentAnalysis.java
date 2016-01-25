package SentimentAnalysis;

import Common.Emotion;
import Common.Tweet;
import InputOutput.SenticNetReader;
import MongoDB.DataBase;
import MongoDB.TweetCollection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.eq;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/** This class analyzes every Tweet stored in the Mongo database
 * and gives it 6 scores, one for each emotion.
 * 
 */
public class SentimentAnalysis {
    
    EmotionDictionary       dict; 
    HashMap<String,Double>  senticWords;
    static ArrayList<Double> test;

    public SentimentAnalysis() {
        test = new ArrayList<>();
        dict = new EmotionDictionary();
        try {
            senticWords = new SenticNetReader("senticnet3.rdf.xml").getdata();
        } catch (IOException ex) {
            Logger.getLogger(SentimentAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /*
        replace every tweet document in the mongo db with a new one that 
        also has the sentiment scores
    */
    
    public void updateDocs(MongoCursor mc,MongoCollection collection)
    {
        Double[] scores;
        
        while(mc.hasNext())
        {
            //the document corresponds to a tweet
            org.bson.Document document = (org.bson.Document)mc.next();
            //find the sentiment scores for this tweet
            scores = analyze(new Tweet(document)); 
            org.bson.Document updatedDocument = new org.bson.Document(document);
            //add scores to the new document
            setScoresToDocument(updatedDocument,scores);  
            test.add(scores[0]);
            //replace the old document with the new one
            collection.replaceOne(document,updatedDocument);  
        }
    }
    
    /*
        Add this fields to the new document that will
        replace the previous one
    */
    
    public void setScoresToDocument(org.bson.Document doc,Double[] scores)
    {
        doc.put("anger", scores[0]);
        doc.put("disgust",scores[1]);
        doc.put("sadness", scores[2]);
        doc.put("surprise", scores[3]);
        doc.put("joy", scores[4]);
        doc.put("fear",scores[5]);
    }
    
    public Double[] analyze(Tweet tweet){
                
        Double[] emWordsCnt = new Double[]{0.0,0.0,0.0,0.0,0.0,0.0}; //emotional words Count
        Double[] emWordsPolarity = new Double[]{0.0,0.0,0.0,0.0,0.0,0.0};
        int totalEmotionalWordsCount = 0;

        //The preprocessed tweets words are already stemmed.
        String text = tweet.getProcessedText();
        String[] words = text.trim().split("\\s+");
        
        for(String word : words){
            if(senticWords.containsKey(word)){
                Emotion em = dict.representativeEmotion(word);
                if(em != Emotion.NO_EMOTION){
                    double polarity = senticWords.get(word);
                    emWordsCnt[em.getId()] += 1;
                    emWordsPolarity[em.getId()] += Math.abs(polarity);
                    totalEmotionalWordsCount += 1;
                }
            }
            
        }
        
        for(int i = 0; i < 6; i++)
            if(emWordsCnt[i] != 0)
                emWordsPolarity[i] /= emWordsCnt[i]*totalEmotionalWordsCount;
                
        return emWordsPolarity;
        
    }

    
    public static void main(String[] args){
        
        DataBase db = DataBase.getInstance();
        TweetCollection tweetDB;
        tweetDB = db.getTweetCollection();
        SentimentAnalysis sa = new SentimentAnalysis();
        //String x= "srkappreciationday indian biggest superstar                         ";
        //System.out.print("mongo "+x.length());
        /*
       
        Tweet tweet = new Tweet(null,
                                 "I hate all of you idiots bad war pain happy joy"
                                         + " ire health peace love life",
                                null,
                                null,
                                null);
        
        Double[] scores = sa.analyze(tweet);
        
        for(int i = 0 ; i < 6; i++)
            System.out.println(scores[i]);
        */
        

        sa.updateDocs(tweetDB.getIterator(),tweetDB.getMongoCollection());
        int i=0;
       MongoCursor mc = tweetDB.getIterator();
       boolean correct=true;
       while(mc.hasNext())
       {
           org.bson.Document d = (org.bson.Document) mc.next();
           System.out.println(d.get("anger")+" "+test.get(i));
           if(!d.getDouble("anger").equals(test.get(i)))
           {
               System.out.println("wrong");
               correct = false;
               break;
           }
           i++;
       }
       if(correct)
            System.out.println("No error found");
        
    
    }
    
}
