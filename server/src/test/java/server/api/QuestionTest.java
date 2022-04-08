package server.api;
//CHECKSTYLE:OFF
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Activity;
import server.Question;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionTest {
    private Activity a, b, c, d;
    private TestActivityRepository dtBase;
    @BeforeEach
    public void init() {
        a = new Activity("A", 100, "DAS", "DAS");
        b = new Activity("B", 99, "DAS", "DAS");
        c = new Activity("C", 103, "ASD", "DAS");
        d = new Activity("D", 104, "DAS", "ADS");

    }
    @Test
    public void testTypeOne() {
        List<Activity> activities = new ArrayList<>();
        activities.add(a);
        activities.add(a);
        activities.add(a);
        activities.add(a);
        dtBase = new TestActivityRepository(activities);

        Question q = new Question(dtBase, 0);
        assertEquals(100+"", q.getAnswer());
    }
    @Test
    public void testTypeTwo() {
        List<Activity> activities = new ArrayList<>();
        activities.add(a);
        activities.add(a);
        activities.add(a);
        activities.add(a);
        dtBase = new TestActivityRepository(activities);
        Question q = new Question(dtBase, 1);
        assertEquals("100", q.getAnswer());
    }
    @Test
    public void testTypeThree() {
        List<Activity> activities = new ArrayList<>();
        activities.add(a);
        activities.add(b);
        activities.add(c);
        activities.add(d);
        dtBase = new TestActivityRepository(activities);

        Question q = new Question(dtBase, 2);
        assertEquals("D", q.getAnswer());
    }
}
