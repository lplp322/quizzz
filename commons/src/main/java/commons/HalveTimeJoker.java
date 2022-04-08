package commons;

public class HalveTimeJoker extends Joker{
    private Player usersName;
    private int halvedTimer;

    /**
     * Constructor for the halve time joker
     * @param usersName
     * @param halvedTimer
     */
    public HalveTimeJoker(Player usersName, int halvedTimer) {
        super("HALF");
        this.usersName = usersName;
        this.halvedTimer = halvedTimer;
    }

    /**
     * ticks the timer down
     */
    public void tickDown() {
        halvedTimer--;
    }

    /**
     * getter for the user's name
     * @return name
     */
    public Player getUsersName() {
        return usersName;
    }

    /**
     * A setter for the halvedTimer
     * @param halvedTimer new value for halvedTimer
     */
    public void setHalvedTimer(int halvedTimer) {
        this.halvedTimer = halvedTimer;
    }

    /**
     * getter for the halved timer
     * @return timer
     */
    public int getHalvedTimer() {
        return halvedTimer;
    }
}
