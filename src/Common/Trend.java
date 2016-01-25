package Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.bson.Document;

/**
 * This class represent a trend fetched from Twitter.
 * 
 * Contains information as startTime and endTime
 * The time interval between those two represent the 
 * interval that this trend was active where active is defined
 * as in the project description.
 * 
 * Also it implements the interface Storable
 * as this class objects are written and read from the database. 
 *
 */
public class Trend implements Storeable  {
    
    private List<String> terms;
    private Date         startTime;
    private Date         endTime;
    
    public Trend(String trend){
        
        startTime = new Date();     //That initializes to now
        endTime   = null;
        
        terms = new ArrayList<>();
        
        terms.addAll(Arrays.asList(trend.trim().split("\\s+")));
        
    }
    
    public Trend(String term,Date st)
    {
        terms = new ArrayList<>();
        
        terms.addAll(Arrays.asList(term.trim().split("\\s+")));
        
        startTime = st;
        endTime   = null;
        
    }
    
    public Trend(org.bson.Document document)
    {
        fromDocument(document);
    }
    
    public List<String> getTermList()
    {
        return terms;
    }
    
    public Date getStartTime()
    {
        return startTime;
    }
    
    public Date getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(Date d){
        endTime = d;
    }

  
    @Override
    public Document toDocument() {
      return new Document()
              //TODO check if endTIme is null
              .append("terms",terms)
              .append("startTime", startTime)
              .append("endTime", endTime);
    }


    @Override
    public void fromDocument(Document doc) {
        
       terms=(ArrayList<String>)doc.get("terms");

       startTime = doc.getDate("startTime");
       endTime   = doc.getDate("endTime");
       
    }
    
    @Override
    public String toString(){
        
        String s = terms.get(0);
        
        for(int i = 1; i < terms.size(); i++)
            s += " " + terms.get(i);
        
        return s;
    }
    
    
}

