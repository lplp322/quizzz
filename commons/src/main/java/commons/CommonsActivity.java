package commons;
//FOR NOW THIS IS LITERALLY THE ACTIVITY CLASS BUT WITH A DIFFERENT NAME
public class CommonsActivity {
    private Long id;
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
     * @param Id is the id of the activity
     */
    public CommonsActivity( Long Id, String title, int consumption, String source, String imagePath) {
        this.id = Id;
        this.title = title;
        this.consumption = consumption;
        this.source = source;
        this.imagePath = imagePath;
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
        this.title = name;
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

    @Override
    public String toString() {
        return "CommonsActivity{" +
                "Id=" + id +
                ", title='" + title + '\'' +
                ", consumption=" + consumption +
                ", source='" + source + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }

    /**
     * @return the id of the activity
     */
    public Long getId() {
        return id;
    }
}
