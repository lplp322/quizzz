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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {
    private int currentGameID;  //the ID of the ongoing game
    private Stage primaryStage;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private GameCtrl gameCtrl;
    private Scene game;

    private SplashCtrl splashCtrl;
    private Scene splash;

    private PromptCtrl promptCtrl;
    private Scene prompt;

    private String name;


    /**
     * Initializes all scenes via pairs of controllers and fxml files
     * @param primaryStage
     * @param overview
     * @param add
     * @param splash
     * @param gameCtrl
     * @param prompt
     */

    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
                           Pair<AddQuoteCtrl, Parent> add, Pair<SplashCtrl, Parent> splash,
                           Pair<GameCtrl, Parent> gameCtrl,
                           Pair<PromptCtrl, Parent> prompt) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.splashCtrl = splash.getKey();
        this.splash = new Scene(splash.getValue());

        this.gameCtrl = gameCtrl.getKey();
        this.game = new Scene(gameCtrl.getValue());

        this.promptCtrl = prompt.getKey();
        this.prompt = new Scene(prompt.getValue());

        showSplash();
        primaryStage.show();
    }

    /**
     * Showing overview
     */
    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    /**
     * Showing add
     */
    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
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
     * Changes the current scene to the questions screen
     */
    public void showGame() {
        primaryStage.setTitle("Quizzz");
        primaryStage.setScene(game);
        gameCtrl.getGameInfo();
    }

    /**
     * Changes the current scene to Prompt.fxml, sets mode to Singleplayer
     */
    public void showSinglePlayerPrompt() {
        Scene currentScene = primaryStage.getScene();   //Gets current scene
        primaryStage.setTitle("Enter your name");
        promptCtrl.setSingleplayer();

        //Resizes new scene by calling the setWindowSize method
        promptCtrl.setWindowSize(currentScene.getWidth(),currentScene.getHeight());
        primaryStage.setScene(prompt);
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
}