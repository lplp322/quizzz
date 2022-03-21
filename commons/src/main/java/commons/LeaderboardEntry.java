package commons;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LeaderboardEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    private String name;
    private int score;

    /**
     * @param name the name of the player in the leaderboard
     * @param score the score of the player in the leaderboard
     */
    public LeaderboardEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     * Empty constructor for object mapper
     */
    public LeaderboardEntry() {

    }

    /**
     * @return returns the name of the player in that leaderboard entry
     */
    public String getName() {
        return name;
    }

    /**
     * @return the score of teh player in that entry of the leaderboard
     */
    public int getScore() {
        return score;
    }

    /**
     * @param name sets the name of that entry in the leaderboard
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param score sets the score for that entry in the leaderboard
     */
    public void setScore(int score) {
        this.score = score;
    }
}
