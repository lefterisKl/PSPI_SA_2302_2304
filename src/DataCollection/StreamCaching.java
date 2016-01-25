package DataCollection;

import Common.Tweet;
import java.util.ArrayList;

/**
 * This class's job is to act as an intermediate cache for the tweets
 * that we fetch before we pass them into MongoDB. 
 *
 */
public class StreamCaching {
    
    
    private ArrayList<Tweet> data;
    
    public StreamCaching(){
        data = new ArrayList<>();
    }
    
    public void addDatum(Tweet obj){
        data.add(obj);
    }
    
    public ArrayList<Tweet> getArrayList(){
        return data;
    }
    
    //TODO Thread issues.
    public void clearCache(){
        data = new ArrayList<>();
    }
    
}
