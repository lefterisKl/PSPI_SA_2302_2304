package InputOutput;
import Common.Emoticon;
import Common.Emotion;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * An object of this class can parse the emoticons from a file
 * with the format given in the file "emoticons.txt". The parser 
 * creates an ArrayList with all objects of the class Emoticon (from package Common).
 */
public class EmoticonLoader {
    
    static private HashMap<String,Emotion> emotionMap;
    /*
       initialized a map to quickly find the corresponding Emotion enum value
    */
    static public void initializeMap()
    {
        emotionMap = new HashMap<>();
        emotionMap.put("anger", Emotion.ANGER);
        emotionMap.put("disgust", Emotion.DISGUST);
        emotionMap.put("sadness",Emotion.SADNESS);
        emotionMap.put("surprise",Emotion.SURPRISE);
        emotionMap.put("joy", Emotion.JOY);
        emotionMap.put("shame",Emotion.FEAR);
    }
    
    static public ArrayList<Emoticon> loadEmoticons(String filename)
    {
         
        ArrayList<Emoticon> emoticons=new ArrayList<Emoticon>();
        BufferedReader reader;
        String line;
        String[] parts;
        Emotion emotion;
        Number score1,score2;
       
        initializeMap();
        
        try {
        
            reader = new BufferedReader(new FileReader(filename));
            line = reader.readLine();
            if(line.charAt(0)=='\uFEFF') //an unprinted character that had to be removed
                line = line.substring(1);
            while(line!=null)
            {
                //parse data of one emoticon
                parts = line.split("\\s");
                parts[0] = parts[0].trim();
                emotion = emotionMap.get(parts[0]);
                score1 = Double.parseDouble(parts[1]);
                score2 = Double.parseDouble(parts[2]);
                //create emoticon and add it to the arraylist
                emoticons.add(new Emoticon(emotion,score1,score2,parts[3]));
                line = reader.readLine();
            }
            
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EmoticonLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException ex)
        {
            
        }
        return emoticons;
    }
    
    
    
}
