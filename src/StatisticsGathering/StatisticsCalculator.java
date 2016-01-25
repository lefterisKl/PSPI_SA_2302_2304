/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StatisticsGathering;

import Common.Trend;
import Common.Tuple;
import Common.Tweet;
import InputOutput.WordLoader;
import MongoDB.DataBase;
import MongoDB.TrendCollection;
import MongoDB.TweetCollection;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author lefteris
 */
public class StatisticsCalculator {
    
    private TweetCollection tweetCollection;
    
    public StatisticsCalculator(TweetCollection tweetCollection)
    {
        this.tweetCollection = tweetCollection;
    }
    
    public Double[] getScores(org.bson.Document tweetDocument)
    {
       
        Double[] scores = new Double[6];
        scores[0] = (Double) tweetDocument.get("anger");
        scores[1] = (Double) tweetDocument.get("disgust");
        scores[2] = (Double) tweetDocument.get("sadness");
        scores[3] = (Double) tweetDocument.get("surprise");
        scores[4] = (Double) tweetDocument.get("joy");
        scores[5] = (Double) tweetDocument.get("fear");
        
        return scores;
        
    }
    
        
    public ArrayList<HashMap<String,Integer>>
    representativesByTrendForEachEmotion(Trend trend){

        ArrayList<Tweet>[] tweetLists = new ArrayList[6];
        ArrayList<HashMap<String,Integer>> tfPerEmotion = new ArrayList<>();

        for(int i = 0; i < 6; i++)
            tweetLists[i] = new ArrayList<Tweet>();

        MongoCursor tweetCursor =    tweetCollection.getCursorByTrend(trend.getTermList());

        while(tweetCursor.hasNext()){

            org.bson.Document doc  = (org.bson.Document) tweetCursor.next();
            Double scores[] = getScores(doc);
        
            tweetLists[getMaxScoreIndex(scores)].add(new Tweet(doc));
        }
    
        for(int i = 0; i < 6; i++)    
            tfPerEmotion.add(getTF(tweetLists[i]));
        
        return tfPerEmotion;
    }
    
    
    public void calculateTrendEmotions(MongoCursor trendCursor){
        while(trendCursor.hasNext()){
           
            Trend trend = new Trend( (org.bson.Document) trendCursor.next());
            long intervalWidth = findIntervalWidth(trend);
            ArrayList<Tuple<Date,Integer[]>> stats = calculateStatsFor(trend,intervalWidth,trend.getStartTime());
            ArrayList<HashMap<String,Integer>> representativeWords = 
                    wordRepresentativesForTrend(trend,intervalWidth,stats);
            ArrayList<HashMap<String,Integer>> data = representativesByTrendForEachEmotion(trend);
        
                
        }
        //We will return data
    }
    
    
    public ArrayList<HashMap<String,Integer>> wordRepresentativesForTrend
    (Trend trend,double intervalWidth,ArrayList<Tuple<Date,Integer[]>> stats)
    {
        ArrayList<HashMap<String,Integer>> representativeWords = new ArrayList<HashMap<String,Integer>>();
        long trendStartTime = trend.getStartTime().getTime();
        int interval = 1;
        Double[] scores;
        MongoCursor tweetCursor = tweetCollection.getCursorByTrend(trend.getTermList());
        ArrayList<Tweet> tweetList = new ArrayList<>();
        while(tweetCursor.hasNext())
        {
            org.bson.Document docTweet = (org.bson.Document) tweetCursor.next();
            if(trendStartTime+interval*intervalWidth > ((Date) docTweet.get("date")).getTime())
            {
                scores = getScores(docTweet);
                if(getMaxScoreIndex(scores)== getMaxIndex(stats.get(interval-1).second()))
                   tweetList.add(new Tweet(docTweet));  
            }
            else //if else runs, we have reached the end of the interval
            {
                //here call slave
                representativeWords.add(getTF(tweetList));
                tweetList = new ArrayList<>(); //make new tweetList for the next interval             
            }
        } 
        return representativeWords;
    }
    
    
    
    
    
    private HashMap<String,Integer> getTF(ArrayList<Tweet> tweets){
        HashMap<String,Integer> occurences = new HashMap<>();
        for(int i = 0; i < tweets.size(); i++){
            String[] words  = tweets.get(0).getProcessedText().trim().split("\\s+");
            for(int j = 0; j < words.length; j++){
                if(occurences.containsKey(words[j]))
                    occurences.put(words[j],occurences.get(words[j]) + 1);
            }
        }
        return removeEmotionalWords(occurences);
    }
    
    
    
    private HashMap<String,Integer> removeEmotionalWords(HashMap<String,Integer> wf){
        ArrayList<String> words = WordLoader.loadWords("emoticons.txt", " ");
        for(int i = 0; i < words.size(); i++){
            if(wf.containsKey(words.get(i))){
                wf.remove(words.get(i));
            }
        }
        return wf;
    }
    
    
    
    
    ArrayList<Tuple<Date,Integer[]>> calculateStatsFor(Trend trend, long width,Date startTime)
    {
            ArrayList<Tuple<Date,Integer[]>> list = new ArrayList<>();
            MongoCursor tweetCursor;
            Integer[] sentimentsCount = new Integer[]{0,0,0,0,0,0};
            tweetCursor = tweetCollection.getCursorByTrend(trend.getTermList());
            int interval = 1;
            while(tweetCursor.hasNext())
            {
               
                Double scores[];
                org.bson.Document currentTweet = (org.bson.Document) tweetCursor.next();
                scores = getScores(currentTweet);
                Date currentDate = (Date) currentTweet.get("date");
                
                
                
             
                while(startTime.getTime() + interval*width < currentDate.getTime())
                {
                    list.add(new Tuple<Date,Integer[]>(new Date(startTime.getTime()
                    +(interval-1)*width),sentimentsCount));
                    for(int i=0;i<6;i++)
                        sentimentsCount[i]=0;
                    
                    interval ++;
                }
                    ++sentimentsCount[getMaxScoreIndex(scores)]; 
                
                
            }
            return  list;
            
    }
    

    public int getMaxScoreIndex(Double[] scores){

        int maxIndex = 0;
        for(int i = 1 ; i < scores.length; i++)
            if(scores[i] > scores[maxIndex])
                maxIndex = i;
        
        return maxIndex;
            
    }
    
    public int getMaxIndex(Integer[] array){
       
        int maxIndex = 0;
        for(int i = 1 ; i < array.length; i++)
            if(array[i] > array[maxIndex])
                maxIndex = i;
        
        return maxIndex;
    }
    
    

    //TODO Implement
    private int findIntervalWidth(Trend trend){
        return 10;
    }
    
    public static void main(String[] args)
    {
        TrendCollection trendDB;
        TweetCollection tweetDB;
        DataBase db = DataBase.getInstance();
        trendDB = db.getTrendCollection();
        tweetDB = db.getTweetCollection();
        
        StatisticsCalculator sc = new StatisticsCalculator(tweetDB);
        sc.calculateTrendEmotions(trendDB.getIterator());
        
      
    }

}

