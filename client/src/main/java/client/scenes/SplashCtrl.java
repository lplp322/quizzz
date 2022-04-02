package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class SplashCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    private AnchorPane mainWindow;

    @FXML
    private Button singleplayerButton;

    @FXML
    private Button multiplayerButton;

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
    }

    /**
     * Function to be executed when the multiPlayer button is pressed
     */
    public void multiPlayer(){
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
}