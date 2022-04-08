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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import commons.LeaderboardEntryCommons;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import java.util.LinkedList;
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

    private HashMap<String, MediaPlayer> sounds;

    private double height, width;

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

        this.sounds = new HashMap<>();
        initSounds();

        //showSplash();
        //LeaderboardEntryCommons a1 = new LeaderboardEntryCommons("A", 60);
        //LeaderboardEntryCommons a2 = new LeaderboardEntryCommons("B",  0);
        //LeaderboardEntryCommons a3 = new LeaderboardEntryCommons("C", 20);
        //showLeaderboard(List.of(a1, a2), a2);
        showChooseServer();

        primaryStage.show();
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);

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
        height = primaryStage.getScene().getHeight();
        width = primaryStage.getScene().getWidth();
        primaryStage.setTitle("Quizzz");
        primaryStage.setScene(splash);
    }

    /**
     * Shows a waiting room before game begins
     */
    public void showWaitingRoom() {
        Scene currentScene = primaryStage.getScene();   //Gets current scene

        //Resizes new scene by calling the setWindowSize method
        lobbyCtrl.setWindowSize(currentScene.getWidth(),currentScene.getHeight());
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
    public void showLeaderboard(List<LeaderboardEntryCommons> results, LeaderboardEntryCommons myResult) {
        if(primaryStage.getScene()!=null){
            Scene currentScene = primaryStage.getScene();   //Gets current scene

        }
        leaderboardCtrl.setWindowSize(1920,1080);
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
     * @return the list of entries in the leaderboard from the server
     * @throws IOException if the link is not valid
     */
    public LinkedList<LeaderboardEntryCommons> getLeaderboard() {
        LinkedList<LeaderboardEntryCommons> leaderboardList = new LinkedList<>();
        try {
            URL url = new URL(getLink() + "leaderboard" );
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            Gson g = new Gson();
            String jsonString = httpToJSONString(http);
            Type typeToken = new TypeToken<LinkedList<LeaderboardEntryCommons>>(){}.getType();
            //System.out.println(typeToken.getTypeName());
            leaderboardList = g.fromJson(jsonString, typeToken);
            http.disconnect();
            //System.out.println(leaderboardList);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return leaderboardList;
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
    public static String httpToJSONString(HttpURLConnection http) throws FileNotFoundException {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (http.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        } catch (IOException e) {

        }
        String jsonString = textBuilder.toString();
        return jsonString;
    }

    /**
     * returns the height
     * @return height of the screen
     */
    public double getHeight() {
        return height;
    }

    /**
     * returns the width
     * @return width of the screen
     */
    public double getWidth() {
        return width;
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
     * Initialize sounds
     */
    private void initSounds() {
        File file = new File("client/src/main/resources/sounds");
        if(file.listFiles() == null) {file = new File("src/main/resources/sounds");}
        for(File f : file.listFiles()) {
            MediaPlayer mp = new MediaPlayer(new Media(f.toURI().toString()));
            this.sounds.put(f.getName().split("\\.")[0], mp);
        }
    }

    /**
     * Getter for the SoundBank
     * @return a hashmap containing all the sounds
     */
    public HashMap<String, MediaPlayer> getSounds() {
        return this.sounds;
    }

    /**
     * Resets the sound for multiple uses
     * @param sound the sound to reset
     */
    public void resetSound(MediaPlayer sound) {
        Duration startTime = sound.getStartTime();
        sound.seek(startTime);
    }

    /**
     * Plays the sound and resets it
     * @param sound the filename of the sound
     */
    public void playSound(String sound) {
        MediaPlayer mp = this.sounds.get(sound);
        if(mp == null) {
            System.out.println("Sound file not found.");
            return;
        }
        mp.play();
        resetSound(mp);
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