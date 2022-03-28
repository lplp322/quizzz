package server.api;

import commons.CommonsActivity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public List<CommonsActivity> newPlayer() {

        CommonsActivity testActivity1 = new CommonsActivity("Test", 420, "testSource", "testPath");
        CommonsActivity testActivity2 = new CommonsActivity("Test2", 024, "testSource", "testPath");

        List<CommonsActivity> testList = new ArrayList<>();
        testList.add(testActivity1);
        testList.add(testActivity2);

        return testList;
    }
}
