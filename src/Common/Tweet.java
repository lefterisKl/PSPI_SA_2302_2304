package Common;

import java.util.Date;
import org.bson.Document;


/**
 * Basic class that represents the "tweets".
 * Contains all information of a tweet that we need.
 * 
 * Also it implements the interface storable since it
 * is being read and written from the database.
 */
public class Tweet implements Storeable {
    
    
    private String rawText;
    private String preprocessedText;
    private String trend;
    private String user;
    private Date date;
    //private Double[] scores;
    
    
    public Tweet(String rawText,String preprocessedText,String trend,String user,Date date)
    {
        this.rawText = rawText;
        this.preprocessedText = preprocessedText;
        this.trend = trend;
        this.user = user;
        this.date = date;
        //scores = new Double[6];
    }
    
    
    public void setRaw(String rawText)
    {
        this.rawText = rawText;
    }
    
     public void setTrend(String trend)
    {
        this.trend = trend;
    }
     
     
    public void setPreprocessed(String preprocessedText)
    {
        this.preprocessedText = preprocessedText;
    }
    
    public void setUser(String user)
    {
        this.user = user;
    }
    
    public void setDate(Date date)
    {
        this.date = date;
    }
    
    public String getRaw()
    {
        return rawText;
    }
    
    public String getTrend()
    {
        return trend;
    }
    
    public String getProcessedText()
    {
        return preprocessedText;
    }
    
    public String getUser()
    {
        return user;
    }
    
    public Date getDate()
    {
        return date;
    }
    
    
    @Override
    public Document toDocument() {
       return new org.bson.Document()
               .append("rawText", rawText)
               .append("preprocessedText",preprocessedText)
               .append("trend",trend)
               .append("user", user)
               .append("date", date)
               .append("anger", null)
               .append("disgust",null)
               .append("sadness", null)
               .append("surprise", null)
               .append("joy", null)
               .append("fear", null);
    }

    @Override
    public void fromDocument(Document doc) {
        rawText = doc.getString("rawText");
        preprocessedText = doc.getString("preprocessedText");
        trend = doc.getString("trend");
        user = doc.getString("user");
        date = doc.getDate("date");
       //add emotion scores?
        
    }
    
    
     public Tweet(Document document)
    {
        fromDocument(document);
    }
    
}
