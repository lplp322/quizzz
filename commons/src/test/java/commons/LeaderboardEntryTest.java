package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//CHECKSTYLE:OFF
import static org.junit.jupiter.api.Assertions.*;
//CHECKSTYLE:ON
class LeaderboardEntryTest {
    LeaderboardEntry entry;
    LeaderboardEntry entry2;

    @BeforeEach
    void config(){
    entry = new LeaderboardEntry();
    entry2 = new LeaderboardEntry("OPAPA", 1234);
    }

    @Test
    void getName() {
        entry.setName("OPAPA");
        assertEquals("OPAPA", entry.getName());
    }

    @Test
    void getScore() {
        entry.setScore(1234);
        assertEquals(1234, entry.getScore());
    }

    @Test
    void compareTo() {
        entry.setScore(123);
        assertEquals(1, entry.compareTo(entry2));
        assertEquals(-1, entry2.compareTo(entry));
    }
}