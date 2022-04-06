package commons;

import org.junit.jupiter.api.Test;
//CHECKSTYLE:OFF
import static org.junit.jupiter.api.Assertions.*;
//CHECKSTYLE:ON

class HalveTimeJokerTest {

    @Test
    void tickDown() {
        HalveTimeJoker halveTimeJoker = new HalveTimeJoker(new Player("Someone"), 10);
        halveTimeJoker.tickDown();
        assertEquals(9, halveTimeJoker.getHalvedTimer());
    }

    @Test
    void getUsersName() {
        HalveTimeJoker halveTimeJoker = new HalveTimeJoker(new Player("Someone"), 10);
        assertEquals("Someone", halveTimeJoker.getUsersName().getName());
    }
}