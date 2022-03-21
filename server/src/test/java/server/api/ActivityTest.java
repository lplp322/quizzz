package server.api;
//CHECKSTYLE:OFF
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Activity;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {
    private Activity act;

    @BeforeEach
    void setup() {
        act = new Activity("Title", 2000, "Source", "Path");
    }

    @Test
    void setSource() {
        assertNotEquals("OtherSource", act.getSource());
        act.setSource("OtherSource");
        assertEquals("OtherSource", act.getSource());
    }

    @Test
    void setImagePath() {
        assertNotEquals("OtherPath", act.getImagePath());
        act.setImagePath("OtherPath");
        assertEquals("OtherPath", act.getImagePath());
    }

    @Test
    void getSource() {
        assertEquals("Source", act.getSource());
    }

    @Test
    void getImagePath() {
        assertEquals("Path", act.getImagePath());
    }

    @Test
    void setTitle() {
        assertNotEquals("OtherTitle", act.getTitle());
        act.setTitle("OtherTitle");
        assertEquals("OtherTitle", act.getTitle());
    }

    @Test
    void setConsumption() {
        assertNotEquals(42, act.getConsumption());
        act.setConsumption(42);
        assertEquals(42, act.getConsumption());
    }

    @Test
    void getTitle() {
        assertEquals("Title", act.getTitle());
    }

    @Test
    void getConsumption() {
        assertEquals(2000, act.getConsumption());
    }

    @Test
    void compareToLower() {
        Activity actLower = new Activity("TitleTitle", 1000, "Paths", "Source");
        assertEquals(-1, actLower.compareTo(act));
    }

    @Test
    void compareToEqual() {
        Activity actEqual = new Activity("TitleTitle", 2000, "Paths", "Source");
        assertEquals(0, actEqual.compareTo(act));
    }

    @Test
    void compareToHigher() {
        Activity actHigher = new Activity("TitleTitle", 3000, "Paths", "Source");
        assertEquals(1, actHigher.compareTo(act));
    }

    @Test
    void testToString() {
        assertEquals("Activity{Id=null, name='Title', energy=2000}", act.toString());
    }
}