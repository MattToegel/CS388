package njit.mt.whackaquack.data.model;


public class ScoreData{
    private String appKey;
    private String created;
    private int score;
    private String uid;
    private String id;

    private int maxScore;
    public ScoreData(String appKey, String uid, int score, String created, String id){
        this.appKey = appKey;
        this.uid = uid;
        this.score = score;
        this.created = created;
        this.id = id;
    }
    public String getId(){
        return id;
    }

    public String getAppKey(){
        return appKey;
    }
    public String getCreated(){
        return created;
    }
    public int getScore(){
        return score;
    }
    public String getUid(){
        return uid;
    }

    /***
     * Pass the potential max score of your game.
     * Internally used to calculate Experience and Points based on score/maxScore percentage
     * @param max
     */
    public void setMaxScore(int max){
        this.maxScore = max;
    }
    public int getMaxScore(){
        return maxScore;
    }
    @Override
    public String toString(){
        return String.format("id: %s, uid: %s, score: %s, created: %s, appKey: %s", getId(), getUid(), getScore(), getCreated(), getAppKey());
    }
}