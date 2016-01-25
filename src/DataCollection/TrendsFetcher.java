package DataCollection;

import java.util.ArrayList;
import twitter4j.Trend;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.api.TrendsResources;

/**
 * 
 * This class provides an wrapper of twitter4j 
 * to fetch the current twitter worldwide trends.
 * 
 */
public class TrendsFetcher {
    
    private TrendsResources trends;
    
    public TrendsFetcher(){

        trends = TwitterFactory.getSingleton().trends();
        
    }
    
    /**
     * @return The current world wide top trend in twitter.
     * @throws TwitterException 
     */
    public Common.Trend[] getTrends() throws TwitterException{
        Trend[] t = trends.getPlaceTrends(1).getTrends();
        ArrayList<Common.Trend> l = new ArrayList<>();
        
        for(int i = 0; i < t.length; i++){
            String trend = t[i].getName();

            if(!Common.Utils.isEnglish(trend))
                continue;
            
            Common.Trend tr= new Common.Trend(trend);
            l.add(tr);
            
        }
        
        Common.Trend[] ta = new Common.Trend[l.size()];
        ta = l.toArray(ta);    
                
        return ta;
    }
    
}
