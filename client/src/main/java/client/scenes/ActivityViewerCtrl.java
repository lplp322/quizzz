package client.scenes;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import commons.CommonsActivity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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

    @FXML
    private Button submitButton;

    @FXML
    private TextField descriptionText;

    @FXML
    private TextField usageText;

    @FXML
    private TextField idText;

    @FXML
    private TextField sourceText;

    @FXML
    private TextField pathText;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;


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
        URL activityReceive = new URL(mainCtrl.getLink()+"admin/activities");
//        System.out.println(mainCtrl.getLink()+"admin/activities");
        HttpURLConnection http = (HttpURLConnection) activityReceive.openConnection();
        String jsonActivites = MainCtrl.httpToJSONString(http);
//        System.out.println(jsonActivites);
        Gson g = new Gson();
        Type ActivityList = new TypeToken<ArrayList<CommonsActivity>>(){}.getType();    //fixes type-erasure
        this.activities  = g.fromJson(jsonActivites, ActivityList);
//        System.out.println(this.activities.get(0));

        TableColumn<CommonsActivity, String> titleCol = new TableColumn<CommonsActivity, String>("Title");
        TableColumn<CommonsActivity, Integer> usageCol = new TableColumn<CommonsActivity, Integer>("Consumption");
        TableColumn<CommonsActivity, Long> idCol = new TableColumn<CommonsActivity, Long>("id");
        TableColumn<CommonsActivity, String> sourceCol = new TableColumn<CommonsActivity, String>("source");
        TableColumn<CommonsActivity, String> imagePathCol = new TableColumn<CommonsActivity, String>("imagePath");


        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        usageCol.setCellValueFactory(new PropertyValueFactory<>("consumption"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        sourceCol.setCellValueFactory(new PropertyValueFactory<>("source"));
        imagePathCol.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        ObservableList<CommonsActivity> data = FXCollections.observableArrayList(activities);
        table.setItems(data);


        table.getColumns().setAll(idCol,titleCol,usageCol,sourceCol, imagePathCol);

        //BufferedReader in = new BufferedReader(new InputStreamReader(updateTable.getInputStream()));
        //String inputLine = in.readLine();
    }


    /**
     * @throws IOException if the url is for the communication is invalid
     */
    public void submitActivity() throws IOException {
        //System.out.println(this.descriptionText.getText());
        //System.out.println(this.usageText.getText());

//        URL url = new URL(mainCtrl.getLink() + "admin/new_activity/"+this.descriptionText.getText()
//        + "/" + this.usageText.getText());

        URL url = new URL(mainCtrl.getLink() + "admin/new_activity/"
                + this.combineString(this.descriptionText.getText()) + "/"
                 + this.usageText.getText() + "/"
                + this.modifyPath(this.sourceText.getText())  + "/"
                + this.modifyPath(this.pathText.getText()));

//        System.out.println(mainCtrl.getLink() + "admin/new_activity/"
//                + this.descriptionText.getText() + "/"
//                + this.usageText.getText() + "/"
//                + this.sourceText.getText() + "/"
//                + this.pathText.getText());

//        System.out.println(mainCtrl.getLink() + "admin/new_activity/"+this.descriptionText.getText()
//                + "/" + this.usageText.getText());
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        Gson g = new Gson();
        http.setRequestMethod("PUT");
        String jsonString = mainCtrl.httpToJSONString(http);
        //System.out.println(http.getResponseCode());
//        System.out.println(jsonString);
        http.disconnect();
        this.clearTexts();
        this.updateEntries();

    }

    /**
     *this methods enables the autofilling of the activity that you click on into the textfields
     * making it easier to edit them
     */
    public void init() {
    ObservableList<CommonsActivity> observableList =  table.getSelectionModel().getSelectedItems();
//        System.out.println(observableList.get(0));
        if (observableList.size() > 0) {
            CommonsActivity activity = observableList.get(0);
            this.idText.setText(String.valueOf(activity.getId()));
            this.descriptionText.setText(activity.getTitle());
            this.usageText.setText(String.valueOf(activity.getConsumption()));
            this.sourceText.setText(activity.getSource());
            this.pathText.setText(activity.getImagePath());
        }

    }

    /**
     * @param input String that you want to combine into 1
     * @return a string that is combined with ! instead of spaces.
     */
    public String combineString(String input) {
        String[] list = input.split(" ");
        String output = list[0];

        for (int i = 1; i < list.length; i ++) {
            output = output + "!" + list[i];
        }

        return output;
    }

    /**
     * Delete an activity with the given id in the db
     */
    public void deleteActivity() throws IOException {
        URL url = new URL(mainCtrl.getLink() + "admin/delete_activity/" + this.idText.getText());
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("DELETE");
        mainCtrl.httpToJSONString(http);
        http.disconnect();
//        this.clearTexts();
        this.updateEntries();
    }

    /**
     * @throws IOException if the url is invalid
     */
    public void editActivity() throws IOException {
        this.deleteActivity();
        this.submitActivity();
        this.clearTexts();
        this.updateEntries();
    }




//    public void editActivity() throws IOException {
//        System.out.println(this.descriptionText.getText());
//        System.out.println(this.usageText.getText());
//
//        URL url = new URL(mainCtrl.getLink() + "admin/edt_activity/"+ this.idText.getText()
//        + "/" +this.descriptionText.getText()
//                + "/" + this.usageText.getText());
//
//        HttpURLConnection http = (HttpURLConnection) url.openConnection();
//        Gson g = new Gson();
////        String jsonString = mainCtrl.httpToJSONString(http);
//        System.out.println(http.getResponseCode());
////        System.out.println(jsonString);
//    }


    /**
     * clears all of the textfields to avoid confusion once activities have been changed / updated
     */
    public void clearTexts() {
        this.idText.setText("");
        this.descriptionText.setText("");
        this.usageText.setText("");
        this.sourceText.setText("");
        this.pathText.setText("");
    }

    /**
     * replaces all of the "/"s in a path so that it can be transfered in a url
     * @param input the string that you want to edit
     * @return a string with ~s instead of /s
     */
    public String modifyPath(String input) {
        String output =  input.replace("/", "~");
        return output;
    }


}
