package commons;

import java.util.List;
import java.util.Map;

public class TrimmedGame {
    private int id;
    private QuestionTrimmed question;
    private Map<String, Player> players;
    private Round round;
    private List<String[]> reactionHistory;

    /**
     * @param id Id of the game
     * @param question the current question shown to the user
     * @param players
     * @param round
     * @param reactionHistory
     */
    public TrimmedGame(int id, QuestionTrimmed question, Map<String, Player> players,
                       Round round, List<String[]> reactionHistory) {
        this.id = id;
        this.question = question;
        this.players = players;
        this.round = round;
        this.reactionHistory = reactionHistory;
    }

    /**
     * Getter for the game id
     * @return the game id
     */
    public int getId() {
        return id;
    }

    /**
     * getter for the players
     * @return map of the players
     */
    public Map<String, Player> getPlayers() {
        return players;
    }

    /**
     * getter for question
     * @return the question class trimmed
     */
    public QuestionTrimmed getQuestion() {
        return question;
    }

    /**
     * getter for the round
     * @return the round
     */
    public Round getRound() {
        return round;
    }

    /**
     * Return the reaction history of this trimmed Game
     * @return the list of the reaction history
     */
    public List<String[]> getReactionHistory() {
        return this.reactionHistory;
    }

    @Override
    public String toString() {
        return "TrimmedGame{" +
                "id=" + id +
                ", question=" + question +
                ", players=" + players +
                ", round=" + round +
                ", reactionHistory=" + reactionHistory +
                '}';
    }
}
