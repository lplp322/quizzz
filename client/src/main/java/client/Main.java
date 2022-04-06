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
package client;

import static com.google.inject.Guice.createInjector;


import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.MainCtrl;
import client.scenes.GameCtrl;
import client.scenes.PromptCtrl;
import client.scenes.ChooseServerCtrl;
import client.scenes.LobbyCtrl;
import client.scenes.SplashCtrl;
import client.scenes.LeaderboardCtrl;
import client.scenes.ActivityViewerCtrl;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;



public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }


    /**
     * JavaFX's method to start the JavaFX application.
     * @param primaryStage The stage for the application.
     * @throws IOException if the fxml files cannot be found.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        var splash = FXML.load(SplashCtrl.class, "client","scenes", "Splash.fxml");
        var game = FXML.load(GameCtrl.class, "client", "scenes",
                "Game.fxml");
        var prompt = FXML.load(PromptCtrl.class, "client", "scenes", "Prompt.fxml");

        var leaderboard = FXML.load(LeaderboardCtrl.class, "client", "scenes", "Leaderboard.fxml");

        var chooseServer = FXML.load(ChooseServerCtrl.class, "client", "scenes", "ChooseServer.fxml");

        var activityViewer = FXML.load(ActivityViewerCtrl.class, "client", "scenes", "AdminMenu.fxml");

        var lobby = FXML.load(LobbyCtrl.class, "client", "scenes", "Lobby.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        mainCtrl.initialize(primaryStage, splash, game, prompt, leaderboard, chooseServer, lobby, activityViewer);
        // TODO Auto-generated method stub
        //Initialising path of the media file, replace this with your file path
    }


}