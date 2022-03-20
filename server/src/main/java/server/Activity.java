package server;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Id;

@Entity
public class Activity implements Comparable {
    @Id
    @SequenceGenerator(
            name = "activity_sequence",
            sequenceName = "activity_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "activity_sequence"
    )

    private Long Id;
    private String title;
    private int consumption;
    private String source;
    private String imagePath;

    /**
     * Constructor for activity
     * @param title the name of the activity
     * @param consumption the energy usage of the activity
     * @param source the source of this information
     * @param imagePath the location of the image
     */
    public Activity(String title, int consumption, String source, String imagePath) {
        this.title = title;
        this.consumption = consumption;
        this.source = source;
        this.imagePath = imagePath;
    }

    /**
     * Empty constructor
     */
    public Activity() {

    }

    /**
     * Sets the source of the activity
     * @param source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Sets the path of the image
     * @param imagePath
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Returns the source of the activity
     * @return source
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns the image path
     * @return imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * sets the name of the activity
     * @param name the name of the activity
     */
    public void setTitle(String name) {
        this.title = title;
    }

    /**
     * sets the energy of the activity
     * @param energy the energy of the activity
     */
    public void setConsumption(int energy) {
        this.consumption = energy;
    }

    /**
     * returns the name of the activity
     * @return the name of the activity
     */
    public String getTitle() {
        return title;
    }

    /**
     * returns the energy of the activity
     * @return the evergy of the activity
     */
    public int getConsumption() {
        return consumption;
    }

    /**
     * compares it to another object
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        Activity otherActivity = (Activity)o;
        return (getConsumption() < otherActivity.getConsumption()?-1:1);
    }

    /**
     * returns to string
     * @return
     */
    @Override
    public String toString() {
        return "Activity{" +
                "Id=" + Id +
                ", name='" + title + '\'' +
                ", energy=" + consumption +
                '}';
    }



}
