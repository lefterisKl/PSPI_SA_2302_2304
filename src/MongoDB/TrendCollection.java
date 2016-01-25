package MongoDB;

import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.List;
import Common.Trend;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;

/**
 * A wrapper for the MongoCollection object that manages the collection where the trends are stored.
 * Except for the functionality provided from the base class Collection, it also provides a method for
 * querying trends by their terms. 
 */
public class TrendCollection extends Collection {

    public TrendCollection(MongoCollection<Document> collection, long numberOfDocuments) {
        super(collection, numberOfDocuments);
    }
    
    public List<Trend> getTrendsWithTerms(List<String> terms)
    {
        MongoCursor it = collection.find(eq("terms",terms)).iterator();
        List<Trend> trends = new ArrayList<Trend>();
        while(it.hasNext())
        {
            org.bson.Document doc = (org.bson.Document) it.next();
            trends.add(new Trend(doc)); 
        }
        return trends;
    }
    
    
 
}
