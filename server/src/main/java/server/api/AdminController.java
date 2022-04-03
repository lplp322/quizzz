package server.api;

import commons.CommonsActivity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import server.AdminService;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    AdminService adminService;


    /**
     * Constructor for the AdminController class
     * @param adminService
     */
    public AdminController(AdminService adminService ) {
        this.adminService = adminService;
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
}
