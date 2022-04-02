package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChooseServerCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    private Label mainLabel;
    @FXML
    private Label cantConnectLabel;

    @FXML
    private TextField textField;

    @FXML
    private Button connect;

    @FXML
    private AnchorPane mainWindow;
    /**
     *
     * @param mainCtrl
     */
    @Inject
    public ChooseServerCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Used to connect to current server
     */
    public void connectToServer(){
        cantConnectLabel.setVisible(false);
        String link = textField.getText();
        System.out.println(textField.getText());
        if(link.equals("")) link = "http://localhost:8080/";
        try {
            URL url = new URL(link);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            System.out.println(http);
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String inputLine = in.readLine();
            if(inputLine.equals("Connected")){
                mainCtrl.playSound("success");
                if(!(link.substring(link.length() - 1)).equals("/")){
                    link+="/";
                }
                mainCtrl.setLink(link);
                mainCtrl.showSplash();
            }
        }
        catch(MalformedURLException u){
            cantConnectLabel.setText("Can not connect");
            cantConnectLabel.setVisible(true);
            mainCtrl.playSound("error");
        } catch(ConnectException e){
            cantConnectLabel.setText("Server not started");
            cantConnectLabel.setVisible(true);
            mainCtrl.playSound("error");
        } catch(IOException e) {
            e.printStackTrace();
            mainCtrl.playSound("error");
        }
    }

    /**
     * Is changing the size of window to the preferred one
     * @param w - width
     * @param h - height
     */
    @FXML
    public void setWindowSize(double w, double h){
        mainWindow.setPrefSize(w,h);
    }
}
