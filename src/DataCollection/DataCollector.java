package DataCollection;

import java.util.ArrayList;
import twitter4j.TwitterException;


import Common.Trend;
import Common.Tweet;
import MongoDB.DataBase;

import MongoDB.TrendCollection;
import MongoDB.TweetCollection;
import Preprocessing.Preprocessor;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * The is the class where all the data collection happens.
 * 
 * Also here we assume the following:
 * 
 * The number of tweets that we download in 5 minutes are not enough
 * so that the time for the program to preprocess them and write them to 
 * the database does not exceed 5 minutes. Ever.
 * 
 * What this class does is:
 * 
 * The actions are repeating at a five minute time step.
 * 
 * At the end of a five minute interval it fetches the new top 
 * trends using the TrendsFetcher class.
 * Stops fetching tweets temporarily, to update the active trend information 
 * in the TweetStreamer class which and continues the fetching.
 * Concurently it starts to preprocesses the tweets that were collected in 
 * the five minute interval and writes them into the Mongo db.
 * 
 */
public class DataCollector {
    
    private TrendsFetcher  tf;
    private TweetsStreamer ts;
    
    private TrendCollection trendDB;
    private TweetCollection tweetDB;
    
    ArrayList<Tweet> batch;
    final Object batchMon;  //Used for Threads communication to avoid races.

    private int downloadedTweets;
    
    public DataCollector(){
        
        tf = new TrendsFetcher();
        ts = new TweetsStreamer();
        
        DataBase db = DataBase.getInstance();
        
        trendDB = db.getTrendCollection();
        tweetDB = db.getTweetCollection();
        
        batch    = null;
        batchMon = new Object();
        
        downloadedTweets = 0;
                
    }
    
    //One thread will run this class's run function
    //This is responsible for updating the active trends
    //every five minutes, passing the data collected 
    //In that five minute interval to the other thread
    private class TrendUpdater implements Runnable{
        
        @Override
        public void run(){
      
            do{

                try {
                    Trend[] topTrends;
                    topTrends = tf.getTrends();
                    
                    trendDB.addMany(ts.removeInactiveTrends());
                    ts.addTrends(topTrends);
                    
                    ts.startCollecting();
                    
                    Thread.sleep(1000*60*5);
                    
                    ts.stopCollecting();
                    
                    synchronized (batchMon){
                        batch = ts.getCollectedData();
                        System.out.println("About to notify");
                        batchMon.notify();
                    }
                    
                    ts.clearCache();
                    
                }
                catch (InterruptedException ex) {
                    ts.stopCollecting();
                    return;
                } catch (TwitterException ex) {
                    Logger.getLogger(DataCollector.class.getName()).log(Level.SEVERE, null, ex);
                }

            }while(!Thread.interrupted());

            System.out.println("Ending collection");
        }
    }
    
    //And the second thread will run this class's run function
    //which is responsible for preprocessing the tweets fetched
    //in the previous five minute interval and then write them in the Mongo db.
    private class ResultsHandler implements Runnable{
        
        @Override
        public void run(){

            Preprocessor p = new Preprocessor();

            do{

                synchronized(batchMon){
                    System.out.println("Started waiting");
                    try {
                        batchMon.wait();
                    } catch (InterruptedException ex) {
                        return;
                    }
                    System.out.println("Ended waiting");
                }

                Date old = new Date();

                for(int i = 0; i < batch.size(); i++){
                    Tweet t = batch.get(i);
                    t.setPreprocessed(p.preprocessTweet(t.getRaw()));
                }

                tweetDB.addMany(batch);
                
                downloadedTweets += batch.size();
                
                System.out.println("Total downloaded tweets " + downloadedTweets);
                
                Date n = new Date();
                
                System.out.println("Time to process last batch");
                System.out.println((n.getTime() - old.getTime())/1000.0);

            }while(!Thread.interrupted());
            System.out.println("Ended Results handling");

        }
    }
    
    
    public void init() throws IOException, InterruptedException{
        
        Thread trendUpdater = new Thread(new TrendUpdater());
        Thread dataHandler  = new Thread(new ResultsHandler());
        
        trendUpdater.start();
        dataHandler.start();
        
        System.out.println("Press anytime enter to terminate");
        System.in.read();
        
        trendUpdater.interrupt();
        dataHandler.interrupt();
        
        dataHandler.join();
        
        System.out.println(downloadedTweets);
                        
    }
    
    
    
    
    
    public static void main(String[] args) throws TwitterException, InterruptedException, IOException {
        
        DataCollector dc = new DataCollector();
        
        dc.init();
        
    }
    
}
