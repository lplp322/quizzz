package client.scenes;

import com.google.gson.Gson;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

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
    private AnchorPane screen;

    @FXML
    private AnchorPane scrollPanel;

    @FXML
    private Button startGame;

    @FXML
    private GridPane gridP;

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
        System.out.printf(mainCtrl.WIDTH + " " + mainCtrl.HEIGHT);
        gridP.setPrefSize(mainCtrl.WIDTH, mainCtrl.HEIGHT);
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
                            displayNamesFibonacci(names);
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
    /*public void displayNames(List<String> names) {
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
    }*/

    /**
     * displays the names in an interesting pattern
     * @param names
     */
    //CHECKSTYLE:OFF
    public void displayNamesFibonacci(List<String> names) {
        lobbyLabel.setText(String.format("%d player" +
                (names.size() > 1? "s":"") +
                " currently waiting in the room", names.size()));
        lobbyLabel.setWrapText(true);
        scrollPanel.getChildren().clear();

        double height = scrollPanel.getHeight();
        double width = scrollPanel.getWidth();
        double y = 0, x = 0;

        double ratio = 0.15; // ratio between the current element to the next element
        int rotationStep = 0; // there are 4 steps that repeat in creating the sequence

        double[] startColor = new double[]{255, 0, 0};
        double[] endColor = new double[]{0, 0, 255};
        double[] colorDiff = new double[] { // how much we have to change to reach the second color
                endColor[0] - startColor[0],
                endColor[1] - startColor[1],
                endColor[2] - startColor[2]
        };

        for(int i = 0; i < names.size(); i++) {
            Label tempLabel = new Label(names.get(i));

            //tempLabel.setWrapText(true);

            String hex = String.format("#%02x%02x%02x",
                    (int)Math.ceil(startColor[0] + (colorDiff[0] * (i) / names.size())),
                    (int)Math.ceil(startColor[1] + (colorDiff[1] * (i) / names.size())),
                    (int)Math.ceil(startColor[2] + (colorDiff[2] * (i) / names.size())));

            tempLabel.setAlignment(Pos.CENTER);
            tempLabel.setStyle("-fx-border-color: black;" +
                    "-fx-background-color: " + hex + ";" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: " + Math.min(height, width)/20);

            if(i == names.size() - 1) { // last one
                tempLabel.setPrefHeight(height);
                tempLabel.setPrefWidth(width);

                AnchorPane.setTopAnchor(tempLabel, y);
                AnchorPane.setLeftAnchor(tempLabel, x);
            }
            else {
                double tempHeight = (ratio * height) / (ratio + 1);
                double tempWidth = (ratio * width) / (ratio + 1);

                switch (rotationStep) {
                    case 0:
                        tempLabel.setPrefHeight(height);
                        tempLabel.setPrefWidth(tempWidth);

                        AnchorPane.setTopAnchor(tempLabel, y);
                        AnchorPane.setLeftAnchor(tempLabel, x);

                        x += tempWidth;
                        width -= tempWidth;

                        break;
                    case 1:
                        tempLabel.setPrefHeight(tempHeight);
                        tempLabel.setPrefWidth(width);

                        AnchorPane.setTopAnchor(tempLabel, y);
                        AnchorPane.setLeftAnchor(tempLabel, x);

                        y += tempHeight;
                        height -= tempHeight;

                        break;
                    case 2:
                        tempLabel.setPrefHeight(height);
                        tempLabel.setPrefWidth(tempWidth);

                        AnchorPane.setTopAnchor(tempLabel, y);
                        AnchorPane.setLeftAnchor(tempLabel, x + (width - tempWidth));

                        width -= tempWidth;

                        break;
                    case 3:
                        tempLabel.setPrefHeight(tempHeight);
                        tempLabel.setPrefWidth(width);

                        AnchorPane.setTopAnchor(tempLabel, y + (height - tempHeight));
                        AnchorPane.setLeftAnchor(tempLabel, x);

                        height -= tempHeight;

                        break;
                }
                rotationStep = (rotationStep + 1) % 4;
            }
            scrollPanel.getChildren().add(tempLabel);
        }
    }
    //CHECKSTYLE:ON

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
