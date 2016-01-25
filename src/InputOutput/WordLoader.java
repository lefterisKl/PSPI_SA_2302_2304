package InputOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This is the parser used to read the file with the secondary emotions.
 * There is also a function for grouping the words by primary emotion
 */
public class WordLoader {
    
    /*
       Return all the words of the file in one list
    */
    public static ArrayList<String> loadWords(String filename,String seperator)
    {
        ArrayList<String> wordList = new ArrayList<String>();
        BufferedReader reader;
        String line;
        String[] words;
        
        try {
            reader = new BufferedReader(new FileReader(filename));
            line = reader.readLine();
              if(line.charAt(0)=='\uFEFF') 
                line = line.substring(1);
              while(line!=null)
              {
                  words = line.split(seperator);
                  for(String w:words)
                  {
                      wordList.add(w);
                  }
                   line = reader.readLine();
                  
              }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WordLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException ex){
            
        }
        return wordList;
    }
    
    /*
        Will return a hashmap where all the secondary emotions of one primary emotion
        can be found.
    */
     public static HashMap<String,String[]> loadWordsAndGroup(String filename)
    {
        HashMap<String,String[]> wordList = new HashMap<String,String[]>();
        String[] grouped=null;
        String text=null;
        try {
             text = new Scanner( new File(filename) ).useDelimiter("\\A").next();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WordLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        grouped = text.split("\\n");
        wordList.put("anger", grouped[0].split("\\t"));
        wordList.put("disgust",grouped[1].split("\\t"));
        wordList.put("fear",grouped[2].split("\\t"));
        wordList.put("joy", grouped[3].split("\\t"));
        wordList.put("sadness",grouped[4].split("\\t"));
        wordList.put("surprise",grouped[5].split("\\t"));
        return wordList;
        
    }
    
   
     
}
