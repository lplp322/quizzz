package server;

public class Round {
    // the number of the current round
    private int round;
    // how many seconds are left
    private int timer;
    // there is a joker that halves the timer for everyone but him,
    // so we must keep the player who has used it and whether someone has
    private Player playerWhoUsedJoker;
    private boolean halfTimerUsed;
    private int halvedTimer;
    // 1 - game running, 2 - game has ended
    private int gameStatus;
    private boolean timeoutActive = false;
    private final int totalRounds = 20;
    private final int roundTimer = 20;

    /**
     * By default, first round is 0 and timer counts down from 20
     */
    public Round() {
        this.round = 0;
        this.timer = roundTimer;
        this.halfTimerUsed = false;
        this.gameStatus = 1;
    }

    /**
     * Method that simulates the game rounds
     * when the last round has passed, change the game status to 2
     */
    public void tickDown() {
        timer--;
        if (timer == 0){
            timeoutActive = true;
        }
        else if(timer <= 0 && !timeoutActive) {
            timer = 20;
        }
        else if (timer == -5){
            timeoutActive = false;
            timer = 20;
            round++;
        }
        if(round == totalRounds-1) {
            gameStatus = 2;
        }
    }

    /**
     * sets the rounds
     * @param round
     */
    public void setRound(int round) {
        this.round = round;
    }

    /**
     * sets the timer
     * @param timer
     */
    public void setTimer(int timer) {
        this.timer = timer;
    }

    /**
     * sets the halved timer
     * @param halvedTimer
     */
    public void setHalvedTimer(int halvedTimer) {
        this.halvedTimer = halvedTimer;
    }

    /**
     * sets whether the halftimer is being used
     * @param halfTimerUsed
     */
    public void setHalfTimerUsed(boolean halfTimerUsed) {
        this.halfTimerUsed = halfTimerUsed;
    }

    /**
     * set the player who has used the joker
     * @param playerWhoUsedJoker
     */
    public void setPlayerWhoUsedJoker(Player playerWhoUsedJoker) {
        this.playerWhoUsedJoker = playerWhoUsedJoker;
    }

    /**
     * returns on which round we are
     * @return the current round
     */
    public int getRound() {
        return round;
    }

    /**
     * returns how many seconds are left
     * @return the seconds left
     */
    public int getTimer() {
        return timer;
    }

    /**
     * returns the status of the game
     * @return the status of the game
     */
    public int getGameStatus() {
        return gameStatus;
    }

    /**
     * returns the halved timer
     * @return the galved timer
     */
    public int getHalvedTimer() {
        return halvedTimer;
    }

    /**
     * returns whether the half-timer is used
     * @return whether the half-timer is used
     */
    public boolean isHalfTimerUsed() {
        return halfTimerUsed;
    }

    /**
     * returns the player who has used the half-time joker
     * @return the player who has used the half-time joker
     */
    public Player getPlayerWhoUsedJoker() {
        return playerWhoUsedJoker;
    }

    /**
     * returns the total number of rounds
     * @return the total number of rounds
     */
    public int getTotalRounds() {
        return totalRounds;
    }

    /**
     * returns the time one round takes
     * @return the time one round takes
     */
    public int getRoundTimer() {
        return roundTimer;
    }

    /**
     * returns the object as a string
     * @return
     */
    @Override
    public String toString() {
        return "Round{" +
                "round=" + round +
                ", timer=" + timer +
                ", timeout=" + timeoutActive +
                ", halvedTimer=" + halvedTimer +
                ", halfTimerUsed=" + halfTimerUsed +
                ", playerWhoUsedJoker=" + playerWhoUsedJoker +
                '}';
    }
}
