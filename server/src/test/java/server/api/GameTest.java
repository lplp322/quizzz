package server.api;
//CHECKSTYLE:OFF
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import server.Activity;
import server.Game;
import server.Player;
import commons.TrimmedGame;
import server.database.ActivityRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GameTest {
    @Mock
    private ActivityRepository activityRepository;
    private List<Player> players;
    private Game game;

    @BeforeEach
    public void init() {
        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity("A", 2, "DAS", "DAS"));
        activities.add(new Activity("B", 3, "DAS", "DAS"));
        activities.add(new Activity("C", 4, "DAS", "DAS"));
        activities.add(new Activity("D", 5, "DAS", "DAS"));
        activityRepository = new TestActivityRepository(activities);
        Map<String, Player> players = new HashMap();
        players.put("A", new Player("A"));
        players.put("B", new Player("B"));
        game = new Game(players,
                1, 1, activityRepository);
    }

    @Test
    public void testGeneral() {
        assertEquals(20, game.getQuestions().size());
    }

    @Test
    public void testPlayers() {
        assertEquals("A", game.getPlayers().get("A").getName());
    }

    @Test
    public void testGameType() {
        assertEquals(1, game.getGameType());
    }

    @Test
    public void testGameId() {
        assertEquals(1, game.getLobbyId());
    }

    @Test
    public void testThreadTick() {
        Thread tickThread = new Thread(game::run);
        tickThread.start();
        int currTimer = game.getRound().getTimer();
        try {
            TimeUnit.SECONDS.sleep(1);
            assertEquals(currTimer - 1, game.getRound().getTimer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void noJokerTrim() {
        TrimmedGame trim = new TrimmedGame(1, game.getQuestions().get(0).getQuestion(), 20, 20,
                game.getQuestions().get(0).getAnswers(), game.getQuestions().get(0).getType(),
                game.getQuestions().get(0).getAnswer());
        TrimmedGame gameTrim = game.trim();
        assertEquals(trim, gameTrim);
    }
}