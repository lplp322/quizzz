package commons;

public class Joker {
    private String jokerName;

    /**
     * Default constructor for a joker
     * @param jokerName
     */
    public Joker(String jokerName) {
        this.jokerName = jokerName;
    }

    /**
     * returns the name of the joker
     * @return joker's name
     */
    public String getJokerName() {
        return jokerName;
    }

    @Override
    public String toString() {
        return "Joker{" +
                "jokerName='" + jokerName + '\'' +
                '}';
    }
}
