package server.api;
//CHECKSTYLE:OFF
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import commons.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    private Player player;

    @BeforeEach
    public void init() {
        player = new Player("P1");
    }
    @Test
    public void testName() {
        assertEquals("P1", player.getName());
    }
    @Test
    public void testScore() {
        assertEquals(0, player.getScore());
    }
}
