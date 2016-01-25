package MongoDB;

import Common.Tweet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 * A wrapper for the MongoCollection object that manages the collection where the tweets are stored.
 * Except for the functionality provided from the base class Collection, it also provides  methods for
 * 1.get all the tweets for a specific user
 * 2.return an iterator on all the tweets associated with one trend
 */
public class TweetCollection extends Collection{
 
   
    
    public TweetCollection(MongoCollection<Document> collection, long numberOfDocuments) {
        super(collection, numberOfDocuments);
    }
    
    
     public List<Tweet> getTweetsByUser(String user)
    {
        MongoCursor it = collection.find(eq("user",user)).iterator();
        List<Tweet> tweets = new ArrayList<Tweet>();
        while(it.hasNext())
        {
            org.bson.Document doc = (org.bson.Document) it.next();
            tweets.add(new Tweet(doc)); 
        }
        return tweets;
    }
    
    
    public MongoCursor getCursorByTrend(List<String> terms)
    {
        
        String str = terms.get(0);
        for(int i = 1; i < terms.size(); i++){
            str = str + " " + terms.get(i);
            System.out.println(str);
        }
        //the iterator's next() will now returns tweets sorted by date
        return  collection.find(eq("trend",str))
                .sort(new org.bson.Document("date",1)) 
                .iterator();
    }
    
    public MongoCollection getCollection()
    {
        return collection;
    }
    
   
    //or this if we decide to have trend as a raw string ATM
     public List<Tweet> getTweetsWithTrend(String trend)
    {
        MongoCursor it = collection.find(eq("trend",trend)).iterator();
        List<Tweet> tweets = new ArrayList<Tweet>();
        while(it.hasNext())
        {
            org.bson.Document doc = (org.bson.Document) it.next();
            tweets.add(new Tweet(doc)); 
        }
        return tweets;
    }
     
     
    
 
}
