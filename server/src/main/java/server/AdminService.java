package server;

import commons.CommonsActivity;
import org.springframework.beans.factory.annotation.Autowired;
import server.database.ActivityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            //System.out.println(commonsActivities.get(i));
        }

        return commonsActivities;
    }

//    /**
//     * @param description
//     * @param power
//     */
//    public void addActivity(String description, int power) {
//        Activity activity = new Activity(description, power, "not null", "not null");
//        dt.save(activity);
//    }


    /**
     * @param description description of the activity
     * @param power the power consumption of the activity
     * @param source the source related to this information
     * @param imagePath the image path
     */
    public void addActivity(String description, int power, String source, String imagePath) {
        Activity activity = new Activity(description, power, source, imagePath);
        //System.out.println(activity);
        dt.save(activity);
    }

    /**
     * @param id the id of the activity you want to delete
     */
    public void deleteActivity(Long id) {
        Optional<Activity> activity = dt.findById(id);
        //System.out.println("DA");
        if(activity.isPresent()) {
            dt.deleteById(id);
        }
        else {
            System.out.println("NOT PRESENT");
        }
    }
}
