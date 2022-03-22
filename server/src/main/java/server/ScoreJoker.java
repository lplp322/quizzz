package server;

public class ScoreJoker extends Joker{

    /**
     * Constructor given game and player
     * @param game the game this joker is a part of
     * @param player the player this joker belongs to
     */
    public ScoreJoker(Game game, Player player) {
        super(game, player);
    }

    /**
     * Empty constructor
     */
    public ScoreJoker() {

    }

    /**
     * Joker is used; adds the player to list of players to get double points this round
     */
    public void use(){
        Round round = game.getRound();
        if (this.used || round.getGameStatus() != 1) {
            return;
        }
        this.used = true;
        game.getRound().addToDoublePoints(player.getName());
    }
}
