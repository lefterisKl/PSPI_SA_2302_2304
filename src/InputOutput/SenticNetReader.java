package InputOutput;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class implements a parser for reading the xml file with the sentiment data.
 * 
 * 
 */
public class SenticNetReader {
    
    String inputFile;

    public SenticNetReader(String inputFile) {
        this.inputFile = inputFile;
    }
    
    /*
        The hashmap returned has for every word of sentic net the  value of its polarity
    */
    
    public HashMap<String,Double> getdata() throws FileNotFoundException, IOException  {
        
        HashMap<String,Double> data = new HashMap<>();
        BufferedReader bf = new BufferedReader(new FileReader(inputFile));
        
        String currentWord = null;
        
        String line = bf.readLine();
        
        while(line != null){
            
            int index = line.indexOf("rdf:Description");
            
            if(index == -1){
                
                index = line.indexOf("polarity");
                if(index != -1){
                    int start = line.indexOf('>') + 1;
                    int end   = line.lastIndexOf('<');
                    data.put(currentWord,
                             Double.parseDouble(line.substring(start, end)));
                    
                }
                
            }
            else{
                int start = line.lastIndexOf('/') + 1;
                int end   = line.lastIndexOf('>') - 1;
                currentWord = line.substring(start, end);                
            }
            
            line = bf.readLine();
            
        }
        
        bf.close();
        
        return data;
        
    }
    
    
    
}
