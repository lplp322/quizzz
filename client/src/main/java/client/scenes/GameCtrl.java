package client.scenes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import commons.HalveTimeJoker;
import commons.Player;
import commons.LeaderboardEntry;
import commons.TrimmedGame;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class GameCtrl {

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
    private AnchorPane playerList;

    @FXML
    private TextField guessText;

    @FXML
    private Button submitButton;

    @FXML
    private Label currentRoundLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Label answerLabel;

    @FXML
    private Text haveYouVoted;

    @FXML
    private ComboBox<ImageView> reactions;

    @FXML
    private VBox reactionBox;

    @FXML
    private Label scoreLabel;

    @FXML
    private Button guaranteeButton;

    @FXML
    private ImageView questionImage;

    @FXML
    private Label typeLabel;

    @FXML
    private ScrollPane scrollPane;

    private MainCtrl mainCtrl;

    private static int lastRoundAnswered = -1;

    private Button userChoice;

    private boolean stopGame;
    private boolean inTimeOut;

    private commons.TrimmedGame currentTrimmedGame;

    private int myScore;

    private int newPoints = 0;

    private int reactionSize = 0;

//    public MostPowerCtrl(MainCtrl mainCtrl) {
//        this.mainCtrl = mainCtrl;
//        this.threeChoicesEnable();
//    }

    /**
     * Injecting mainCtrl
     * @param mainCtrl
     */
    @Inject
    public GameCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Resets the game
     */
    public void init() {
        stopGame = false;
        lastRoundAnswered = -1;
        this.resetColors();
        inTimeOut = false;
    }

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
     * Poll for game info
     * @return trimmedGame
     */
    public TrimmedGame pollGame() {
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

    /**
     *
     * @param trimmedGame
     */
    public void displayScreen(TrimmedGame trimmedGame) throws IOException{
        int timeForCurrentPlayer = trimmedGame.getRound().getTimer();

        if(trimmedGame.getRound().getHalveTimeJoker() != null) {
            HalveTimeJoker hf = trimmedGame.getRound().getHalveTimeJoker();

            String playerName = hf.getUsersName().getName();
            if(!playerName.equals(mainCtrl.getName())) {
                timeForCurrentPlayer = hf.getHalvedTimer();
            }
        }

        if (trimmedGame.getRound().getRound() == -1) {
            sendAnswer("1");
            this.stopGame = true;
            this.showLeaderboard();
        }

        if (trimmedGame.getRound().getTimer() == 20 ||
                trimmedGame.getRound().getTimer() == 19) {
            this.mainCtrl.returnToGame();
            this.showRound(trimmedGame, timeForCurrentPlayer);
        }

        if (timeForCurrentPlayer < 0) {//works for now, BUT NEEDS TO BE CHANGED IN TRIMMEDGAME
            showTimeout(trimmedGame);
            this.showCorrectAnswer(trimmedGame.getQuestion().getAnswer());
            if (trimmedGame.getRound().getTimer() == -4) {
                this.resetColors();
                haveYouVoted.setVisible(false);
            }
            if (trimmedGame.getRound().getTimer() == -2) {
                this.getMultiplayerLeaderboard();
            }
        } else {
            inTimeOut = false;
            updatePolling(trimmedGame, timeForCurrentPlayer);
        }

    }

    /**
     * Displays the name of the players and also those who have left
     * @param trimmedGame
     */
    public void showPlayers(TrimmedGame trimmedGame) {
        playerList.getChildren().clear();

        final double height = 20;
        final double width = playerList.getWidth();
        int cnt = 0;

        for(Player temp : trimmedGame.getPlayers().values()) {
            Label label = new Label(temp.getName());

            label.setPrefWidth(width);
            label.setPrefHeight(height);
            label.setAlignment(Pos.CENTER);

            String color = "white";
            if(temp.isDisconnected()) {
                color = "red";
            }

            label.setStyle(
                    "-fx-text-fill: " + color + ";" +
                            "-fx-font-size: 20;"
            );

            AnchorPane.setLeftAnchor(label, 0.0);
            AnchorPane.setTopAnchor(label, height*cnt++);

            playerList.getChildren().add(label);
            //playerList.getItems().add(temp.getName() + (temp.isDisconnected()?"-Disconnected":""));
        }
    }

    /**
     * Getting game info in a new thread
     */
    public void tickGame() throws IOException {
        //getLeaderboard();
        loadReactions();
        mainCtrl.playSound("music");
        //playerList.getItems().add(this.mainCtrl.getName());
        this.scoreLabel.setText("0");
        Thread t1 = new Thread(()-> {
            while(!stopGame) {
                Platform.runLater(() -> {
                        TrimmedGame trimmedGame = pollGame(); // poll the game
                        this.currentTrimmedGame  = trimmedGame;
                        showPlayers(trimmedGame);

                        try {
                            showReaction(trimmedGame.getReactionHistory()); // display all the reactions
                            displayScreen(trimmedGame); // show round or timeout
                            //displayJokers(trimmedGame.getPlayers().get(mainCtrl.getName()).getJokerList());
                        }
                        catch (IOException e) {
                            e.printStackTrace();

                        }
                }
                );
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.setDaemon(true); // stops thread after main thread stops
        t1.start();
    }

    /**
     * display available jokers
     * @param jokers list of jokers
     */
    public void displayJokers(List<String> jokers) {
        for(String x : jokers) {
            System.out.println(x);
        }
    }

    /**
     * Loads the available emoji's (in resources/reactions) into the dropdown menu
     */
    //CHECKSTYLE:OFF
    public void loadReactions() {
        File folder = new File("client/src/main/resources/reactions");
        System.out.println(folder);
        // for the gradle run task
        if (folder.listFiles() == null) {folder = new File("src/main/resources/reactions");}
        List<ImageView> ls = new ArrayList<>();

        ImageView img = new ImageView(new Image("reactions/701.png"));
        img.setFitHeight(30);
        img.setFitWidth(30);
        reactions.setValue(img);
        for(File f : folder.listFiles()) {
            ls.add(new ImageView(new Image("reactions/"+f.getName())));
        }
        reactions.setItems(FXCollections.observableArrayList(ls));
        reactions.setCellFactory(param -> new ListCell<>() {
            private void send(String emoji) {
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
            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty)
                    setGraphic(null);
                else {
                    item.setFitHeight(40);
                    item.setFitWidth(40);
                    HBox hBox = new HBox(item);
                    String sep = "/";
                    setOnMousePressed(event -> {
                        send(item.getImage().getUrl().split(Pattern.quote(sep))[item.getImage().getUrl().
                                split(Pattern.quote(sep)).length-1]);
                    });
                    setGraphic(hBox);
                }
                setText("");
            }
        });
        reactions.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(ImageView item, boolean empty){
                super.updateItem(item, empty);
                if(item != null) {
                    setGraphic(img);
                    setText("");
                }
            }
        });
    }
    //CHECKSTYLE:ON

    /**
     * Showing the timeout
     * @param trimmedGame
     */
    public void showTimeout(TrimmedGame trimmedGame) {
        timerLabel.setText("Timeout");
        currentRoundLabel.setText("Round is over");
        questionLabel.setText(trimmedGame.getQuestion().getQuestion());
        answerLabel.setVisible(true);

        inTimeOut = true;

        this.scoreLabel.setText(String.valueOf(myScore));

        if(trimmedGame.getRound().getRound()  >lastRoundAnswered) answerLabel.setText("You have not answered");
    }


    /**
     * Showing the round screen
     * @param trimmedGame
     * @param realTimer
     */
    private void showRound(TrimmedGame trimmedGame, int realTimer) {
        answerLabel.setVisible(false);

        currentRoundLabel.setText("Current Round " + trimmedGame.getRound().getRound() + 1);
        timerLabel.setText("Time: " + realTimer);
        questionLabel.setText(trimmedGame.getQuestion().getQuestion());
        try {
            questionImage.setImage(new Image(trimmedGame.getQuestion().getUrl().substring(26)));
        } catch (IllegalArgumentException e) {
            questionImage.setImage(new Image(new File(trimmedGame.getQuestion().getUrl()).toURI().toString()));
        }

        switch (trimmedGame.getQuestion().getType()) {
            case 0:
            case 1:
                typeLabel.setText("How much energy does it take?");
                break;
            case 2:
                typeLabel.setText("Instead of ..., you could do instead ...");
                break;
        }

        if (trimmedGame.getQuestion().getType() == 1 || trimmedGame.getQuestion().getType() == 2) {
            this.threeChoicesEnable();
            if(trimmedGame.getQuestion().getAnswers().size() == 3) {
                choiceA.setText(trimmedGame.getQuestion().getAnswers().get(0));
                choiceB.setText(trimmedGame.getQuestion().getAnswers().get(1));
                choiceC.setText(trimmedGame.getQuestion().getAnswers().get(2));
            }
        } else this.guessEnable();
    }


    private void updatePolling(TrimmedGame trimmedGame, int realTimer) {
        answerLabel.setVisible(false);
        currentRoundLabel.setText("Current Round " + (trimmedGame.getRound().getRound()+1));
        timerLabel.setText("Time: " + realTimer);
        questionLabel.setText(trimmedGame.getQuestion().getQuestion());

//        if (trimmedGame.getQuestionType() == 1 || trimmedGame.getQuestionType() == 2) {
//            this.threeChoicesEnable();
//            if(trimmedGame.getPossibleAnswers().size() == 3) {
//                choiceA.setText(trimmedGame.getPossibleAnswers().get(0));
//                choiceB.setText(trimmedGame.getPossibleAnswers().get(1));
//                choiceC.setText(trimmedGame.getPossibleAnswers().get(2));
//            }
//        } else this.guessEnable();
    }

    /*
    public  void jokerMessage(String joker) throws IOException {
        URL url = new URL(mainCtrl.getLink() + this.mainCtrl.getCurrentID()
                + "/" + this.mainCtrl.getName() + "/joker/" + currentTrimmedGame.getRound().getRound()  + "/" + joker);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        String response = mainCtrl.httpToJSONString(http);
        http.disconnect();
    }*/

    /**
     * @param answer is a string related to which answer the user has chosen.
     * @throws IOException
     */
    public void sendAnswer(String answer) throws IOException {
        mainCtrl.playSound("success");
        URL url = new URL(mainCtrl.getLink() + this.mainCtrl.getCurrentID() + "/"
                + this.mainCtrl.getName() + "/checkAnswer/" +
                currentTrimmedGame.getRound().getRound()  + "/" + answer);

        //System.out.println("answer is being sent");
        //System.out.println(this.mainCtrl.getName());
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("PUT");
        //System.out.println(http.getResponseCode());

        String response = mainCtrl.httpToJSONString(http);
        //System.out.println(response);
        http.disconnect();
        haveYouVoted.setVisible(true);

        //System.out.println(response);

        if (findScore(response) > myScore) {
            System.out.println(findScore(response));
            System.out.println(myScore);
            this.newPoints = findScore(response) - myScore;
            this.myScore = findScore(response);
            System.out.println(newPoints);
        }

        else {
            this.newPoints = 0;
        }
        printAnswerCorrectness(response);


//        this.scoreLabel.setText(String.valueOf(myScore));
    }

    /**
     * Sends the halftime joker
     */
    public void sendHalfJoker() {
        mainCtrl.playSound("success");
        try {
            URL url = new URL(mainCtrl.getLink() + mainCtrl.getCurrentID() + "/" + this.mainCtrl.getName() + "/joker/"
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
     * @throws IOException
     */
    public void choiceASend () throws IOException {
        mainCtrl.playSound("success");
        if (this.checkCanAnswer()) {
            this.sendAnswer("0");

            lastRoundAnswered = this.currentTrimmedGame.getRound().getRound();
            this.userChoice = choiceA;
            this.showYourAnswer();
        }
    }

    /**
     * @throws IOException
     */
    public void choiceBSend() throws IOException {
        mainCtrl.playSound("success");
        if (this.checkCanAnswer()) {
            this.sendAnswer("1");
            lastRoundAnswered = this.currentTrimmedGame.getRound().getRound();
            this.userChoice = choiceB;
            this.showYourAnswer();
        }
    }

    /**
     * @throws IOException
     */
    public void choiceCSend() throws IOException {
        mainCtrl.playSound("success");
        if (this.checkCanAnswer()) {
            this.sendAnswer("2");
            lastRoundAnswered = this.currentTrimmedGame.getRound().getRound();
            this.userChoice = choiceC;
            this.showYourAnswer();
        }
    }


    /**
     * @return the list of entries in the leaderboard from the server
     * @throws IOException if the link is not valid
     */
    public LinkedList<commons.LeaderboardEntry> getLeaderboard() throws IOException {
        URL url = new URL(mainCtrl.getLink() + "leaderboard" );
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        Gson g = new Gson();
        String jsonString = mainCtrl.httpToJSONString(http);
        Type typeToken = new TypeToken<LinkedList<commons.LeaderboardEntry>>(){}.getType();
        //System.out.println(typeToken.getTypeName());
        LinkedList<commons.LeaderboardEntry> leaderboardList = g.fromJson(jsonString, typeToken);
        http.disconnect();
        //System.out.println(leaderboardList);
        return leaderboardList;
    }

    /**
     * @return returns true if the user can still answer this question
     */
    public boolean checkCanAnswer() {
        if (this.currentTrimmedGame.getRound().getRound() > lastRoundAnswered && !inTimeOut) {
            return true;
        }
        return false;
    }


    /**
     * @param answer the string of the answer
     * @return the button that currently contains the correct answer
     */
    public Button findCorrectChoice(String answer) {
        //I know this is not a very good way of solving this problem but it works
//        System.out.println("This is the correct answer " + this.currentTrimmedGame.getCorrectAnswer() + "!");
//        System.out.println("choice a " + choiceA.getText()+ "!");
//        System.out.println("choice b " + choiceB.getText()+ "!");
//        System.out.println("choice c " + choiceC.getText()+ "!");

        if (choiceA.getText().equals(answer)) {
            return this.choiceA;
        }
        if (choiceB.getText().equals(answer)) {
            return this.choiceB;
        }

        return this.choiceC;
    }

    /**
     * @param answers the list of possible answers that should be shown to the user
     */
    public  void setPossibleAnswers(List<String> answers) {
        if (answers == null) {
            return;
        }

        if (answers.size() == 0) {
            return;
        }
        this.choiceA.setText(answers.get(0));
        this.choiceB.setText(answers.get(1));
        this.choiceC.setText(answers.get(2));
    }

    /**
     * @param correctAnswer the string of the correct answer
     */
    public void showCorrectAnswer(String correctAnswer) {
        System.out.println(correctAnswer);
        Button correctButton = this.findCorrectChoice(correctAnswer);
        System.out.println(correctButton.getText());
        correctButton.setStyle("-fx-background-color: #16b211");
    }


    /**
     * shows the style of
     */
    public void showYourAnswer() {
        this.userChoice.setStyle("-fx-background-color: #5d96d9");
    }

    /**
     *
     */
    public void resetColors() {
        this.choiceA.setStyle("-fx-background-color: #ffffff");
        this.choiceB.setStyle("-fx-background-color: #ffffff");
        this.choiceC.setStyle("-fx-background-color: #ffffff");
        this.choiceA.setVisible(true);
        this.choiceB.setVisible(true);
        this.choiceC.setVisible(true);
    }

    /**
     * Exits the game - sets the current player as disconnected and if the game is a multiplayer one,
     * the other players see that this play has disconnected
     */
    public void exitGame() {
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


        stopGame = true;
        this.mainCtrl.showSplash();
    }

    /**
     *
     */
    public void choicesDisappear() {
        this.choiceA.setVisible(false);
        this.choiceB.setVisible(false);
        this.choiceC.setVisible(false);
    }


    /**
     * Changing the label with answer, when response to the answer received
     * @param response - response from server in String format
     */
    public void printAnswerCorrectness(String response) {

        answerLabel.setText("You received " + this.newPoints + " points!");
    }

    /**
     *
     */
    public void submitAnswer() throws IOException {
        if(!(guessText.getText()==null) && this.checkCanAnswer()){
            haveYouVoted.setVisible(true);
            sendAnswer(guessText.getText());
            lastRoundAnswered = currentTrimmedGame.getRound().getRound();
        }
    }

    /**
     * Shows the reactions in the game UI
     * @param reactions The reaction list to show
     * @throws MalformedURLException If cannot find the reactions folder
     */
    public void showReaction(List<String[]> reactions) throws MalformedURLException {
        if(reactionBox.getChildren().size() < reactions.size()) {
            mainCtrl.playSound("msg");
        }
        reactionBox.getChildren().clear();
        if(reactions.size() > this.reactionSize) {
            mainCtrl.playSound("msg");
            this.reactionSize = reactions.size();
        }
        for(String[] pair : reactions) {
            Label lb = new Label();
            lb.setPrefWidth(190);
            lb.setPrefHeight(50);
            lb.setAlignment(Pos.CENTER_LEFT);
            lb.setContentDisplay(ContentDisplay.RIGHT);
            lb.setId("reaction");
            Image img = new Image((GameCtrl.class.getClassLoader().getResource("reactions/"+pair[1]).toString()));
            ImageView imageView = new ImageView(img);
            imageView.setFitHeight(30);
            imageView.setFitWidth(30);
            lb.setGraphic(imageView);
            lb.setText(pair[0]+": ");
            lb.setFont(new Font(18));
            reactionBox.getChildren().add(lb);
        }
    }


    /**
     * Displays the all-time scores and your score from the game
     * @throws IOException
     */
    public void showLeaderboard() throws IOException {
        commons.LeaderboardEntry myEntry = new commons.LeaderboardEntry(this.mainCtrl.getName(), myScore);
        this.mainCtrl.showLeaderboard(this.getLeaderboard(), myEntry);
    }


    /**
     * @param response takes the response of the correctness and points from the server and
     *                 retrieves just the points
     * @return  an integer representing the number of points the player has
     */
        public static int findScore(String response){
            String[] words= response.split("\\s");//splits the string based on whitespace
//using java foreach loop to print elements of string array
            return Integer.parseInt(words[4]);
        }

    /**
     * when the user clicks on users the eliminate wrong answer
     * if their answer is wrong it will send a new request to the server with the correct answer
     * @throws IOException if the url where it sends the answer is invalid
     */
    public void sendCorrectAnswer() throws IOException {
        mainCtrl.playSound("success");
        if (userChoice == null) {
            return;
        }
//            System.out.println("user choice: " +  userChoice.getText());
//            System.out.println("correct answer: " + this.currentTrimmedGame.getCorrectAnswer());
//            System.out.println("question type " + this.currentTrimmedGame.getQuestionType());
        if (!(userChoice.getText().equals(this.currentTrimmedGame.getQuestion().getAnswer())) &&
        this.currentTrimmedGame.getQuestion().getType() != 0 &&
        this.currentTrimmedGame.getQuestion().getAnswers().contains(userChoice.getText())){
            System.out.println("sending right answer...");
            String choice = this.convertAnswerToChoice(currentTrimmedGame.getQuestion().getAnswer());
            this.sendAnswer(choice);
            this.guaranteeButton.setVisible(false);
        }
    }

    /**
     * Converts an answer into an integer that represents the multiple choice options
     * 0 for the first, 1 for the second etc.
     * @param answer the String of the answer
     * @return a string of the number so that it can be easily used in communication
     */
    public String convertAnswerToChoice(String answer) {

        if (choiceA.getText().equals(answer)) {
            return "0";
        }
        if (choiceB.getText().equals(answer)) {
            return "1";
        }

        return "2";
    }

    /**
     * sends the number of points that should be added on top of the user's current points
     * effectively doubling the points they receive for this question
     * @throws IOException if the url is invalid
     */
    public void sendDoublePoints() throws IOException {
        mainCtrl.playSound("success");
        if (userChoice == null) {
            return;
        }

        if (this.currentTrimmedGame.getQuestion().getAnswers().contains(userChoice.getText()) ||
                (this.currentTrimmedGame.getQuestion().getType() == 0 &&
                        this.currentTrimmedGame.getRound().getRound() == lastRoundAnswered)){

            System.out.println("sending extra points");
            this.doublePointsJokerButton.setVisible(false);

            URL url = new URL(mainCtrl.getLink() + this.mainCtrl.getCurrentID() + "/" +
                    this.mainCtrl.getName() + "/updateScore/" +
                    this.currentTrimmedGame.getRound().getRound() + "/" + this.newPoints);

            System.out.println(mainCtrl.getLink() + this.mainCtrl.getCurrentID() + "/" +
                    this.mainCtrl.getName() + "/updateScore/" +
                    this.currentTrimmedGame.getRound().getRound() + "/" + this.newPoints);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();


            this.newPoints = this.newPoints * 2;
            String jsonString = this.mainCtrl.httpToJSONString(http);
            System.out.println("this is the json String " + jsonString);
            System.out.println("this is the response code " + http.getResponseCode());
            this.myScore = Integer.parseInt(jsonString);
            printAnswerCorrectness(null);
            http.disconnect();
        }

    }

    /**
     * Displays the leaderboard for multiplayer
     * @throws IOException if the link is not valid
     */
    public void getMultiplayerLeaderboard() throws IOException {
        //System.out.println("button was clicked");
        URL url = new URL(mainCtrl.getLink()  +  this.mainCtrl.getCurrentID() + "/getMultiplayerLeaderBoard" );
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        Gson g = new Gson();
        String jsonString = this.mainCtrl.httpToJSONString(http);
        Type typeToken = new TypeToken<LinkedList<commons.LeaderboardEntry>>(){}.getType();
        //System.out.println(typeToken.getTypeName());
        LinkedList<commons.LeaderboardEntry> leaderboardList = g.fromJson(jsonString, typeToken);
        http.disconnect();
        //System.out.println(leaderboardList);
        LeaderboardEntry userEntry = new LeaderboardEntry(this.mainCtrl.getName(), this.myScore);
        this.mainCtrl.showLeaderboard(leaderboardList, userEntry);
//        return leaderboardList;
    }


    /**
     * checks if the user has not voted for this question and if not they can remove one of
     * the incorrect answers in multiple choice questions
     */
    public void eliminateWrongAnswer() {
        mainCtrl.playSound("success");
        //System.out.println("checking wrong answer");
        //System.out.println(this.currentTrimmedGame.getQuestionType());
        if (this.currentTrimmedGame.getQuestion().getType() != 0 &&
                (this.userChoice == null ||
                        this.currentTrimmedGame.getQuestion().getAnswers().contains(userChoice.getText()))) {
            System.out.println("wrong answer can be deleted");
            this.eliminateWrongButton.setVisible(false);
            if (!choiceA.getText().equals(this.currentTrimmedGame.getQuestion().getAnswer())) {
                choiceA.setVisible(false);
                return;
            }

            else if (!choiceB.getText().equals(this.currentTrimmedGame.getQuestion().getAnswer())) {
                choiceB.setVisible(false);
            }

            else {
                choiceC.setVisible(false);
            }
        }
    }
}