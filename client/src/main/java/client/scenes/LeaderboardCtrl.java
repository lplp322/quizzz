package client.scenes;

import commons.LeaderboardEntryCommons;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    private Label myResult;

    @FXML
    private Label goldScore;

    @FXML
    private Label goldName;

    @FXML
    private Label silverScore;

    @FXML
    private Label silverName;

    @FXML
    private Label bronzeScore;

    @FXML
    private Label bronzeName;

    @FXML
    private AnchorPane goldBar;

    @FXML
    private AnchorPane silverBar;

    @FXML
    private AnchorPane bronzeBar;

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
     * Displays no bars
     */
    public void showZeroBars() {
        goldBar.setPrefHeight(0);
        silverBar.setPrefHeight(0);
        bronzeBar.setPrefHeight(0);
    }

    /**
     * Displays one bar
     * @param minHeight the minHeight the bar can get
     * @param barHeight the height of the bar
     * @param barY the y location of the bar
     */
    public void showOneBar(double minHeight, double barHeight, double barY) {
        goldBar.setPrefHeight(Math.max(minHeight, barHeight));
        goldBar.setLayoutY(barY + (barHeight-(goldBar.getPrefHeight())));
        silverBar.setPrefHeight(0);
        bronzeBar.setPrefHeight(0);
    }

    /**
     * Displays two bars
     * @param results the results list
     * @param minHeight the minHeight the bar can get
     * @param barHeight the height of the bar
     * @param barY the y location of the bar
     */
    public void showTwoBars(List<LeaderboardEntryCommons> results, double minHeight, double barHeight, double barY) {
        double xToY = (1.00 * results.get(0).getScore()/results.get(1).getScore());
        double silverRatio = 1.00 / (1 + xToY);
        double goldRatio = silverRatio * xToY;
        goldBar.setPrefHeight(Math.max(minHeight, goldRatio*barHeight));
        goldBar.setLayoutY(barY + (barHeight-(goldBar.getPrefHeight())));
        silverBar.setPrefHeight(Math.max(minHeight,silverRatio*barHeight));
        silverBar.setLayoutY(barY + (barHeight-(silverBar.getPrefHeight())));
        bronzeBar.setPrefHeight(0);
    }

    /**
     * Displays three bars
     * @param results the results list
     * @param minHeight the minHeight the bar can get
     * @param barHeight the height of the bar
     * @param barY the y location of the bar
     */
    public void showThreeBars(List<LeaderboardEntryCommons> results, double minHeight, double barHeight, double barY) {
        double xToY = (1.00 * results.get(0).getScore()/results.get(1).getScore());
        double yToZ = (1.00 * results.get(1).getScore()/results.get(2).getScore());
        double bronzeRatio = 1.00 / (xToY*yToZ + yToZ + 1);
        double silverRatio = yToZ * bronzeRatio;
        double goldRatio = xToY * silverRatio;
        goldBar.setPrefHeight(Math.max(minHeight,goldRatio*barHeight));
        goldBar.setLayoutY(barY + (barHeight-(goldBar.getPrefHeight())));
        silverBar.setPrefHeight(Math.max(minHeight,silverRatio*barHeight));
        silverBar.setLayoutY(barY + (barHeight-(silverBar.getPrefHeight())));
        bronzeBar.setPrefHeight(Math.max(minHeight,bronzeRatio*barHeight));
        bronzeBar.setLayoutY(barY + (barHeight-(bronzeBar.getPrefHeight())));
    }

    /**
     * Displays the bars with appropriate sizes
     * @param results list of entries
     * @param firstThree the labels for the first three players
     * @param bars the gold, silver and bronze bars
     */
    public void displayBars(List<LeaderboardEntryCommons> results, Label[] firstThree, AnchorPane[] bars) {
        final double barHeight = 96, barY = 21, minHeight = 21;
        // reset the bar locations
        for(int i = 0; i < 3; i++) {
            bars[i].setPrefHeight(63);
            bars[i].setLayoutY(35);
        }
        for(int i = 0; i < firstThree.length; i++) {
            firstThree[i].setText("");
        }
        int curCase = 0;
        for(int i = 0; i < Math.min(3,results.size()); i++) {
            if(results.get(i).getScore() == 0) break;
            curCase++;
        }
        switch (curCase) {
            case 0:
                showZeroBars();
                break;
            case 1:
                showOneBar(minHeight, barHeight, barY);
                break;
            case 2:
                showTwoBars(results, minHeight, barHeight, barY);
                break;
            default:
                showThreeBars(results, minHeight, barHeight, barY);
        }
    }

    /**
     * Displays all the results
     * @param results the list of results
     * @param yourEntry your score
     */
    public void displayResults(List<LeaderboardEntryCommons> results, LeaderboardEntryCommons yourEntry) {
        int yourPlace = 0;
        scrollPanel.getChildren().clear();
        Label[] firstThree = {goldName, goldScore, silverName, silverScore, bronzeName, bronzeScore};
        AnchorPane[] bars = {goldBar, silverBar, bronzeBar};
        displayBars(results, firstThree, bars);
        for(int i = 0; i < results.size(); i++) {
            LeaderboardEntryCommons tempEntry = results.get(i);

            if(i < 3 && results.get(i).getScore() != 0) {
                int tempPlayer = i * 2;
                firstThree[tempPlayer].setText(tempEntry.getName());
                firstThree[tempPlayer].setLayoutY(-firstThree[tempPlayer].getPrefHeight());
                firstThree[tempPlayer + 1].setText(tempEntry.getScore() + " POINTS");
                firstThree[tempPlayer + 1].setLayoutY(bars[i].getPrefHeight()-firstThree[tempPlayer+1].getPrefHeight());
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
