package server.api;
//CHECKSTYLE:OFF
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
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

import java.sql.Time;
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
    private Player playerA;
    private Player playerB;

    @BeforeEach
    public void init() {
        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity("A", 2, "DAS", "DAS"));
        activities.add(new Activity("B", 3, "DAS", "DAS"));
        activities.add(new Activity("C", 4, "DAS", "DAS"));
        activities.add(new Activity("D", 5, "DAS", "DAS"));
        activityRepository = new TestActivityRepository(activities);
        Map<String, Player> players = new HashMap();
        playerA = new Player("A");
        playerB = new Player("B");
        players.put("A", playerA);
        players.put("B", playerB);
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
        try {
            int currTimer = game.getRound().getTimer();
            TimeUnit.SECONDS.sleep(1);
            assertEquals(currTimer - 1, game.getRound().getTimer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void noJokerTrim() {
        TrimmedGame trim = new TrimmedGame(1, game.getQuestions().get(0).getQuestion(), 00, 20,
                game.getQuestions().get(0).getAnswers(), game.getQuestions().get(0).getType(),
                game.getQuestions().get(0).getAnswer());
        TrimmedGame gameTrim = game.trim();
        assertEquals(trim, gameTrim);
    }

    @Test
    public void trimWithJokerOtherPlayer() {
        TrimmedGame expectedTrimB = new TrimmedGame(1, game.getQuestions().get(0).getQuestion(), 0, 10,
                game.getQuestions().get(0).getAnswers(), game.getQuestions().get(0).getType(),
                game.getQuestions().get(0).getAnswer());
        Thread tickThread = new Thread(game::run);
        tickThread.start();
        playerA.getJokerList().get("Time").use();
        TrimmedGame trimB = game.trim("B");
        assertEquals(trimB, expectedTrimB);
        Assertions.assertTrue(playerA.getJokerList().get("Time").isUsed());
    }

    @Test
    public void trimWithJokerSamePlayer() {
        TrimmedGame expectedTrimA = new TrimmedGame(1, game.getQuestions().get(0).getQuestion(), 0, 20,
                game.getQuestions().get(0).getAnswers(), game.getQuestions().get(0).getType(),
                game.getQuestions().get(0).getAnswer());
        Thread tickThread = new Thread(game::run);
        tickThread.start();
        playerA.getJokerList().get("Time").use();
        TrimmedGame trimA = game.trim("A");
        assertEquals(trimA, expectedTrimA);
        Assertions.assertTrue(playerA.getJokerList().get("Time").isUsed());
    }

    @Test
    public void trimWithJokerOtherPlayerAfter4Seconds() {
        TrimmedGame expectedTrimB = new TrimmedGame(1, game.getQuestions().get(0).getQuestion(), 0, 8,
                game.getQuestions().get(0).getAnswers(), game.getQuestions().get(0).getType(),
                game.getQuestions().get(0).getAnswer());
        Thread tickThread = new Thread(game::run);
        tickThread.start();
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        playerA.getJokerList().get("Time").use();
        TrimmedGame trimB = game.trim("B");
        assertEquals(trimB, expectedTrimB);
        Assertions.assertTrue(playerA.getJokerList().get("Time").isUsed());
    }

    @Test
    public void trimWithJokerOtherPlayerWaitingBeforeAfter() {
        TrimmedGame expectedTrimB = new TrimmedGame(1, game.getQuestions().get(0).getQuestion(), 0, 6,
                game.getQuestions().get(0).getAnswers(), game.getQuestions().get(0).getType(),
                game.getQuestions().get(0).getAnswer());
        Thread tickThread = new Thread(game::run);
        tickThread.start();
        try {
            TimeUnit.SECONDS.sleep(4);
            playerA.getJokerList().get("Time").use();
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TrimmedGame trimB = game.trim("B");
        assertEquals(trimB, expectedTrimB);
        Assertions.assertTrue(playerA.getJokerList().get("Time").isUsed());
    }

    @Test
    public void testTwoJokerUses() {
        Thread tickThread = new Thread(game::run);
        tickThread.start();
        playerA.getJokerList().get("Time").use();
        playerB.getJokerList().get("Time").use();

        Assertions.assertTrue(playerA.getJokerList().get("Time").isUsed());
        Assertions.assertFalse(playerB.getJokerList().get("Time").isUsed());

    }

    @Test
    public void trimWithJokerOtherPlayerNextRound() {
        Thread tickThread = new Thread(game::run);
        tickThread.start();
        playerA.getJokerList().get("Time").use();
        game.getRound().goNextRound();
        TrimmedGame trimB = game.trim("B");
        TrimmedGame trimA = game.trim("A");
        assertEquals(trimB, trimA);
        Assertions.assertTrue(playerA.getJokerList().get("Time").isUsed());
    }

    @Test
    public void scoreJokerTest() {
        Thread tickThread = new Thread(game::run);
        tickThread.start();
        playerA.getJokerList().get("Score").use();
        game.updatePlayerScore("A", 100);
        game.updatePlayerScore("B", 100);

        Assertions.assertTrue(playerA.getScore() == 2 * playerB.getScore());
        Assertions.assertTrue(playerA.getJokerList().get("Score").isUsed());
    }
}