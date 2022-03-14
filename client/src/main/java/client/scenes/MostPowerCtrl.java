package client.scenes;

import com.google.gson.Gson;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;

import java.net.HttpURLConnection;


import java.net.URL;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MostPowerCtrl {
//    private final MainCtrl mainCtrl;
private final MainCtrl mainCtrl;

    @Inject
    public MostPowerCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    @FXML
    private Button choiceA;

    @FXML
    private Label questionLabel;



    @FXML
    private Button choiceB;

    @FXML
    private Button choiceC;

    @FXML
    private Button halfTimeJokerButton;

    @FXML
    private Button doublePointsJokerButton;

    @FXML
    private Button eliminateWrongButton;

    @FXML
    private Button quitGameButton;

    @FXML
    private ListView playerList;

    @FXML
    private TextField guessText;

    @FXML
    private Button submitButton;

    @FXML
    private Label currentRoundLabel;

    @FXML
    private Label timerLabel;




//    public MostPowerCtrl(MainCtrl mainCtrl) {
//        this.mainCtrl = mainCtrl;
//        this.threeChoicesEnable();
//    }


    /**
     * This method changes the FXML so that only the appropriate buttons/text is visible
     * for the 2 types of questions where the user chooses one of 3 choices
     */
    public void threeChoicesEnable() {
        this.choiceA.setVisible(true);
        this.choiceB.setVisible(true);
        this.choiceC.setVisible(true);
        this.guessText.setVisible(false);
        this.submitButton.setVisible(false);

    }

    /**
     * This method changes the FXML so that the user only sees the appropriate information for
     * the guessing type of question
     */
    public void guessEnable() {
        this.choiceA.setVisible(false);
        this.choiceB.setVisible(false);
        this.choiceC.setVisible(false);
        this.guessText.setVisible(true);
        this.submitButton.setVisible(true);

    }


    /**
     * This is the method that will continously poll the server in order to update game information
     * @throws IOException
     */
    public void getGameInfo() throws IOException {

        boolean gameFinished = false;

        while (gameFinished!= true) {
//            URL url = new URL("http://localhost:8080/1/getGameInfo");
            URL url = new URL("http://localhost:8080/" + mainCtrl.getCurrentID() + "/getGameInfo");
            //for now all gameID's are set to 1 but these need to be changed once the gameID is stored from the sever
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            Gson g = new Gson();
            String jsonString = httpToJSONString(http);
            commons.TrimmedGame trimmedGame = g.fromJson( jsonString, commons.TrimmedGame.class);
            currentRoundLabel.setText("currentRound" + trimmedGame.getRoundsLeft());
            timerLabel.setText("Time: " + trimmedGame.getTimer());
            questionLabel.setText(trimmedGame.getCurrentQuestion());

            if (trimmedGame.getQuestionType() == 1) {
                this.threeChoicesEnable();
            }

            else if (trimmedGame.getQuestionType() == 2) {
                this.guessEnable();
            }

            if (trimmedGame.getRoundsLeft() == 0) {
                gameFinished = true;
            }
            gameFinished = true; //this is hardcoded to make the code stop
            http.disconnect();
        }

    }


    /**
     * @param http this is a http connection that the response of which will be turned into a string
     * @return
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
     * @param joker this is a string related to which joker is being passed to the server
     * @throws IOException
     */
    public static void jokerMessage(String joker) throws IOException {
        URL url = new URL("http://localhost:8080/1/getGameInfo"); //THIS IS NOT THE CORRECT URL, IT IS JUST A PLACE HOLDER
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("PUT");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/json");

        String data = "{\n  \"Joker\": \"Halve time\"\n}";
        //need to fix this to take as input a joker, but I can't figure out the string manipulation

        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);

        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        http.disconnect();

    }

    /**
     * @param correct this is a boolean that relates if the question was answered correctly 
     * @throws IOException
     */
    public static void correctMessage(boolean correct) throws IOException {
        URL url = new URL("http://localhost:8080/1/correctness/" + correct);
        //for now all gameID's are set to 1 but these need to be changed once the gameID is stored from the sever
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.disconnect();
    }





}





