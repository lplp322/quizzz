package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//CHECKSTYLE:OFF
import static org.junit.jupiter.api.Assertions.*;
//CHECKSTYLE:ON

import java.util.ArrayList;
import java.util.HashMap;


class TrimmedGameTest {
    private TrimmedGame trimGame;
    @BeforeEach
    void config(){
        trimGame = new TrimmedGame(1, new QuestionTrimmed("Somethink",
                new ArrayList<String>(), 1, "", ""), new HashMap<>(), new Round(), new ArrayList<>());
    }

    @Test
    void getId() {
        assertTrue(trimGame.getId() == 1);
    }

    @Test
    void getPlayers() {
        assertTrue(trimGame.getPlayers().size() == 0);
    }

    @Test
    void getQuestion() {
        assertTrue(trimGame.getQuestion().getQuestion().equals("Somethink"));
    }

    @Test
    void getRound() {
        assertTrue(trimGame.getRound().isTimeoutActive() == false);
    }

    @Test
    void getReactionHistory() {
        assertTrue(trimGame.getReactionHistory().size()== 0);
    }

    @Test
    void testToString() {
        System.out.println(trimGame.toString());
        assertEquals("TrimmedGame{id=1, question="+trimGame.getQuestion().toString()+ ", " +
                "players={}, round=Round{round=0, timer=20, timeout=false}, reactionHistory=[]}", trimGame.toString());
    }
}