package commons;

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
    private int allPlayers;
    private int playersVoted;

    //private Map<String, Joker> gameChangingJokers;

    private HalveTimeJoker halveTimeJoker;

    /**
     * By default, first round is 0 and timer counts down from 20
     * @param allPlayers number of players
     */
    public Round(int allPlayers) {
        this.round = 0;
        this.timer = roundTimer;
        this.halveTimeJoker = null;
        this.gameStatus = 1;
        this.allPlayers = allPlayers;
        this.playersVoted = 0;
    }

    /**
     * Method that simulates the game rounds
     * when the last round has passed, change the game status to 2
     */
    public void tickDown() {
        timer--;
        if(playersVoted == allPlayers) {
            fastForwardRound();
            playersVoted = 0;
        }

        if(halveTimeJoker != null) {
            halveTimeJoker.tickDown();
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

            if(round == totalRounds) {
                gameStatus = 2;
                round = -1;
            }
            halveTimeJoker = null;
        }
//        System.out.println(round);

    }

    /**
     * Fast forwards the round if all the players have voted
     */
    public void fastForwardRound() {
        timer = Math.min(timer, 0);
        if(halveTimeJoker != null)
            halveTimeJoker.setHalvedTimer(0);
    }

    /**
     * A player has submitted his vote
     */
    public void playerAnswered() {
        playersVoted++;
    }

    /**
     * activates the halftime joker
     * @param player
     */
    public void activateHalfTime(Player player) {
        if(timer > 10) {
            //System.out.println("DAS");
            halveTimeJoker = new HalveTimeJoker(player, timer / 2);
        }
    }

    /**
     * A player has disconnected, so we don't have to consider his vote anymore
     */
    public void removePlayer() {
        allPlayers--;
    }

    /**
     * returns whether we are in a timeout
     * @return are we in a timeout
     */
    public boolean isTimeoutActive() {
        return timeoutActive;
    }

    /**
     * returns the halftimejoker
     * @return the half time joker
     */
    public HalveTimeJoker getHalveTimeJoker() {
        return halveTimeJoker;
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
