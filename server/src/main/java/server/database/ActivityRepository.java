package server.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import server.Activity;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
    /**
     * query to return all the activities in the database as a list of activity
     * @return all the activities in the database
     */
    @Query("SELECT a FROM Activity a")
    List<Activity> getAllActivities();

    /**
     * Query to find an activity with a given title
     * @param title Title of the activity
     * @return The Activity with the given title
     */
    Optional<Activity> findByTitle(String title);


//    /**
//     * @param id of the activity that you wish to delete
//     */
//    @Query ("DELETE FROM Activity WHERE Activity .Id = id")
//    void deleteById(Long id);
}
