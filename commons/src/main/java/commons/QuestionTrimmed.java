package commons;

import java.util.List;

public class QuestionTrimmed {
    private String question;
    private List<String> answers;
    private int type;
    private String answer;

    /**
     * 
     * @param question
     * @param answers
     * @param type
     * @param answer
     */
    public QuestionTrimmed(String question, List<String> answers, int type, String answer) {
        this.question = question;
        this.answers = answers;
        this.type = type;
        this.answer = answer;
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
}
