package server;

public abstract class Joker {
    boolean used;
    Game game;
    Player player;

    /**
     * Useful constructor that takes the game and player to work on
     * @param game Game the joker is part of
     * @param player Player the joker belongs to
     */
    public Joker(Game game, Player player) {
        this.used = false;
        this.game = game;
        this.player = player;
    }

    /**
     * Empty constructor
     */
    public Joker() {

    }

    /**
     * Getter for whether the joker has been used or not
     * @return whether the joker is used or not
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Abstract method for how a joker will act when it is used
     */
    public abstract void use();
}
