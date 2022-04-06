package commons;

import java.util.List;

public class QuestionTrimmed {
    private String question;
    private List<String> answers;
    private int type;
    private String answer;
    private String url;

    /**
     * 
     * @param question the actual answer for the round
     * @param answers list of possible answers
     * @param type the type of question
     * @param answer the correct answer for the question
     * @param url the url for the image
     */
    public QuestionTrimmed(String question, List<String> answers, int type, String answer, String url) {
        this.question = question;
        this.answers = answers;
        this.type = type;
        this.answer = answer;
        this.url = url;
    }

    /**
     * getter for question
     * @return the question object
     */
    public String getQuestion() {
        return question;
    }

    /**
     * getter for answers
     * @return list of answers
     */
    public List<String> getAnswers() {
        return answers;
    }

    /**
     * getter for type
     * @return the type of the question
     */
    public int getType() {
        return type;
    }

    /**
     * getter for the answer
     * @return the answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * returns the url
     * @return url
     */
    public String getUrl() {
        return url;
    }
}
