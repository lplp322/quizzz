package commons;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int score;
    private List<String> jokerList;
    private boolean disconnected;

    /**
     * Default constructor
     * @param name
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
        String j = "HALF";
        this.jokerList = new ArrayList<>();
        jokerList.add(j);
        disconnected = false;
    }

    /**
     * Constructor in case player has chosen special jokers
     * @param name
     * @param jokerList
     */
    public Player(String name, List<String> jokerList) {
        this.name = name;
        this.score = 0;
        this.jokerList = jokerList;
        disconnected = false;
    }

    /**
     * removes a joker for a player
     * @param joker
     */
    public void useJoker(Joker joker) {
        jokerList.remove(joker);
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
    public List<String> getJokerList() {
        return jokerList;
    }

    /**
     * sets the player state
     * @param disconnected
     */
    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    /**
     * returns whether the player has disconneted
     * @return is he ingame
     */
    public boolean isDisconnected() {
        return disconnected;
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
