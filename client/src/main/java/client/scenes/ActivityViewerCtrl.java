package client.scenes;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import commons.CommonsActivity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ActivityViewerCtrl {
    private final MainCtrl mainCtrl;
    private List<CommonsActivity> activities;
    @FXML
    private AnchorPane mainWindow;

    @FXML
    private Button quitButton;

    @FXML
    private TableView table;


    /**
     * Instantiates a Splash Controller
     * @param mainCtrl The Main Controller
     */
    @Inject
    public ActivityViewerCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Changes the size of the AnchorPlane
     * @param w
     * @param h
     */
    public void setWindowSize(double w, double h){
        mainWindow.setPrefSize(w,h);
    }

    /**
     * Shows the splash screen
     */
    public void returnToMenu(){
        this.mainCtrl.showSplash();
    }

    /**
     * Requests activities from server, stores them in a list
     * @throws IOException
     */
    @FXML
    public void updateEntries() throws IOException {
        URL activityReceive = new URL(mainCtrl.getLink()+"admin/testActivity");
        HttpURLConnection http = (HttpURLConnection) activityReceive.openConnection();
        String jsonActivites = MainCtrl.httpToJSONString(http);
        Gson g = new Gson();
        Type ActivityList = new TypeToken<ArrayList<CommonsActivity>>(){}.getType();    //fixes type-erasure
        this.activities  = g.fromJson(jsonActivites, ActivityList);

        //BufferedReader in = new BufferedReader(new InputStreamReader(updateTable.getInputStream()));
        //String inputLine = in.readLine();
    }
}
