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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
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
import java.net.MalformedURLException;
import java.util.ArrayList;
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
    private Label correctAns;

    private MainCtrl mainCtrl;
    private static int lastRoundAnswered = -1;
    private Button userChoice;
    private boolean stopGame;
    private boolean inTimeOut;
    private TrimmedGame currentTrimmedGame;
    private int myScore;
    private int newPoints = 0;
    private int reactionSize = 0;

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
        GameUtils.init(this.mainCtrl, this);
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
        this.correctAns.setVisible(false);
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
        this.correctAns.setVisible(false);
    }

    /**
     * This method is used to make the jokers invisible in singplayer games
     */
    public void disableJokers() {
        this.guaranteeButton.setVisible(false);
        this.eliminateWrongButton.setVisible(false);
        this.doublePointsJokerButton.setVisible(false);
        this.halfTimeJokerButton.setVisible(false);
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
                haveYouVoted.setVisible(false);
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
                            this.disableJokers();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
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
    }

    /**
     * @param answer is a string related to which answer the user has chosen.
     * @throws IOException
     */
    public void sendAnswer(String answer) throws IOException {
        String response = GameUtils.sendAnswer(answer, currentTrimmedGame);
        haveYouVoted.setVisible(true);
        if (findScore(response) > myScore) {
            this.newPoints = findScore(response) - myScore;
            this.myScore = findScore(response);
        }
        else {
            this.newPoints = 0;
        }
        printAnswerCorrectness(response);
    }

    /**
     * Sends the halftime joker
     */
    public void sendHalfJoker() {
        GameUtils.sendHalfJoker();
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
        if (choiceA.getText().equals(answer)) {
            return this.choiceA;
        }
        if (choiceB.getText().equals(answer)) {
            return this.choiceB;
        }
        return this.choiceC;
    }

    /**
     * @param correctAnswer the string of the correct answer
     * @param questType
     */
    public void showCorrectAnswer(String correctAnswer, int questType) {
        if(questType == 0){
            correctAns.setText("Correct answer: "+ correctAnswer+" Wh");
            correctAns.setVisible(true);
        }
        else {
            Button correctButton = this.findCorrectChoice(correctAnswer);
            correctButton.setStyle("-fx-background-color: #16b211");
        }
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
        stopGame = true;
        GameUtils.exitGame();
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
        for(String[] pair : reactions) {
            Label lb = new Label();
            lb.setPrefWidth(190);
            lb.setPrefHeight(50);
            lb.setAlignment(Pos.CENTER_LEFT);
            lb.setContentDisplay(ContentDisplay.RIGHT);
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
        this.mainCtrl.showLeaderboard(GameUtils.getLeaderboard(), myEntry);
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
        this.currentTrimmedGame.getQuestion().getAnswers().contains(userChoice.getText())){
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
        GameUtils.sendJoker("Double-Points");
        mainCtrl.playSound("success");
        if (userChoice == null) {
            return;
        }
        if (this.currentTrimmedGame.getQuestion().getAnswers().contains(userChoice.getText()) ||
                (this.currentTrimmedGame.getQuestion().getType() == 0 &&
                        this.currentTrimmedGame.getRound().getRound() == lastRoundAnswered)){

            this.doublePointsJokerButton.setVisible(false);
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
        GameUtils.sendJoker("Eliminated-Wrong-Answer");
        mainCtrl.playSound("success");
        if (this.currentTrimmedGame.getQuestion().getType() != 0 &&
                (this.userChoice == null ||
                        this.currentTrimmedGame.getQuestion().getAnswers().contains(userChoice.getText()))) {
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