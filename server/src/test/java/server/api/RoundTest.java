package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Round;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoundTest {
    private Round round;
    @BeforeEach
    public void init() {
        round = new Round();
    }
    @Test
    public void testOneTickDown() {
        round.tickDown();
        assertTrue(round.getRound() == 0 && round.getTimer() == 19);
    }
    @Test
    public void testFullTick() {
        for(int i = 0; i < 20*20; i++) {
            round.tickDown();
        }
        assertTrue(round.getGameStatus() == 2);
    }
}
