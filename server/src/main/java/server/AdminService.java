package server;

import commons.CommonsActivity;
import org.springframework.beans.factory.annotation.Autowired;
import server.database.ActivityRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminService {
    @Autowired
    private ActivityRepository dt;

    /**
     * Returns a List of all activites, but converted into CommonsActivities
     * @return A List of CommonsActivities
     */
    public List<CommonsActivity> getActivities(){
        List<Activity> activities = dt.getAllActivities();
        List<CommonsActivity> commonsActivities = new ArrayList<>();
        for(int i=0;i<activities.size();i++){
            commonsActivities.add(activities.get(i).convertCommonsActivity());
            System.out.println(commonsActivities.get(i));
        }

        return commonsActivities;
    }

    /**
     * @param description
     * @param power
     */
    public void addActivity(String description, int power) {
        Activity activity = new Activity(description, power, "not null", "not null");
        dt.save(activity);
    }
}
