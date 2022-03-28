package client.scenes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import commons.LeaderboardEntry;
import commons.TrimmedGame;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    private int currentRound;

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

    private MainCtrl mainCtrl;

    private static int lastRoundAnswered = -1;

    private Button userChoice;

    private boolean stopGame;

    private commons.TrimmedGame currentTrimmedGame;

    private int myScore;

    private int newPoints = 0;

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
//        this.resetColors();
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

    //CHECKSTYLE:OFF
    /**
     * Getting game info in a new thread
     */
    public void getGameInfo() throws IOException {
        //getLeaderboard();
        playerList.getItems().remove(0, playerList.getItems().size());
        loadReactions();
        playerList.getItems().add(this.mainCtrl.getName());

        Thread t1 = new Thread(()-> {

            while(!stopGame) {
                Platform.runLater(() -> {
                            try {
                                URL url = new URL(mainCtrl.getLink() + mainCtrl.getCurrentID()
                                        +"/" + mainCtrl.getName() + "/getGameInfo");
                                //for now all gameID's are set to 1,
                                //but these need to be changed once the gameID is stored from the sever
                                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                                Gson g = new Gson();
                                String jsonString = mainCtrl.httpToJSONString(http);
                                commons.TrimmedGame trimmedGame = g.fromJson(jsonString, commons.TrimmedGame.class);
                                currentRound = trimmedGame.getRoundNum();

                                showReaction(trimmedGame.getReactionHistory());
                                this.currentTrimmedGame  = trimmedGame;
                                System.out.println(trimmedGame.getCorrectAnswer());
//                                System.out.println(currentRound);
                                if (currentRound == -1) {
                                    sendAnswer("1");
                                    this.stopGame = true;
                                    this.showLeaderboard();
                                }

                                if (trimmedGame.getTimer() == 20 ||
                                trimmedGame.getTimer() == 19) {
                                    this.mainCtrl.returnToGame();
                                    this.showRound(trimmedGame);
                                }
                                if (trimmedGame.getTimer() < 0) {//works for now, BUT NEEDS TO BE CHANGED IN TRIMMEDGAME
                                    showTimeout(trimmedGame);
                                    this.showCorrectAnswer(trimmedGame.getCorrectAnswer());
                                    if (trimmedGame.getTimer() == -4) {
                                        this.resetColors();
                                        haveYouVoted.setVisible(false);
                                    }

                                    if (trimmedGame.getTimer() == -2) {
                                        this.getMultiplayerLeaderboard();
                                    }
                                }


                                else {
                                    updatePolling(trimmedGame);
                                }
                                http.disconnect();
                            } catch (IOException e) {
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
        t1.start();
    }


    /**
     * Loads the available emoji's (in reasources/reactions) into the dropdown menu
     */
    public void loadReactions() {
        reactions.setValue(new ImageView());
        File folder = new File("client/src/main/resources/reactions");
        List<ImageView> ls = new ArrayList<>();
        System.out.println(folder.listFiles()[0].toString());
        ImageView img = new ImageView(new Image("reactions/701.png"));
        img.setFitHeight(30);
        img.setFitWidth(30);
        reactions.setValue(img);
//        reactions.setButtonCell(new ListCell<>());
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


    /**
     * Showing the timeout
     * @param trimmedGame
     */
    public void showTimeout(TrimmedGame trimmedGame) {
        timerLabel.setText("Timeout");
        currentRoundLabel.setText("Round is over");
        questionLabel.setText(trimmedGame.getCurrentQuestion());
        answerLabel.setVisible(true);
        this.scoreLabel.setText(String.valueOf(myScore));
        if(currentRound >lastRoundAnswered) answerLabel.setText("You have not answered");
    }


    /**
     * Showing the round screen
     * @param trimmedGame
     */
    private void showRound(TrimmedGame trimmedGame) {
        answerLabel.setVisible(false);
        currentRoundLabel.setText("currentRound " + (trimmedGame.getRoundNum()+1));
        timerLabel.setText("Time: " + trimmedGame.getTimer());
        questionLabel.setText(trimmedGame.getCurrentQuestion());
        
        if (trimmedGame.getQuestionType() == 1 || trimmedGame.getQuestionType() == 2) {
            this.threeChoicesEnable();
            if(trimmedGame.getPossibleAnswers().size() == 3) {
                choiceA.setText(trimmedGame.getPossibleAnswers().get(0));
                choiceB.setText(trimmedGame.getPossibleAnswers().get(1));
                choiceC.setText(trimmedGame.getPossibleAnswers().get(2));
            }
        } else this.guessEnable();
    }


    private void updatePolling(TrimmedGame trimmedGame) {
        answerLabel.setVisible(false);
        currentRoundLabel.setText("currentRound " + (trimmedGame.getRoundNum()+1));
        timerLabel.setText("Time: " + trimmedGame.getTimer());
        questionLabel.setText(trimmedGame.getCurrentQuestion());

//        if (trimmedGame.getQuestionType() == 1 || trimmedGame.getQuestionType() == 2) {
//            this.threeChoicesEnable();
//            if(trimmedGame.getPossibleAnswers().size() == 3) {
//                choiceA.setText(trimmedGame.getPossibleAnswers().get(0));
//                choiceB.setText(trimmedGame.getPossibleAnswers().get(1));
//                choiceC.setText(trimmedGame.getPossibleAnswers().get(2));
//            }
//        } else this.guessEnable();
    }

    /**
     * @param joker this is a string related to which joker is being passed to the server
     * @throws IOException
     */
    public  void jokerMessage(String joker) throws IOException {

//        URL url = new URL("http://localhost:8080/1/P1/checkAnswer/" + currentround + "/" + joker);
        URL url = new URL(mainCtrl.getLink() + this.mainCtrl.getCurrentID()
                + "/" + this.mainCtrl.getName() + "/joker/" + currentRound + "/" + joker);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
//        http.setRequestMethod("PUT");
        //System.out.println(http.getResponseCode());
        String response = mainCtrl.httpToJSONString(http);
        //System.out.println(response);
        http.disconnect();
    }

    /**
     * @param answer is a string related to which answer the user has chosen.
     * @throws IOException
     */
    public void sendAnswer(String answer) throws IOException {
        URL url = new URL(mainCtrl.getLink() + this.mainCtrl.getCurrentID() + "/"
                + this.mainCtrl.getName() + "/checkAnswer/" +
                currentRound + "/" + answer);

        System.out.println("answer is being sent");
        //System.out.println(this.mainCtrl.getName());
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("PUT");
        //System.out.println(http.getResponseCode());

        String response = mainCtrl.httpToJSONString(http);
        //System.out.println(response);
        http.disconnect();
        haveYouVoted.setVisible(true);

        System.out.println(response);

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
     * @throws IOException
     */
    public void choiceASend () throws IOException {

        if (this.checkCanAnswer()) {
            this.sendAnswer("0");

            lastRoundAnswered = this.currentRound;
            this.userChoice = choiceA;
            this.showYourAnswer();
        }
    }

    /**
     * @throws IOException
     */
    public void choiceBSend() throws IOException {
        if (this.checkCanAnswer()) {
            this.sendAnswer("1");
            lastRoundAnswered = this.currentRound;
            this.userChoice = choiceB;
            this.showYourAnswer();
        }
    }

    /**
     * @throws IOException
     */
    public void choiceCSend() throws IOException {
        if (this.checkCanAnswer()) {
            this.sendAnswer("2");
            lastRoundAnswered = this.currentRound;
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
        if (this.currentRound > lastRoundAnswered) {
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
     * Exits the game
     */
    public void exitGame() {
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
            sendAnswer(guessText.getText());
            lastRoundAnswered = currentRound;
        }
    }

    /**
     * Shows the reactions in the game UI
     * @param reactions The reaction list to show
     * @throws MalformedURLException If cannot find the reactions folder
     */
    public void showReaction(List<String[]> reactions) throws MalformedURLException {
        reactionBox.getChildren().remove(0, reactionBox.getChildren().size());
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
        if (userChoice == null) {
            return;
        }
//            System.out.println("user choice: " +  userChoice.getText());
//            System.out.println("correct answer: " + this.currentTrimmedGame.getCorrectAnswer());
//            System.out.println("question type " + this.currentTrimmedGame.getQuestionType());
        if (!(userChoice.getText().equals(this.currentTrimmedGame.getCorrectAnswer())) &&
        this.currentTrimmedGame.getQuestionType() != 0 &&
        this.currentTrimmedGame.getPossibleAnswers().contains(userChoice.getText())){
            System.out.println("sending right answer...");
            String choice = this.convertAnswerToChoice(currentTrimmedGame.getCorrectAnswer());
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

        if (userChoice == null) {
            return;
        }

        if (this.currentTrimmedGame.getPossibleAnswers().contains(userChoice.getText()) ||
                (this.currentTrimmedGame.getQuestionType() == 0 && this.currentRound == lastRoundAnswered)){

            System.out.println("sending extra points");
            this.doublePointsJokerButton.setVisible(false);

            URL url = new URL(mainCtrl.getLink() + this.mainCtrl.getCurrentID() + "/" +
                    this.mainCtrl.getName() + "/updateScore/" +  this.currentRound + "/" + this.newPoints);

            System.out.println(mainCtrl.getLink() + this.mainCtrl.getCurrentID() + "/" +
                    this.mainCtrl.getName() + "/updateScore/" +  this.currentRound + "/" + this.newPoints);
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
     * @return the list of entries in the leaderboard from the server
     * @throws IOException if the link is not valid
     */
    public void getMultiplayerLeaderboard() throws IOException {
        System.out.println("button was clicked");
        URL url = new URL(mainCtrl.getLink()  +  this.mainCtrl.getCurrentID() + "/getMultiplayerLeaderBoard" );
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        Gson g = new Gson();
        String jsonString = this.mainCtrl.httpToJSONString(http);
        Type typeToken = new TypeToken<LinkedList<commons.LeaderboardEntry>>(){}.getType();
        //System.out.println(typeToken.getTypeName());
        LinkedList<commons.LeaderboardEntry> leaderboardList = g.fromJson(jsonString, typeToken);
        http.disconnect();
        System.out.println(leaderboardList);
        LeaderboardEntry userEntry = new LeaderboardEntry(this.mainCtrl.getName(), this.myScore);
        this.mainCtrl.showLeaderboard(leaderboardList, userEntry);
//        return leaderboardList;
    }


    /**
     * checks if the user has not voted for this question and if not they can remove one of
     * the incorrect answers in multiple choice questions
     */
    public void eliminateWrongAnswer() {
        System.out.println("checking wrong answer");
        System.out.println(this.currentTrimmedGame.getQuestionType());
        if (this.currentTrimmedGame.getQuestionType() != 0 &&
                (this.userChoice == null ||
                        this.currentTrimmedGame.getPossibleAnswers().contains(userChoice.getText()))) {
            System.out.println("wrong answer can be deleted");
            this.eliminateWrongButton.setVisible(false);
            if (!choiceA.getText().equals(this.currentTrimmedGame.getCorrectAnswer())) {
                choiceA.setVisible(false);
                return;
            }

            else if (!choiceB.getText().equals(this.currentTrimmedGame.getCorrectAnswer())) {
                choiceB.setVisible(false);
            }

            else {
                choiceC.setVisible(false);
            }
        }
    }


}










