package commons;

import java.util.List;
import java.util.Objects;

public class TrimmedGame {
    private int id;
    private String currentQuestion;
    private int roundNum;
    private int timer;
    private int questionType;
    private List<String> possibleAnswers;
    private String correctAnswer;
    private List<String[]> reactionHistory;

    /**
     * @param id Id of the game
     * @param currentQuestion the current question shown to the user
     * @param roundNum
     * @param timer
     * @param answers
     * @param questionType
     * @param correctAnswer the correct answer so this can be displayed to the user
     * @param reactionHistory
     */
    public TrimmedGame(int id, String currentQuestion, int roundNum, int timer, List<String> answers,
                       int questionType, String correctAnswer, List<String[]> reactionHistory) {
        this.id = id;
        this.currentQuestion = currentQuestion;
        this.roundNum = roundNum;
        this.timer = timer;
        this.possibleAnswers = answers;
        this.questionType = questionType;
        this.correctAnswer = correctAnswer;
        this.reactionHistory = reactionHistory;
    }

    /**
     * Getter for the game id
     * @return the game id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the current question
     * @return the current question
     */
    public String getCurrentQuestion() {
        return currentQuestion;
    }

    /**
     * Getter for the round number
     * @return the round number
     */
    public int getRoundNum() {
        return roundNum;
    }

    /**
     * Getter for the time left
     * @return the time left
     */
    public int getTimer() {
        return timer;
    }

    /**
     * Getter for the question type
     * @return the question type
     */
    public int getQuestionType() {
        return questionType;
    }

    /**
     * Getter for the possible answers
     * @return the list of possible answers
     */
    public List<String> getPossibleAnswers() {
        return possibleAnswers;
    }

    /**
     * Return the reaction history of this trimmed Game
     * @return the list of the reaction history
     */
    public List<String[]> getReactionHistory() {
        return this.reactionHistory;
    }

    /**
     * Equals method for TrimmedGame
     * @param o the object to compare with
     * @return true iff the other object is equal to this
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrimmedGame that = (TrimmedGame) o;
        return id == that.id && roundNum == that.roundNum && timer == that.timer &&
                Objects.equals(currentQuestion, that.currentQuestion) &&
                Objects.equals(possibleAnswers, that.possibleAnswers) &&
                Objects.equals(reactionHistory, that.reactionHistory);
    }

    /**
     * @return the string of the correct answer
     */
    public String getCorrectAnswer() {
        return this.correctAnswer;
    }



}
