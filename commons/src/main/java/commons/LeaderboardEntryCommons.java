package commons;

public class LeaderboardEntryCommons implements Comparable {
    private String name;
    private int score;

    /**
     * Constructor for leaderboard entry which can be used on client side
     * @param name the name of the entry
     * @param score the score of the entry
     */
    public LeaderboardEntryCommons(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     * Compares the current leaderboard entry to another on their score
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        boolean g = score < ((LeaderboardEntryCommons)o).getScore();
        if(g) return 1;
        else return -1;
    }

    /**
     * A getter for the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * A getter for the score
     * @return the score
     */
    public int getScore() {
        return score;
    }
}
