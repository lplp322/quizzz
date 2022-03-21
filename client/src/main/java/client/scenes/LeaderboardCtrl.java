package client.scenes;

import commons.LeaderboardEntry;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.util.List;

public class LeaderboardCtrl {
    private final MainCtrl mainCtrl;

    private final double fontSize = 20;

    @FXML
    private AnchorPane mainWindow;

    @FXML
    private AnchorPane scrollPanel;

    @FXML
    private Text myResult;

    @FXML
    private Text goldScore;

    @FXML
    private Text goldName;

    @FXML
    private Text silverScore;

    @FXML
    private Text silverName;

    @FXML
    private Text bronzeScore;

    @FXML
    private Text bronzeName;

    /**
     *
     * @param mainCtrl
     */
    @Inject
    public LeaderboardCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     *
     * @param w
     * @param h
     */
    @FXML
    public void setWindowSize(double w, double h) {
        mainWindow.setPrefSize(w, h);
    }

    /**
     * Displays all the results
     * @param results
     * @param yourEntry
     */
    public void displayResults(List<LeaderboardEntry> results, LeaderboardEntry yourEntry) {
        int yourPlace = 0;

        Text[] firstThree = {goldName, goldScore, silverName, silverScore, bronzeName, bronzeScore};

        for(int i = 0; i < results.size(); i++) {
            LeaderboardEntry tempEntry = results.get(i);

            if(i < 3) {
                int tempPlayer = i * 2;

                firstThree[tempPlayer].setText(tempEntry.getName());
                firstThree[tempPlayer + 1].setText(tempEntry.getScore() + " POINTS");
            }

            Text place = new Text(i + 1 + ".");
            Text name = new Text(tempEntry.getName());
            Text score = new Text(tempEntry.getScore() + " POINTS");

            Font font = new Font(fontSize);

            place.setFont(font);
            name.setFont(font);
            score.setFont(font);

            AnchorPane.setTopAnchor(place, fontSize*i);
            AnchorPane.setLeftAnchor(place, 30.0);

            AnchorPane.setTopAnchor(name, fontSize*i);
            AnchorPane.setLeftAnchor(name, 70.0);

            AnchorPane.setTopAnchor(score, fontSize*i);
            AnchorPane.setLeftAnchor(score, 320.0);

            scrollPanel.getChildren().add(place);
            scrollPanel.getChildren().add(name);
            scrollPanel.getChildren().add(score);

            // the check for 0 must be done to ensure we get the highest position
            if(yourPlace == 0 && tempEntry.getScore() == yourEntry.getScore()) {
                yourPlace = i + 1;
            }
        }
        scrollPanel.setPrefSize(300, 20*results.size());

        //myResult.setFont(new Font(30));
        myResult.setText(String.format("You came in: %d with %d POINTS", yourPlace, yourEntry.getScore()));
    }

    /**
     * Exits the leaderboard screen and goes back to the splash screen
     */
    public void backToMainScreen() {
        this.mainCtrl.showSplash();
    }

}
