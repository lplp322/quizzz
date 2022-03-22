package server;

import java.util.HashMap;
import java.util.Map;

public class Player {
    private String name;
    private int score;
    private Map<String, Joker> jokerList;

    /**
     * Default constructor
     * @param name
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.jokerList = new HashMap<>();
    }

    /**
     * Constructor in case player has chosen special jokers
     * @param name
     * @param jokerList
     */
    public Player(String name, Map<String, Joker> jokerList) {
        this.name = name;
        this.score = 0;
        this.jokerList = jokerList;
    }

    /**
     * returns the name of the player
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * returns the score of the player
     * @return the score of the player
     */
    public int getScore() {
        return score;
    }

    /**
     * returns a list of the jokes available to the player
     * @return the list of the jokers available to the player
     */
    public Map<String, Joker> getJokerList() {
        return jokerList;
    }

    /**
     * returns the object as a string
     * @return
     */
    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", jokerList=" + jokerList +
                '}';
    }

    /**
     * @param score the score that the user has
     */
    public void setScore(int score) {
        this.score = score;
    }
}
