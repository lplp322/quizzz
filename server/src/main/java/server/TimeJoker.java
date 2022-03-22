package server;

public class TimeJoker extends Joker{

    public TimeJoker(Game game, Player player){
        super(game, player);
    }

    public void use(){
        Round round = game.getRound();
        if (this.used || round.isHalfTimerUsed() || round.getGameStatus() != 1) {
            return;
        }
        this.used = true;
        round.setPlayerWhoUsedJoker(player);
        round.setHalvedTimer();
    }

}
