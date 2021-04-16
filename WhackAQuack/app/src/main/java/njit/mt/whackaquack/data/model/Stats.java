package njit.mt.whackaquack.data.model;

import java.util.HashMap;

public class Stats {
    public int getPoints() {
        return points;
    }

    public int getExperience() {
        return experience;
    }

    public String getUpdated() {
        return updated;
    }

    public String getUid() {
        return uid;
    }

    public HashMap<String, Integer> getPlays() {
        return plays;
    }

    public int getLevel() {
        return level;
    }

    private int points;
    private int experience;
    private String updated;
    private String uid;
    private HashMap<String, Integer> plays = new HashMap<String, Integer>();
    private int level;
    public Stats(int points, int experience, String updated, String uid, HashMap<String, Integer> plays, int level){
        this.points = points;
        this.experience = experience;
        this.updated = updated;
        this.uid = uid;
        this.plays = plays;
        this.level = level;
    }
    @Override
    public String toString(){
        return String.format("uid: %s, points: %s, experience: %s, updated: %s, level: %s", getUid(), getPoints(), getExperience(), getUpdated(), getLevel());
    }
}
