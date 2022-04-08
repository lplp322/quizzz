package client.scenes;

import client.utils.GameUtils;
import com.google.inject.Inject;
import commons.HalveTimeJoker;
import commons.Player;
import commons.TrimmedGame;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GameCtrl {
    @FXML
    private AnchorPane mainWindow;

    @FXML
    private Label choiceA;

    @FXML
    private Label questionLabel;

    @FXML
    private Label choiceB;

    @FXML
    private Label choiceC;

    @FXML
    private ImageView halfTimeJokerButton;

    @FXML
    private ImageView doublePointsJokerButton;

    @FXML
    private ImageView eliminateWrongButton;

    @FXML
    private AnchorPane playerList;

    @FXML
    private Slider guessText;

    @FXML
    private ImageView imageA;

    @FXML
    private ImageView imageB;

    @FXML
    private ImageView imageC;

    @FXML
    private ImageView submitButton;

    @FXML
    private Label currentRoundLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Label answerLabel;

    @FXML
    private GridPane choiceOne;

    @FXML
    private GridPane choiceTwo;

    @FXML
    private GridPane choiceThree;

    @FXML
    private ComboBox<ImageView> reactions;

    @FXML
    private VBox reactionBox;

    @FXML
    private Label scoreLabel;

    //@FXML
    //private Button guaranteeButton;

    @FXML
    private Label sliderValue;

    @FXML
    private ImageView questionImage;

    @FXML
    private Label typeLabel;

    @FXML
    private ScrollPane scrollPane;

    //@FXML
    //private Label correctAns;

    @FXML
    private GridPane gridPane;

    private MainCtrl mainCtrl;
    private static int lastRoundAnswered = -1;

    private GridPane userChoice;
    private Label userChoiceLabel;

    private boolean stopGame;
    private boolean inTimeOut;
    private TrimmedGame currentTrimmedGame;
    private int myScore;
    private int newPoints = 0;
    private int reactionSize = 0;

    private int sliderRound = -1 ;

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
        setJokers(true);
        //gridPane.setPrefSize(mainCtrl.getWidth(), mainCtrl.getHeight());
        //System.out.printf(mainCtrl.WIDTH + " " + mainCtrl.HEIGHT);
        stopGame = false;
        lastRoundAnswered = -1;
        this.resetColors();
        inTimeOut = false;
        GameUtils.init(this.mainCtrl, this);
    }

    /**
     * This method changes the FXML so that only the appropriate buttons/text is visible
     * for the 2 types of questions where the user chooses one of 3 choices
     */
    public void threeChoicesEnable() {
        this.guessText.setVisible(false);
        this.submitButton.setVisible(false);
        this.sliderValue.setVisible(false);
        this.choiceOne.setVisible(true);
        this.choiceTwo.setVisible(true);
        this.choiceThree.setVisible(true);
        //this.correctAns.setVisible(false);
    }

    /**
     * This method changes the FXML so that the user only sees the appropriate information for
     * the guessing type of question
     * @param answer
     * @param round
     */
    public void guessEnable(String answer, int round) {
        //set up the slider min max values
        System.out.println(answer);
        if(round>sliderRound) {
            double rand1 = Math.random();
            double rand2 = Math.random() + 1;
            int ans = Integer.parseInt(answer);
            guessText.setMin(rand1 * 0.3 * ans);
            guessText.setMax(rand2 * 4 * ans);
            guessText.setValue(rand1 * 0.3 * ans);
            sliderRound = round;
        }
        this.choiceOne.setVisible(false);
        this.choiceTwo.setVisible(false);
        this.sliderValue.setVisible(true);
        this.choiceThree.setVisible(false);
        this.guessText.setVisible(true);
        this.submitButton.setVisible(true);
        //this.correctAns.setVisible(false);

    }

    /**
     * This method is used to make the jokers invisible in singplayer games
     * @param value whether to set to false or true
     */
    public void setJokers(boolean value) {
        //this.guaranteeButton.setVisible(false);
        this.eliminateWrongButton.setVisible(value);
        this.doublePointsJokerButton.setVisible(value);
        this.halfTimeJokerButton.setVisible(value);
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
            this.showCorrectAnswer(trimmedGame.getQuestion().getAnswer(), trimmedGame.getQuestion().getType());
            if (trimmedGame.getRound().getTimer() == -4) {
                this.resetColors();
                //haveYouVoted.setVisible(false);
            }
            if (trimmedGame.getRound().getTimer() == -2 && !this.mainCtrl.isSingleplayerFlag()) {
                GameUtils.getMultiplayerLeaderboard(myScore);
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

            String color = "black";
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
        }
    }

    /**
     * Getting game info in a new thread
     */
    public void tickGame() {
        loadReactions();
        mainCtrl.playSound("music");
        this.scoreLabel.setText("0");
        Thread t1 = new Thread(()-> {
            while(!stopGame) {
                Platform.runLater(() -> {
                        TrimmedGame trimmedGame = GameUtils.pollGame(); // poll the game
                        this.currentTrimmedGame  = trimmedGame;
                        showPlayers(trimmedGame);

                        try {
                            showReaction(trimmedGame.getReactionHistory()); // display all the reactions
                            displayScreen(trimmedGame);
                            if (this.mainCtrl.isSingleplayerFlag()) {
                                this.setJokers(false);
                            }
                            // show round or timeout
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
     * Loads the available emoji's (in resources/reactions) into the dropdown menu
     */
    public void loadReactions() {
        File folder = new File("client/src/main/resources/reactions");
        //System.out.println(folder);
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
        reactions.setStyle("-fx-background-color: #0033cc");
        reactions.setCellFactory(param -> new ListCell<>() {
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
                        GameUtils.sendEmoji(item.getImage().getUrl().split(Pattern.quote(sep))[item.getImage().getUrl().
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

        if(trimmedGame.getRound().getRound() > lastRoundAnswered) answerLabel.setText("You have not answered");
    }

    /**
     * Showing the round screen
     * @param trimmedGame
     * @param realTimer
     */
    private void showRound(TrimmedGame trimmedGame, int realTimer) {
        answerLabel.setVisible(false);
        sliderValue.setStyle("-fx-text-fill: white");
        currentRoundLabel.setText("Current Round " + trimmedGame.getRound().getRound() + 1);
        timerLabel.setText("Time: " + realTimer);
        questionLabel.setText(trimmedGame.getQuestion().getQuestion());
        try {
            questionImage.setImage(new Image(trimmedGame.getQuestion().getUrl().get(0).substring(26)));
        } catch (IllegalArgumentException e) {
            questionImage.setImage(new Image(new File(trimmedGame.getQuestion().getUrl().get(0)).toURI().toString()));
        }
        switch (trimmedGame.getQuestion().getType()) {
            case 0:
            case 1:
                typeLabel.setText("How much energy does it take?");
                break;
            case 2:
                typeLabel.setText("Which activity consumes less energy than the given one");

                ImageView[] images = new ImageView[]{imageA, imageB, imageC};

                for(int i = 0; i < 3; i++) {
                    try {
                        images[i].setImage(new Image(trimmedGame.getQuestion().getUrl().get(i+1).substring(26)));
                    } catch (IllegalArgumentException e) {
                        images[i].setImage(new Image(
                                new File(trimmedGame.getQuestion().getUrl().get(i+1)).toURI().toString()));
                    }
                }
                break;
        }
        if (trimmedGame.getQuestion().getType() == 1 || trimmedGame.getQuestion().getType() == 2) {
            this.threeChoicesEnable();
            if(trimmedGame.getQuestion().getAnswers().size() == 3) {
                choiceA.setText(trimmedGame.getQuestion().getAnswers().get(0));
                choiceB.setText(trimmedGame.getQuestion().getAnswers().get(1));
                choiceC.setText(trimmedGame.getQuestion().getAnswers().get(2));
            }
        } else this.guessEnable(trimmedGame.getQuestion().getAnswer(), trimmedGame.getRound().getRound());
    }

    private void updatePolling(TrimmedGame trimmedGame, int realTimer) {
        answerLabel.setVisible(false);
        currentRoundLabel.setText("Current Round " + (trimmedGame.getRound().getRound()+1));
        timerLabel.setText("Time: " + realTimer);
        questionLabel.setText(trimmedGame.getQuestion().getQuestion());
    }

    /**
     * @param answer is a string related to which answer the user has chosen.
     * @throws IOException
     */
    public void sendAnswer(String answer) throws IOException {
        mainCtrl.playSound("success");
        URL url = new URL(mainCtrl.getLink() + this.mainCtrl.getCurrentID() + "/"
                + this.mainCtrl.getName() + "/checkAnswer/" +
                currentTrimmedGame.getRound().getRound()  + "/" + answer);
        answerLabel.setText("You have voted");
        //System.out.printf("dasdasdas");
        //System.out.println("answer is being sent");
        //System.out.println(this.mainCtrl.getName());
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("PUT");
        //System.out.println(http.getResponseCode());

        String response = mainCtrl.httpToJSONString(http);
        //System.out.println(response);
        http.disconnect();
        //haveYouVoted.setVisible(true);

        //System.out.println(response);

        if (findScore(response) > myScore) {
            this.newPoints = findScore(response) - myScore;
            this.myScore = findScore(response);
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

        try {
            URL url = new URL(mainCtrl.getLink() + mainCtrl.getCurrentID() + "/" + this.mainCtrl.getName() + "/joker/"
                    + "HALF");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            sendJoker("Half-Time");
            mainCtrl.playSound("success");
            halfTimeJokerButton.setVisible(false);

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
            this.userChoice = choiceOne;
            this.userChoiceLabel = choiceA;
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
            this.userChoice = choiceTwo;
            this.userChoiceLabel = choiceB;
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
            this.userChoice = choiceThree;
            this.userChoiceLabel = choiceC;
            this.showYourAnswer();
        }
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
    public GridPane findCorrectChoice(String answer) {
        //I know this is not a very good way of solving this problem but it works
//        System.out.println("This is the correct answer " + this.currentTrimmedGame.getCorrectAnswer() + "!");
//        System.out.println("choice a " + choiceA.getText()+ "!");
//        System.out.println("choice b " + choiceB.getText()+ "!");
//        System.out.println("choice c " + choiceC.getText()+ "!");

        if (choiceA.getText().equals(answer)) {
            return this.choiceOne;
        }
        if (choiceB.getText().equals(answer)) {
            return this.choiceTwo;
        }

        return this.choiceThree;
    }

    /**
     * @param correctAnswer the string of the correct answer
     * @param questType
     */
    public void showCorrectAnswer(String correctAnswer, int questType) {
        //System.out.println(correctAnswer);
        if(questType == 0){
            sliderValue.setText("Correct answer: "+ correctAnswer+" Wh");
            guessText.setValue(Double.parseDouble(correctAnswer));
            //correctAns.setVisible(true);
        }
        else {
            GridPane correctButton = this.findCorrectChoice(correctAnswer);
            //System.out.println(correctButton.getText());
            correctButton.setStyle("-fx-background-color: #16b211");
        }
    }

    /**
     * updates the value of the slider
     */
    @FXML
    public void updateValue() {
        if(lastRoundAnswered < currentTrimmedGame.getRound().getRound())
            sliderValue.setText(""+(int)Math.ceil(guessText.getValue()));
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
        this.choiceOne.setStyle("-fx-background-color: #0033cc");
        this.choiceTwo.setStyle("-fx-background-color: #0033cc");
        this.choiceThree.setStyle("-fx-background-color: #0033cc");
        this.choiceOne.setVisible(true);
        this.choiceTwo.setVisible(true);
        this.choiceThree.setVisible(true);
    }

    /**
     * Exits the game - sets the current player as disconnected and if the game is a multiplayer one,
     * the other players see that this play has disconnected
     */
    public void exitGame() {
        stopGame = true;
        GameUtils.exitGame();
    }

    /**
     *
     */
    public void choicesDisappear() {
        this.choiceOne.setVisible(false);
        this.choiceTwo.setVisible(false);
        this.choiceThree.setVisible(false);
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
        if(this.checkCanAnswer()){
            sliderValue.setStyle("-fx-text-fill: purple");
            sendAnswer((int)Math.ceil(guessText.getValue())+"");
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
        for(String[] pair : reactions) {
            Label lb = new Label();
            lb.setPrefWidth(190);
            lb.setPrefHeight(50);
            lb.setAlignment(Pos.CENTER_LEFT);
            lb.setContentDisplay(ContentDisplay.RIGHT);
            lb.setStyle("-fx-background-color: white");
            lb.setId("reaction");
            try {
                Image img = new Image((GameCtrl.class.getClassLoader().getResource("reactions/" + pair[1])
                        .toString()));ImageView imageView = new ImageView(img);
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                lb.setGraphic(imageView);
                lb.setText(pair[0]+": ");
            } catch (NullPointerException e) {
                lb.setPrefHeight(80);
                lb.setText(pair[0]+ ": " + pair[1].replace("-", " "));
                lb.setWrapText(true);
                lb.setStyle("-fx-background-color: red");
            }
            lb.setFont(new Font(18));
            reactionBox.getChildren().add(lb);
        }
    }

    /**
     * Displays the all-time scores and your score from the game
     * @throws IOException
     */
    public void showLeaderboard() throws IOException {
        commons.LeaderboardEntryCommons myEntry = new commons.LeaderboardEntryCommons(this.mainCtrl.getName(), myScore);
        this.mainCtrl.showLeaderboard(mainCtrl.getLeaderboard(), myEntry);
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
        GameUtils.sendJoker("Guarantee-Correct-Answer");
        mainCtrl.playSound("success");
        if (userChoice == null) {
            return;
        }
        if (
        this.currentTrimmedGame.getQuestion().getType() != 0 &&
        this.currentTrimmedGame.getQuestion().getAnswers().contains(userChoiceLabel.getText())){
            System.out.println("sending right answer...");
            String choice = this.convertAnswerToChoice(currentTrimmedGame.getQuestion().getAnswer());
            this.sendAnswer(choice);
            //this.guaranteeButton.setVisible(false);
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
        if(userChoice == null) return;

        if (this.currentTrimmedGame.getQuestion().getAnswers().contains(userChoiceLabel.getText()) ||
                (this.currentTrimmedGame.getQuestion().getType() == 0 &&
                        this.currentTrimmedGame.getRound().getRound() == lastRoundAnswered)){

            //System.out.println("sending extra points");
            this.doublePointsJokerButton.setVisible(false);

            sendJoker("Double-Points");
            mainCtrl.playSound("success");
            doublePointsJokerButton.setVisible(false);

            URL url = new URL(mainCtrl.getLink() + this.mainCtrl.getCurrentID() + "/" +
                    this.mainCtrl.getName() + "/updateScore/" +
                    this.currentTrimmedGame.getRound().getRound() + "/" + this.newPoints);

            //System.out.println(mainCtrl.getLink() + this.mainCtrl.getCurrentID() + "/" +
            //        this.mainCtrl.getName() + "/updateScore/" +
            //        this.currentTrimmedGame.getRound().getRound() + "/" + this.newPoints);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();


            this.newPoints = this.newPoints * 2;
            String jsonString = GameUtils.sendDoublePoints(newPoints, this.currentTrimmedGame);
            this.myScore = Integer.parseInt(jsonString);
            printAnswerCorrectness(null);
        }
    }

    /**
     * checks if the user has not voted for this question and if not they can remove one of
     * the incorrect answers in multiple choice questions
     */
    public void eliminateWrongAnswer() {

        if (lastRoundAnswered < this.currentTrimmedGame.getRound().getRound() &&
                this.currentTrimmedGame.getQuestion().getType() != 0 &&
                (this.userChoice == null ||
                        this.currentTrimmedGame.getQuestion().getAnswers().contains(userChoiceLabel.getText()))) {
            //System.out.println("wrong answer can be deleted");
            sendJoker("Eliminated-Wrong-Answer");
            mainCtrl.playSound("success");
            this.eliminateWrongButton.setVisible(false);

            if (!choiceA.getText().equals(this.currentTrimmedGame.getQuestion().getAnswer())) {
                choiceOne.setVisible(false);
                return;
            }
            else if (!choiceB.getText().equals(this.currentTrimmedGame.getQuestion().getAnswer())) {
                choiceTwo.setVisible(false);
            }
            else {
                choiceThree.setVisible(false);
            }
        }
    }

    /**
     * Sends the joker to all other players (notification)
     * @param joker The joker to send
     */
    public void sendJoker(String joker) {
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
     * Changes the size of the window;
     * @param w
     * @param h
     */
    @FXML
    public void setWindowSize(double w, double h){
        mainWindow.setPrefSize(w,h);
    }
}