package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;



public class SplashCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    private AnchorPane mainWindow;

    @FXML
    private Button singleplayerButton;

    @FXML
    private Button multiplayerButton;

    @FXML
    private Button helpButton;

    @FXML
    private Label helpLabel;

    /**
     * Instantiates a Splash Controller
     * @param mainCtrl The Main Controller
     */
    @Inject
    public SplashCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Function to be executed when the singlePlayer button is pressed
     */
    public void singlePlayer() {
        mainCtrl.playSound("success");
        this.mainCtrl.showSinglePlayerPrompt();
        this.mainCtrl.setSingleplayerFlag(true);
        System.out.println(this.mainCtrl.isSingleplayerFlag());
    }

//    /**
//     * Function to be executed when the multiPlayer button is pressed
//     */
//    public void multiPlayer(){
//        mainCtrl.playSound("success");
//        this.mainCtrl.showMultiPlayer();
//    }


    /**
     * Function to be executed when the multiplayer button is pressed
     */
    public void multiPlayer(){
        this.mainCtrl.showMultiPlayer();
        this.mainCtrl.setSingleplayerFlag(false);
        System.out.println(this.mainCtrl.isSingleplayerFlag());
        mainCtrl.playSound("success");
        this.mainCtrl.showMultiPlayer();
    }

    /**
     * Changes the size of the AnchorPlane
     * @param w - preferred width
     * @param h - preferred height
     */
    @FXML
    public void setWindowSize(double w, double h){
        mainWindow.setPrefSize(w,h);
    }

    /**
     * Shows the activity viewer
     */
    public void showActivityViewer(){
        mainCtrl.playSound("success");
        this.mainCtrl.showActivityViewer();
    }

    /**
     * Displays the leaderboard for singleplayer
     */
    public void showLeaderboard() {
        mainCtrl.playSound("success");
        this.mainCtrl.showLeaderboard(mainCtrl.getLeaderboard(), null, 0);
    }
//    public void showActivityViewer(){this.mainCtrl.showActivityViewer();}


    /**
     * shows the help label instructing the user how the game works
     */
    public void showHelp() {
        mainCtrl.playSound("success");
        this.helpLabel.setWrapText(true);

        if (this.helpLabel.isVisible()) {
            this.helpLabel.setVisible(false);
        }

        else {
            this.helpLabel.setVisible(true);
        }
    }
}