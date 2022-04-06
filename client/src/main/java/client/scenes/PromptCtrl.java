package client.scenes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URL;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class PromptCtrl {
    private final MainCtrl mainCtrl;
    private boolean isSingleplayer; //Holds information about the mode of the game

    @FXML
    private AnchorPane mainWindow;
    @FXML
    private Button menuButton;
    @FXML
    private Button startButton;
    @FXML
    private TextField nameField;
    @FXML
    private Label errorLabel;

    /**
     * Create a new Prompt controller
     * @param mainCtrl - the Main controller
     */
    @Inject
    public PromptCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
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
     * Is executed when clickng on the start button, checks the name and initiates connection to server
     * @throws MalformedURLException
     */
    @FXML
    public void onClickStart() throws MalformedURLException {
        if(nameField.getText().matches("[a-zA-Z0-9]+")){    //regex for checking name
            errorLabel.setVisible(false);   //makes the errorLabel visible
            URL playerGame;
            if(isSingleplayer) {
                playerGame = new URL(mainCtrl.getLink() + "singleplayer/" + nameField.getText());
            }
            else playerGame = new URL(mainCtrl.getLink() + "multiplayer/" + nameField.getText());
                this.mainCtrl.setName(nameField.getText());
                try {
                    URLConnection nameVerify  = playerGame.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(nameVerify.getInputStream()));
                    String inputLine = in.readLine();
                    int ID = Integer.parseInt(inputLine);
                    if(ID == 0){
                        errorLabel.setVisible(true);
                        errorLabel.setText("Your name is already taken");
                        mainCtrl.playSound("error");
                        return;
                    }
                    this.mainCtrl.setCurrentGameID(ID); //sets the gameID of the MainCtrl to the one received
                    mainCtrl.playSound("success");
                    if(isSingleplayer)  this.mainCtrl.showGame();  //shows the game screen

                    else this.mainCtrl.showWaitingRoom(); //lobby screen needed to be here instead of overview
                } catch (IOException e) {
                    errorLabel.setVisible(true);
                    errorLabel.setText("Could not connect to server!");
                    mainCtrl.playSound("error");
                }
        }
        else{
            mainCtrl.playSound("error");
            if(nameField.getText().equals(""))      //checks if name is empty
                errorLabel.setText("Name cannot be empty!");
            else errorLabel.setText("Name can only contain letters/numbers!");
            errorLabel.setVisible(true);
        }
    }

    /**
     * Is executed when clicking on the menu button
     */
    public void onClickMenu(){
        mainCtrl.playSound("success");
        this.mainCtrl.showSplash();
    }

    /**
     * Sets the mode to Singleplayer
     */
    public void setSingleplayer(){
        isSingleplayer = true;
    }

    /**
     * Sets the mode to Multiplayer
     */
    public void setMultiplayer(){
        isSingleplayer = false;
    }
}
