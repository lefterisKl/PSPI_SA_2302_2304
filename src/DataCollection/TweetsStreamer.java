package DataCollection;

import Common.Trend;
import Common.Tweet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/**
 * This is a basic class for streaming tweets related to the
 * active trends.
 * 
 * It wraps the twitter4j API and offers functions that are needed by our 
 * program.
 * 
 * So at every point of the program we have a set of active trends
 * and the tweets that we fetch must be related to those trends.
 * 
 * This class does the bookeeping on to which trends are still active,
 * offers functions to update, or add new trends.
 * 
 * So the tweets that are collected are cached and at any moment
 * we can request them.
 * 
 *  
 */
public class TweetsStreamer {
    
    private TwitterStream ts;
    private FilterQuery fq;
    private ActiveTrendsStatusListener statusListener;
    
    //This is going to be sorted by start time.
    private ArrayList<Trend> trackingTrends;
    private HashSet<String>  trackingTrendsSet;
    
    public TweetsStreamer(){
        
        ts = TwitterStreamFactory.getSingleton();
        fq = new FilterQuery();
        fq.language("en");
        
        trackingTrends = new ArrayList<>();
        statusListener = new ActiveTrendsStatusListener(this);
        trackingTrendsSet = new HashSet<>();
        
        ts.addListener(statusListener);
        
    }
    
    public void addWord(Trend trend){
        
        if(trackingTrendsSet.contains(trend.toString())){
            
            for(int i = 0; i < trackingTrends.size(); i++)
                if(trend.toString().equals(trackingTrends.get(i).toString())){
                    trackingTrends.remove(i);
                    trackingTrends.add(trend);
                }
            
            return ;
        }
        
        trackingTrends.add(trend);
        trackingTrendsSet.add(trend.toString());
        updateFilterQuery();
        
    }
    
    public void addTrends(Trend[] trends){
        
        for(int i = 0; i < trends.length; i++){
            
            if(trackingTrendsSet.contains(trends[i].toString())){
                String trendString = trends[i].toString();
                for(int j = 0; j < trackingTrends.size(); j++)
                    
                    if(trendString.equals(trackingTrends.get(j).toString()))
                        trends[i].setEndTime(new Date(
                                             new Date().getTime()+1000*60*120));


                return ;
            }
            else{
                trends[i].setEndTime(new Date(new Date().getTime()+1000*60*120));
                trackingTrends.add(trends[i]);
                trackingTrendsSet.add(trends[i].toString());
            }
        }
        
        updateFilterQuery();
        
    }
    
    public void removeWord(String word){
        if(!trackingTrendsSet.remove(word))
            return;
        
        for(int i = 0; i < trackingTrends.size();i++)
            if(word.equals(trackingTrends.get(i).toString()))
                trackingTrends.remove(i);

        updateFilterQuery();
    }
    
    public ArrayList<Trend> removeInactiveTrends(){
        ArrayList<Trend> deadTrends = new ArrayList<>();
        Date now = new Date();
        for(int i = 0; i < trackingTrends.size(); i++){
            Date trendEnd = trackingTrends.get(i).getEndTime();
            if(now.getTime() - trendEnd.getTime() >= 0 ){
                deadTrends.add(trackingTrends.get(i));
                trackingTrendsSet.remove(trackingTrends.get(i).toString());
                trackingTrends.remove(i);
            }
            
        }
        return deadTrends;
    }
    
    public void startCollecting(){
        ts.filter(fq);
    }
    
    public void stopCollecting(){
        ts.cleanUp();
    }
    
    public ArrayList<Tweet> getCollectedData(){
        return statusListener.getStreamCaching().getArrayList();
    }
    
    //If we make somehow allow this to be called only after 
    //stopCollecting is up the many concurency problems 
    //are solved easily.
    public void clearCache(){
        statusListener.getStreamCaching().clearCache();
    }
    
    
    private void updateFilterQuery(){
        
        String ar[] = new String[trackingTrends.size()];
        ar = trackingTrendsSet.toArray(ar);
        
        fq = fq.track(ar);
    }
    
    
    public ArrayList<Trend> getActiveTrends(){
        return trackingTrends;
    }
    
}
