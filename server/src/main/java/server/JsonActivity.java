package server;

public class JsonActivity {
    private String title;
    private long consumption_in_wh;
    private String source;

    /**
     * Constructor used for JSON parsing
     * @param title title
     * @param consumption_in_wh consumption
     * @param source source link
     */
    public JsonActivity(String title, long consumption_in_wh, String source) {
        this.title = title;
        this.consumption_in_wh = consumption_in_wh;
        this.source = source;
    }

    /**
     * Empty Constructor
     */
    public JsonActivity() {
    }

    /**
     * Getter for title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for consumption
     * @return consumption
     */
    public long getConsumption_in_wh() {
        return consumption_in_wh;
    }

    /**
     * Getter for source
     * @return source
     */
    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "JSONActivity{" +
                "title='" + title + '\'' +
                ", consumption_in_wh=" + consumption_in_wh +
                ", source='" + source + '\'' +
                '}';
    }

    /**
     * Returns an Activity with the same attributes, plus a given image path
     * @param imagePath the path to the image
     * @return completed Activity
     */
    public Activity toActivity(String imagePath){
        return new Activity(title, (int) consumption_in_wh, source, imagePath);
    }


}
