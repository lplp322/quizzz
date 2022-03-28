package commons;

public interface ActivityInterface {
    /**
     * Changes source
     * @param source
     */
    public abstract void setSource(String source);

    /**
     * Changes the path of the activity image
     * @param imagePath
     */
    public abstract void setImagePath(String imagePath);

    /**
     * Changes the title of the activtiy
     * @param name
     */
    public abstract void setTitle(String name);

    /**
     * Changes the consumption of the activity
     * @param energy
     */
    public abstract void setConsumption(int energy);

    /**
     * Getter for the consumption
     * @return Energy usage
     */
    public abstract int getConsumption();

    /**
     * Getter for the title
     * @return title of the activity
     */
    public abstract String getTitle();
}
