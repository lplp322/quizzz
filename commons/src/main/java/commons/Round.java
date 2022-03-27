package commons;

import java.util.HashMap;
import java.util.Map;

public class Round {
    // the number of the current round
    private int round;
    // how many seconds are left
    private int timer;
    // 1 - game running, 2 - game has ended
    private int gameStatus;
    private boolean timeoutActive = false;
    private final int totalRounds = 20;
    private final int roundTimer = 20;
    private Map<Object, Joker> gameChangingJokers;

    /**
     * By default, first round is 0 and timer counts down from 20
     */
    public Round() {
        this.round = 0;
        this.timer = roundTimer;
        this.gameChangingJokers = new HashMap<>();
        this.gameStatus = 1;
    }

    /**
     * Method that simulates the game rounds
     * when the last round has passed, change the game status to 2
     */
    public void tickDown() {
        timer--;
        if(gameChangingJokers.containsKey(HalveTimeJoker.class)) {
            HalveTimeJoker tempJoker = (HalveTimeJoker) gameChangingJokers.get(HalveTimeJoker.class);
            tempJoker.tickDown();
        }

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
            gameChangingJokers.clear();
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
                '}';
    }
}
