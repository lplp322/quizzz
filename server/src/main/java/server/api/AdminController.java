package server.api;

import commons.CommonsActivity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import server.AdminService;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    /**
     * Constructor for the AdminController class
     */
    @Autowired
    public AdminController() {
    }
    /**
     * Mapping to test how the ActivityInterface behaves
     * @return A list containing two test activities
     */
    @GetMapping("/activities")
    @ResponseBody
    public List<CommonsActivity> newPlayer() {
        List<CommonsActivity> testList = adminService.getActivities();
        return testList;
    }

    /**
     *
     * @param description
     * @param usage
     * @param source
     * @param imagePath
     */
    @PutMapping("new_activity/{description}/{usage}/{source}/{imagePath}")
    public void newActivity(@PathVariable String description, @PathVariable String usage,
                            @PathVariable String source, @PathVariable String imagePath) {
        //System.out.println("DASDAS");
        adminService.addActivity(splitString(description),
                Integer.parseInt(usage), fixPath(source),  fixPath(imagePath));
    }

    /**
     * @param id the id of the activity you would like to delete
     */
    @DeleteMapping("delete_activity/{id}")
    public void deleteActivity(@PathVariable Long id) {
        Long longid = id;
        //System.out.println(id);
        adminService.deleteActivity(id);
//        System.out.println(id);
    }

    /**
     * @param input the path that you want to replace the "/"s with "~"s
     * @return the path with /s instead of ~s
     */
    public String fixPath(String input) {
        String output = input.replace("~", "/");
        return output;
    }

    /**
     * @param input the string that is going to be split to include spaces
     * @return the string including spaces instead of !
     */
    public String splitString(String input) {
        String[] list = input.split("!");
        String result = list[0];
        for (int i = 1; i < list.length; i ++) {
            result = result + " " + list[i];
        }
        return result;
    }
}
