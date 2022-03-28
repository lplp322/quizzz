package client.scenes;

import com.google.gson.Gson;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LobbyCtrl {
    private MainCtrl mainCtrl;
    private final int pollingTime = 200;

    @FXML
    private Label lobbyLabel;

    @FXML
    private AnchorPane scrollPanel;

    @FXML
    private Button startGame;

    /**
     * Injecting mainCtrl
     * @param mainCtrl
     */
    @Inject
    public LobbyCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    private List<String> prevNames;
    private boolean stopPolling;

    /**
     * Determines whether the game we are in has started
     * @return true or false depending on whether we have started
     */
    public boolean hasGameStarted() {
        try {
            URL url = new URL(mainCtrl.getLink() + "multiplayer/getLobbyId/");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            int id = Integer.parseInt(mainCtrl.httpToJSONString(http));
            return id != mainCtrl.getCurrentID();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Starts polling for lobby info
     */
    public void init() {
        stopPolling = false;
        prevNames = null;
        Thread t = new Thread(() -> {
            while(!stopPolling) {
                Platform.runLater(() -> {
                    try {
                        URL url = new URL(mainCtrl.getLink() + "pollLobby");
                        HttpURLConnection http = (HttpURLConnection) url.openConnection();
                        http.setRequestMethod("GET");

                        Gson g = new Gson();
                        String jsonString = mainCtrl.httpToJSONString(http);
                        List<String> names = g.fromJson(jsonString, List.class);

                        http.disconnect();

                        if(!names.equals(prevNames)) {
                            displayNames(names);
                            prevNames = names;
                        }

                        if(hasGameStarted()) {
                            stopPolling = true;
                            mainCtrl.showGame();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                });
                try {
                    Thread.sleep(pollingTime);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    /**
     * Displays the list of names on the screen
     * @param names list of names
     */
    public void displayNames(List<String> names) {
        lobbyLabel.setText(String.format("%d player" +
                (names.size() > 1? "s":"") +
                " currently waiting in the room", names.size()));
        lobbyLabel.setWrapText(true);
        scrollPanel.getChildren().clear();

        final int nameHeight = 25;
        final int nameWidth = 150;
        final int perRow = 12;

        String[] colors = new String[]{"#ffffff", "#000000"};
        String[] fontColors = new String[]{"#000000", "#ffffff"};

        for(int i = 0; i < names.size(); i++) {
            String name = names.get(i);

            Label tmp = new Label(name);

            int y = i % perRow;
            int x = i / perRow;

            tmp.setPrefWidth(nameWidth);
            tmp.setPrefHeight(nameHeight);
            tmp.setWrapText(true);
            tmp.setStyle("-fx-border-color: black;" +
                    "-fx-background-color: " + colors[(x+y)%2] + ";" +
                    "-fx-text-fill: " + fontColors[(x+y)%2] + ";" +
                    "-fx-font-size: 20");


            AnchorPane.setTopAnchor(tmp, 1.0*nameHeight*y);
            AnchorPane.setLeftAnchor(tmp, 1.0*x*nameWidth);

            scrollPanel.getChildren().add(tmp);
        }
        scrollPanel.setPrefSize(Math.ceil(1.00*names.size() / perRow) * nameWidth, scrollPanel.getHeight());
    }

    /**
     * Exits the lobby and removes the player from it
     */
    public void exitLobby() {
        try {
            URL url = new URL(mainCtrl.getLink() + "multiplayer/delete/" + mainCtrl.getName());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("DELETE");
            mainCtrl.httpToJSONString(http);

            http.disconnect();

            stopPolling = true;
            mainCtrl.showSplash();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the game
     */
    public void startGame() {
        try {
            URL url = new URL(mainCtrl.getLink() + "startGame/");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("PUT");
            mainCtrl.httpToJSONString(http);

            http.disconnect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
