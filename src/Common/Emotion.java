package Common;


/**
 * This is just an Enum representing the 6 basic emotions
 * We also associate each emotion - Enum with an integer for programming 
 * convenience.
 * 
 */
public enum Emotion {
    
    ANGER(0),
    DISGUST(1),
    SADNESS(2),
    SURPRISE(3),
    JOY(4),
    FEAR(5),
    NO_EMOTION(-1);
    
    
    private final int id;  
    
    Emotion(int id) {
        this.id = id;
    }
    
    public int getId(){
        return id;
    }

}
