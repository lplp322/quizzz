package client.utils;

import client.scenes.GameCtrl;
import client.scenes.MainCtrl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import commons.LeaderboardEntryCommons;
import commons.TrimmedGame;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class GameUtils{
    private static MainCtrl mainCtrl;
    private static GameCtrl gameCtrl;

    /**
     * Initialize the static util class
     * @param mainCtrl the main control
     * @param gameCtrl the game control
     */
    public static void init(MainCtrl mainCtrl, GameCtrl gameCtrl) {
        GameUtils.mainCtrl = mainCtrl;
        GameUtils.gameCtrl = gameCtrl;
    }


    /**
     * Sends the joker to all other players (notification)
     * @param joker The joker to send
     */
    public static void sendJoker(String joker) {
        try {
            URL url = new URL( mainCtrl.getLink()+ "reaction/" + mainCtrl.getCurrentID()
                    + "/" + mainCtrl.getName() + "/" + joker);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("PUT");
            mainCtrl.httpToJSONString(http);
            http.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the leaderboard for multiplayer
     * @param myScore your score
     * @param trimmedGame the trimmedGame
     * @throws IOException if the link is not valid
     */
    public static void getMultiplayerLeaderboard(int myScore, TrimmedGame trimmedGame) throws IOException {
        URL url = new URL(mainCtrl.getLink()  +  mainCtrl.getCurrentID() + "/getMultiplayerLeaderBoard" );
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        Gson g = new Gson();
        String jsonString = mainCtrl.httpToJSONString(http);
        Type typeToken = new TypeToken<LinkedList<LeaderboardEntryCommons>>(){}.getType();
        LinkedList<commons.LeaderboardEntryCommons> leaderboardList = g.fromJson(jsonString, typeToken);
        http.disconnect();
        LeaderboardEntryCommons userEntry = new LeaderboardEntryCommons(mainCtrl.getName(), myScore);
        System.out.println(trimmedGame.getRound().getRound());
        mainCtrl.showLeaderboard(leaderboardList, userEntry, trimmedGame.getRound().getRound());
//        return leaderboardList;
    }

    /**
     * sends the number of points that should be added on top of the user's current points
     * effectively doubling the points they receive for this question
     * @param newPoints the new points
     * @param currentTrimmedGame the current trimmed game
     * @throws IOException if the url is invalid
     * @return The response from the server
     */
    public static String sendDoublePoints(int newPoints, TrimmedGame currentTrimmedGame) throws IOException {
        URL url = new URL(mainCtrl.getLink() + mainCtrl.getCurrentID() + "/" +
                mainCtrl.getName() + "/updateScore/" +
                currentTrimmedGame.getRound().getRound() + "/" + newPoints);

        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        String jsonString = mainCtrl.httpToJSONString(http);
        System.out.println("this is the json String " + jsonString);
        System.out.println("this is the response code " + http.getResponseCode());
        http.disconnect();

        return jsonString;
    }

    /**
     * Exits the game - sets the current player as disconnected and if the game is a multiplayer one,
     * the other players see that this play has disconnected
     */
    public static void exitGame() {
        mainCtrl.playSound("success");
        try {
            URL url = new URL(mainCtrl.getLink() + "multiplayer/disconnect/" + mainCtrl.getCurrentID() +
                    "/" + mainCtrl.getName());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("DELETE");
            mainCtrl.httpToJSONString(http);

            http.disconnect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        mainCtrl.getSounds().get("music").stop();
        mainCtrl.resetSound(mainCtrl.getSounds().get("music"));
        mainCtrl.showSplash();
    }

    /**
     * @return the list of entries in the leaderboard from the server
     * @throws IOException if the link is not valid
     */
    public static LinkedList<commons.LeaderboardEntryCommons> getLeaderboard() throws IOException {
        URL url = new URL(mainCtrl.getLink() + "leaderboard" );
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        Gson g = new Gson();
        String jsonString = mainCtrl.httpToJSONString(http);
        Type typeToken = new TypeToken<LinkedList<commons.LeaderboardEntryCommons>>(){}.getType();
        LinkedList<commons.LeaderboardEntryCommons> leaderboardList = g.fromJson(jsonString, typeToken);
        http.disconnect();
        return leaderboardList;
    }

    /**
     * Sends the halftime joker
     */
    public static void sendHalfJoker() {
        sendJoker("Half-Time");
        mainCtrl.playSound("success");
        try {
            URL url = new URL(mainCtrl.getLink() + mainCtrl.getCurrentID() + "/" + mainCtrl.getName() + "/joker/"
                    + "HALF");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("PUT");

            MainCtrl.httpToJSONString(http);

            http.disconnect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param answer is a string related to which answer the user has chosen.
     * @param currentTrimmedGame the current trimmed game
     * @throws IOException
     * @return the response from the server
     */
    public static String sendAnswer(String answer, TrimmedGame currentTrimmedGame) throws IOException {
        mainCtrl.playSound("success");
        URL url = new URL(mainCtrl.getLink() + mainCtrl.getCurrentID() + "/"
                + mainCtrl.getName() + "/checkAnswer/" +
                currentTrimmedGame.getRound().getRound()  + "/" + answer);

        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("PUT");

        String response = mainCtrl.httpToJSONString(http);
        http.disconnect();
        return response;
    }

    /**
     * Sends the emoji to server
     * @param emoji the emoji to send
     */
    public static void sendEmoji(String emoji) {
        try {
            URL url = new URL( mainCtrl.getLink()+ "reaction/" + mainCtrl.getCurrentID()
                    + "/" + mainCtrl.getName() + "/" + emoji);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("PUT");
            mainCtrl.httpToJSONString(http);
            http.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Poll for game info
     * @return trimmedGame
     */
    public static TrimmedGame pollGame() {
        try {
            URL url = new URL(mainCtrl.getLink() + mainCtrl.getCurrentID()
                    +"/" + mainCtrl.getName() + "/getGameInfo");
            //for now all gameID's are set to 1,
            //but these need to be changed once the gameID is stored from the sever
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            Gson g = new Gson();
            String jsonString = mainCtrl.httpToJSONString(http);
            TrimmedGame trimmedGame = g.fromJson(jsonString, TrimmedGame.class);
            http.disconnect();
            return trimmedGame;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}