package MongoDB;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * An instance of this class initializes the databases needed for storing trends and tweets.
 * Also gives access to the MongoCollection wrappers TrendCollection and TweetCollection
 * that provide an interface for i/o  to the classes of the other packages.
 */
public class DataBase {
    
    private static DataBase database;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    
    
    
    public static DataBase getInstance()
    {
        if(database==null)
        {
            database= new DataBase("TwitterDB");
        }
        return database;
    }
    
    private DataBase(String name)
    {
        mongoClient = new MongoClient( "localhost" , 27017 );
        mongoDatabase = mongoClient.getDatabase(name);
    }
    
    public TrendCollection getTrendCollection()
    {
        MongoCursor mc = mongoDatabase.listCollectionNames().iterator();
        while(mc.hasNext())
        {
            String name = (String) mc.next();
            if(name.equals("Trends"))
            {
                MongoCollection<org.bson.Document> collection = mongoDatabase.getCollection("Trends");
                return new TrendCollection(collection,collection.count()); 
            }
        }
        
        mongoDatabase.createCollection("Trends");
        return new TrendCollection(mongoDatabase.getCollection("Trends"),0);           
    }
    
   
    
    public TweetCollection getTweetCollection()
    {
        MongoCursor mc = mongoDatabase.listCollectionNames().iterator();
        while(mc.hasNext())
        {
            String name = (String) mc.next();
            if(name.equals("Tweets"))
            {
                MongoCollection<org.bson.Document> collection = mongoDatabase.getCollection("Tweets");
                return new TweetCollection(collection,collection.count()); 
            }
        }
        mongoDatabase.createCollection("Tweets");
        return new TweetCollection(mongoDatabase.getCollection("Tweets"),0);        
    }
       
}
