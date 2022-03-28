package server.api;

import commons.ActivityInterface;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import server.Activity;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    /**
     * Mapping to test how the ActivityInterface behaves
     * @return A list containing two test activities
     */
    @GetMapping("/testActivity")
    @ResponseBody
    public List<ActivityInterface> newPlayer() {

        Activity testActivity1 = new Activity("Test", 420, "testSource", "testPath");
        Activity testActivity2 = new Activity("Test2", 024, "testSource", "testPath");

        List<ActivityInterface> testList = new ArrayList<>();
        testList.add(testActivity1);
        testList.add(testActivity2);

        return  testList;
    }
}
