package server;

public class TimeJoker extends Joker{

    /**
     * Constructor given game and player
     * @param game Game the joker is part of
     * @param player The player the joker belongs to
     */
    public TimeJoker(Game game, Player player){
        super(game, player);
    }

    /**
     * Empty constructor
     */
    public TimeJoker() {

    }

    /**
     * The joker acts on the current round; sets a half timer for every other player in the game
     */
    public void use(){
        Round round = game.getRound();
        if (this.used || round.isHalfTimerUsed() || round.getGameStatus() != 1) {
            return;
        }
        this.used = true;
        round.setPlayerWhoUsedJoker(player);
        round.setHalfTimerUsed(true);
        round.setHalvedTimer();
    }

}
