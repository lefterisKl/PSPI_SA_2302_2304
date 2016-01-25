package Common;


/**
 * This class is used for representing emoticons
 * It contains informations such as its representation in string format,
 * the emotion it represents.
*/
public class Emoticon {
    private Emotion emotion;
    private String emoticon;
    private Number positiveScore;
    private Number negativeScore;
    
    //constructors
    public  Emoticon(Emotion emotion,Number positiveScore,Number negativeScore,String emoticon)
    {
        this.emotion = emotion;
        this.positiveScore = positiveScore;
        this.negativeScore = negativeScore;
        this.emoticon=emoticon;
        
    }
    //accessors
    public Emotion getEmotion()
    {
        return emotion;
    }
    
    public String getEmoticon()
    {
        return emoticon;
    }
    
    public Number getPositiveScore()
    {
        return positiveScore;
    }

    public Number getNegativeScore()
    {
        return negativeScore;
    }
    
    //mutators
    public void setEmotion(Emotion emotion)
    {
        this.emotion = emotion;
    }
    
    public void setEmoticon(String emoticon)
    {
        this.emoticon = emoticon;
    }
    
    public void setPositiveScore(Number positiveScore)
    {
        this.positiveScore = positiveScore;
    }
    
    public void setNegativeScore(Number negativeScore)
    {
        this.negativeScore = negativeScore;
    }
    
    public static void main(String[] args)
    {
        Emoticon emo = new Emoticon(Emotion.ANGER,1,2,";-(");
        System.out.println(emo.getEmotion()+" "+emo.getPositiveScore()+" "+emo.getNegativeScore()+" "+emo.getEmoticon());
    }
    
}
