package server;

import commons.Player;
import commons.Round;
import commons.LeaderboardEntry;
import commons.TrimmedGame;
import server.database.ActivityRepository;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.Collections;


public class Game implements Runnable{
    private Map<String, Player> players;
    private int lobbyId;
    private List<Question> questions;
    private int gameType;
    private Round round;
    private List<String[]> reactions;

    /**
     * @param players map of players that are in the game
     * @param lobbyId the ID of the lobby that they were in before
     * @param gameType the type of game that is being played (single or multiplayer)
     * @param dtBase the database for the activities
     * @param reactions the list of reactions
     */
    public Game(Map<String, Player> players, int lobbyId, int gameType,
                ActivityRepository dtBase, List<String[]> reactions) {
        this.players = players;
        this.lobbyId = lobbyId;
        this.gameType = gameType;
        round = new Round();
        this.reactions = reactions;

        questions = new ArrayList<>();
        for(int i = 0; i < round.getTotalRounds(); i++) {
            Question tempQuestion = new Question(dtBase);
            questions.add(tempQuestion);
            System.out.println(tempQuestion.getQuestion());
        }
//
//        for (int i =0; i < players.size(); i ++) {
//            this.playerScore.put(players.get(i).getName(), 0);
//        }
    }

    /**
     * sets a player as disconnected
     * @param name
     */
    public void disconnectPlayer(String name) {
        players.get(name).setDisconnected(true);
    }

    /**
     * method which handles simulating the game rounds
     */
    @Override
    public void run() {
        try {
            while(round.getGameStatus() == 1) {
                round.tickDown();
                Thread.sleep(1000);
                //System.out.println(round);
            }
        }
        catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    /**
     * activates a joker
     * @param joker
     * @param playerName
     */
    public void useJoker(String joker, String playerName) {
        if(round.getHalveTimeJoker() == null) {
            if(joker.equals("HALF")) {
                round.activateHalfTime(players.get(playerName));
            }
        }
    }

    /**
     * @return returns the map of names to the players
     */
    public Map<String, Player> getPlayers() {
        return players;
    }

    /**
     * returns lobby-id
     * @return the lobby-id
     */
    public int getLobbyId() {
        return lobbyId;
    }

    /**
     * returns list of all the questions
     * @return the list of all the questions
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * returns the type of the game(multi-player, single-player)
     * @return the type of the game
     */
    public int getGameType() {
        return gameType;
    }

    /**
     * Return the list of all reaction of this game
     * @return a list containing all the reaction in this game
     */
    public List<String[]> getReactions() {
        return this.reactions;
    }

    /**
     * returns the round object
     * @return the round object
     */
    public Round getRound() { return round; }

    /*
    public TrimmedGame trim(){
        Question currQuestion = questions.get(round.getRound());
        String answer = currQuestion.getAnswer();
        return new TrimmedGame(lobbyId, currQuestion.getQuestion(), questions.size(), round.getTimer(),
                currQuestion.getAnswers(), currQuestion.getType(), answer, (List<String[]>) reactions);
    }*/

    /**
     * trims the current object
     * @return the current object as TrimmedGame
     */
    public TrimmedGame getTrimmed() {
        return new TrimmedGame(lobbyId, questions.get(round.getRound()).getTrimmed(), players, round, reactions);
    }


    /**
     * returns the object as a string
     * @return the object as a string
     */
    @Override
    public String toString() {
        return "Game{" +
                "players=" + players +
                ", lobbyId=" + lobbyId +
                ", questions=" + questions +
                ", gameType=" + gameType +
                '}';
    }


    /**
     * Will check for the correctness of the player answer and give him points
     * @param name - name of the player
     * @param round - round number
     * @param answer - String with provided answer
     * @return True if answer was correct
     */
    public boolean checkPlayerAnswer(String name, int round, int answer) {
        //System.out.println("Round: "+ getRound().getRound());
        //System.out.println("Correct answer: "+ getQuestions().get(round).getAnswer());
        if(getRound().getRound() == round){
            Question currQuestion = questions.get(getRound().getRound());
            //System.out.println("Question type " + currQuestion.getType());
            if(currQuestion.getType() == 0){
                //TO BE IMPLEMENTED
                int score = checkPlayerEstimation(name, round, answer+"");
                int adjustedScore = getRound().getTimer() * score;
                updatePlayerScore(name, adjustedScore);
                if(score<40) return false;
                return true;
            }
            else{
                int correctAns = -1;
                for(int i=0; i<currQuestion.getAnswers().size(); i++){
                    if(currQuestion.getAnswers().get(i).equals(currQuestion.getAnswer())) correctAns = i;
                }
                //System.out.println("Correct answer: "+ correctAns);
                if(correctAns == -1) System.out.println("errrroororroror");
                if (correctAns == answer){
                    updatePlayerScore(name, 5*getRound().getTimer());
                    return true;
                }
                return false;
            }
        }
        else{
            System.out.println("False round"+ round);
            return false;
        }
    }

    /**
     * Checks the error of the estimation, and awards points accordingly
     * @param name - name of the player
     * @param round - game round
     * @param estimation - the guess as a string
     * @return  - number of points awarded
     */
    public int checkPlayerEstimation(String name, int round, String estimation) {
        Double answerDouble = Double.parseDouble(getQuestions().get(round).getAnswer());
        Double estimationDouble = Double.parseDouble(estimation);
        Double error = Math.abs(answerDouble-estimationDouble);
        if(error==0)return 10;
        else if(error<=(answerDouble*20)/100)return 8;
        else if(error<=(answerDouble*40)/100)return 6;
        else if(error<=(answerDouble*50)/100)return 4;
        else if(error<=(answerDouble*70)/100)return 2;
        return 0;
    }

    /**
     * @param name name of the player that the score is being updated for
     * @param points The points to be added
     * @return returns the updated score of the player
     */
    public int updatePlayerScore(String name, int points) {
        Player player = this.players.get(name);
        int score = player.getScore();
        score = score + points;
        player.setScore(score);
        return score;
    }

    /**
     * @param name The name of the player that the score is associated with
     * @return the score associated with the given name
     */
    public int getPlayerScore(String name) {
        int score = this.players.get(name).getScore();
        return score;
    }

    /**
     * @return a linked list containing the current leaderboard for multiplayer games
     */
    public LinkedList<LeaderboardEntry> getMultiplayerLeaderboard() {
        Set<Map.Entry<String,Player>> playerMappings = this.players.entrySet();
        LinkedList<LeaderboardEntry> leaderboardEntries = new LinkedList<>();

        for (Map.Entry<String, Player> entry : playerMappings) {
            Player player = entry.getValue();
            commons.LeaderboardEntry ldEntry = new LeaderboardEntry(player.getName(), player.getScore());
            leaderboardEntries.add(ldEntry);
        }

        Collections.sort(leaderboardEntries);

        return leaderboardEntries;
    }
}
