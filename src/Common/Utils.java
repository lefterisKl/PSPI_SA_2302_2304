package Common;


public class Utils {
    
    
    //True if it contains only latin characters, whitespace, punctiation
    //and numbers.
    public static boolean isEnglish(String str){
        return str.matches("[\\s\\p{Punct}0-9\\p{IsLatin}]*");
    }
    
    
}
