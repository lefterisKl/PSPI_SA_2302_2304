package MongoDB;

import Common.Storeable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 * A wrapper for a MongoCollection object.
 * This is also the base class for TrendCollection and TweetCollection.
 * The common functionality needed for the two inheriting classes is implemented here:
 * 1. Getting an iterator on the collection (MongoCursor)
 * 2. Adding objects that implement the interface Storable in the database
 */
public class Collection {
    
    protected MongoCollection collection;
    protected long numberOfDocuments;
    
    public Collection(MongoCollection<Document> collection,long  numberOfDocuments)
    {
        this.collection = collection;
        this.numberOfDocuments=numberOfDocuments;
    }
    
    public MongoCollection getMongoCollection()
    {
        return collection;
    }
    
    public MongoCursor getIterator()
    {
        return collection.find().iterator();
    }

    public void addOne(Storeable object) //or Trend class
    {  
      collection.insertOne(object.toDocument()); 
    }
    
    public <T> void addMany(List<T> objects)
    {
        if(objects.size()==0)
            return ;
        ArrayList<Document> documents = new ArrayList<>();
        
        for(int i = 0; i < objects.size(); i++){
            documents.add(((Storeable)objects.get(i)).toDocument());
        }
        
        collection.insertMany(documents);
    }
    
}
