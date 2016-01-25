package Common;

import org.bson.Document;

/**
 * This is an interface that every class that is being 
 * written and read from the Mongo db must implement.
 */
public interface Storeable {

    
    /**
     * Transforms and object to Document type which in turn can be written
     * to the Mongo db.
     * @return 
     */
    Document toDocument();
    

    /**
     * After reading a Document object from the database
     * this function is called to transform the object to the3 
     * class that implements this interface.
     * @param doc 
     */
    void fromDocument(Document doc);
}
