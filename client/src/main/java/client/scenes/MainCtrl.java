/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import commons.LeaderboardEntry;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.Pair;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainCtrl {
    private  String link = "http://localhost:8080/";

    private int currentGameID;  //the ID of the ongoing game
    private Stage primaryStage;

    private GameCtrl gameCtrl;
    private Scene game;

    private SplashCtrl splashCtrl;
    private Scene splash;

    private PromptCtrl promptCtrl;
    private Scene prompt;

    private LeaderboardCtrl leaderboardCtrl;
    private Scene leaderboard;

    private ChooseServerCtrl chooseServerCtrl;
    private Scene chooseServer;

    private ActivityViewerCtrl activityViewerCtrl;
    private Scene activityViewer;

    private LobbyCtrl lobbyCtrl;
    private Scene lobby;

    private String name;
    private boolean singleplayerFlag;

    /**
     * Initializes all scenes via pairs of controllers and fxml files
     * @param primaryStage
     * @param splash
     * @param gameCtrl
     * @param prompt
     * @param leaderboard
     * @param chooseServer
     * @param lobby
     * @param adminMenu
     */

    public void initialize(Stage primaryStage, Pair<SplashCtrl, Parent> splash,
                           Pair<GameCtrl, Parent> gameCtrl,
                           Pair<PromptCtrl, Parent> prompt, Pair<LeaderboardCtrl, Parent> leaderboard,
                           Pair<ChooseServerCtrl, Parent> chooseServer,
                           Pair<LobbyCtrl, Parent> lobby,
                           Pair<ActivityViewerCtrl, Parent> adminMenu) {

        this.primaryStage = primaryStage;

        this.splashCtrl = splash.getKey();
        this.splash = new Scene(splash.getValue());

        this.gameCtrl = gameCtrl.getKey();
        this.game = new Scene(gameCtrl.getValue());
        this.game.getStylesheets().add(getClass().getResource("Game.css").toString());

        this.promptCtrl = prompt.getKey();
        this.prompt = new Scene(prompt.getValue());

        this.leaderboardCtrl = leaderboard.getKey();
        this.leaderboard = new Scene(leaderboard.getValue());
        this.leaderboard.getStylesheets().add(getClass().getResource("LeaderboardStyle.css").toString());

        this.lobbyCtrl = lobby.getKey();
        this.lobby = new Scene(lobby.getValue());

        this.activityViewerCtrl = adminMenu.getKey();
        this.activityViewer = new Scene(adminMenu.getValue());

        this.chooseServerCtrl = chooseServer.getKey();
        this.chooseServer = new Scene(chooseServer.getValue());

        //showSplash();
        showChooseServer();
        primaryStage.show();

        this.checkForClosingApplication();
    }

    /**
     * Changes the current scene to the splash screen, resizes scene windows is already open
     */
    public void showSplash() {
        if(primaryStage.getScene()!=null){
            Scene currentScene = primaryStage.getScene();   //Gets current scene
            splashCtrl.setWindowSize(currentScene.getWidth(),currentScene.getHeight());
        }
        primaryStage.setTitle("Quizzz");
        primaryStage.setScene(splash);
    }

    /**
     * Shows a waiting room before game begins
     */
    public void showWaitingRoom() {
        primaryStage.setTitle("Waiting room");
        lobbyCtrl.init();
        primaryStage.setScene(lobby);
    }

    /**
     * Changes the current scene to the questions screen
     */
    public void showGame() throws IOException {
        primaryStage.setTitle("Quizzz");
        primaryStage.setScene(game);
        gameCtrl.init();
        gameCtrl.tickGame();
    }

    /**
     * Shows the leaderboard
     * @param results
     * @param myResult
     */
    public void showLeaderboard(List<LeaderboardEntry> results, LeaderboardEntry myResult) {
        /*if(primaryStage.getScene() != null) {
            Scene currentScene = primaryStage.getScene();
            leaderboardCtrl.setWindowSize(currentScene.getWidth(), currentScene.getHeight());
        }*/
        /*primaryStage.setTitle("Leaderboard");
        primaryStage.setScene(leaderboard);
        LeaderboardEntry ll = new LeaderboardEntry("Energy Master29", 150);
        LeaderboardEntry ll2 = new LeaderboardEntry("MLGenergyUsage", 100);
        LeaderboardEntry ll3 = new LeaderboardEntry("You", 50);
        LeaderboardEntry ll4 = new LeaderboardEntry("Me", 3);
        leaderboardCtrl.displayResults(List.of(ll, ll2, ll3, ll4, ll4,
        ll4, ll4, ll4, ll4, ll4, ll4, ll4, ll4, ll4, ll4, ll4, ll4, ll4, ll4, ll4, ll4),
         ll3);
        */
        primaryStage.setScene(this.leaderboard);
        leaderboardCtrl.displayResults(results, myResult);
    }

    /**
     * Starts the single player Prompt.fxml
     */
    public void showSinglePlayerPrompt() {
        promptCtrl.setSingleplayer();
        showPrompt();

    }

    /**
     * Starts the multiplayer Prompt.fxml
     */
    public void showMultiPlayer(){
        promptCtrl.setMultiplayer();
        showPrompt();
    }

    /**
     * Starts the Prompt.fxml with needed size
     */
    public void showPrompt(){
        Scene currentScene = primaryStage.getScene();   //Gets current scene
        primaryStage.setTitle("Enter your name");
        //Resizes new scene by calling the setWindowSize method
        //System.out.println(currentScene.getWidth() + " " +currentScene.getHeight());
        promptCtrl.setWindowSize(currentScene.getWidth(),currentScene.getHeight());
        primaryStage.setScene(prompt);
    }

    /**
     * Starts ChooseServer.fxml
     */
    public void showChooseServer(){
        if(primaryStage.getScene()!=null){
            Scene currentScene = primaryStage.getScene();   //Gets current scene
            chooseServerCtrl.setWindowSize(currentScene.getWidth(),currentScene.getHeight());
        }
        primaryStage.setTitle("Choose server");
        primaryStage.setScene(chooseServer);
    }
    /**
     * Changes current scene to the activity viewer
     */
    public void showActivityViewer() {
        Scene currentScene = primaryStage.getScene();   //Gets current scene
        primaryStage.setTitle("ActivityViewer");

        //Resizes new scene by calling the setWindowSize method
        activityViewerCtrl.setWindowSize(currentScene.getWidth(),currentScene.getHeight());
        primaryStage.setScene(activityViewer);
    }
    /**
     * @param http this is a http connection that the response of which will be turned into a string
     * @return The http response in JSON format
     */
    public static String httpToJSONString(HttpURLConnection http) {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (http.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String jsonString = textBuilder.toString();
        return jsonString;
    }

    /**
     * A getter for the current gameID
     * @return gameID
     */
    public int getCurrentID(){
        return this.currentGameID;
    }

    /**
     * Sets the ID of the ongoing game
     * @param ID - number received from server
     */
    public void setCurrentGameID(int ID){
        this.currentGameID = ID;
    }

    /**
     * @param name is the name of the player that they have inputted.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name of the player
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the current link
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets link
     * @param link - server link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * returns the user to the game screen (used after showing the leaderboard)
     */
    public void returnToGame() {
        primaryStage.setTitle("Quizzz");
        primaryStage.setScene(game);
    }

    /**
     * @return a boolean indicating if the game is singleplayer or not
     */
    public Boolean isSingleplayerFlag() {
        return this.singleplayerFlag;
    }

    /**
     * @param value assigns a value (boolean to the flag indicating if
     *              the game is single player or no t
     */
    public void setSingleplayerFlag(Boolean value) {
        this.singleplayerFlag = value;
    }


    /**
     * Method checks if the user is closing the application, if this is the case
     * it creates an alert that makes them confirm this is the case or cancel
     */
    public void checkForClosingApplication() {
        primaryStage.setOnCloseRequest(evt -> {
            // allow user to decide between yes and no
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Do you really want to close this application?", ButtonType.YES, ButtonType.NO);

            // clicking X also means no
            ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

            if (ButtonType.NO.equals(result)) {
                // consume event i.e. ignore close request
                evt.consume();
            }
        });
    }



}