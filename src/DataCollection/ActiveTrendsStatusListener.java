/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataCollection;

import Common.Trend;
import Common.Tweet;
import java.util.ArrayList;
import java.util.Date;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * A TwitterStream class needs a status listener and this is the 
 * implementation of it.
 * 
 * This listener is "activated" on events such as a new tweet has 
 * been fetched.
 * 
 * So this class's job is : for each tweet that arrives associate it 
 * with a current trend, create a twitter object out of it and 
 * add it to the cache-buffer where later it will all be written into
 * MongoDB.
 * 
 */
public class ActiveTrendsStatusListener implements StatusListener{

    StreamCaching cache;
    TweetsStreamer associatedStreamer;

    public ActiveTrendsStatusListener(TweetsStreamer s) {
        cache = new StreamCaching();
        associatedStreamer = s;
    }
    
    public StreamCaching getStreamCaching(){
        return cache;
    }
    
    
    @Override
    public void onStatus(Status arg0) {
        if(arg0.isRetweet())
            return;
        
        ArrayList<Trend> trends = associatedStreamer.getActiveTrends();
        Trend associatedTrend = null;
        
        for(int i = 0; i < trends.size(); i++){
            if(arg0.toString().contains(trends.get(i).toString())){
                associatedTrend = trends.get(i);
                break;
            }
        }
        
        if(associatedTrend == null)
            return;

        Tweet t = new Tweet(arg0.getText(), null,associatedTrend.toString(), 
                            arg0.getUser().getName(), new Date());
        cache.addDatum(t);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Track limitation notice");
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onStallWarning(StallWarning warning) {
        System.out.println("Just got a stall warning" + warning.getMessage());
    }

    @Override
    public void onException(Exception excptn) {
        System.out.println(excptn.getMessage());
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
