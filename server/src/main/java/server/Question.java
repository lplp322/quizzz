package server;

import commons.QuestionTrimmed;
import server.database.ActivityRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;


public class Question {
    // the actual question
    private String question;
    //
    private List<String> answers;
    //each question is one of three types
    private int type;
    //4 random activities from the database
    private List<Activity> activities;
    //the database
    private ActivityRepository dt;
    //Correct answer
    private String answer;
    private List<String> url;

    /**
     * Creates a new question
     * @param dt the ActivityRepository with the activities
     */
    public Question(ActivityRepository dt) {
        this.dt = dt;
        type = (new Random()).nextInt(3);
        List<Activity> allActivities = dt.getAllActivities();
        Collections.shuffle(allActivities);
        activities = new ArrayList<>();
        answers = new ArrayList<>();
        url = new LinkedList<>();

        for(int i = 0; i < 4; i++) {
            activities.add(allActivities.get(i));
        }

        switch (type) {
            case 0:
                generateTypeOne();
                break;
            case 1:
                generateTypeTwo();
                break;
            case 2:
                generateTypeThree();
                break;
        }
    }

    /**
     * Creates a question of a given type
     * @param dt the ActivityRepository with the activities
     * @param type the type of the question
     */
    public Question(ActivityRepository dt, int type) {
        this.dt = dt;
        this.type = type;
        List<Activity> allActivities = dt.getAllActivities();
        Collections.shuffle(allActivities);
        activities = new ArrayList<>();
        answers = new ArrayList<>();
        url = new LinkedList<>();

        for(int i = 0; i < 4; i++) {
            activities.add(allActivities.get(i));
        }

        switch (type) {
            case 0:
                generateTypeOne();
                break;
            case 1:
                generateTypeTwo();
                break;
            case 2:
                generateTypeThree();
                break;
        }
    }

    /** Generates a question in the following format: Open-ended questions (estimating the answer) that are randomised.
     *
     */
    private void generateTypeOne() {
        Activity act = activities.get(0);

        question = act.getTitle();

        url.add(act.getImagePath());

        answers.add(act.getConsumption()+"");
        answer = act.getConsumption()+"";

        Collections.shuffle(answers);
    }

    /** Generates a question in the following format: Comparing the energy usage of three activities.
     *
     */
    private void generateTypeTwo() {
        int wrongAnswer1;
        int wrongAnswer2;
        Random random = new Random();
        int questionIndex = random.nextInt(activities.size());
        question = activities.get(questionIndex).getTitle();

        url.add(activities.get(questionIndex).getImagePath());

        int answerInt = activities.get(questionIndex).getConsumption();

        int deviation = random.nextInt(activities.get(questionIndex).getConsumption());
        boolean sign = random.nextBoolean();

        if(sign)wrongAnswer1 =activities.get(questionIndex).getConsumption()+deviation;
            else wrongAnswer1 =activities.get(questionIndex).getConsumption()-deviation;

        deviation = random.nextInt((activities.get(questionIndex).getConsumption()/2));
        sign = random.nextBoolean();

        if(sign)wrongAnswer2 = activities.get(questionIndex).getConsumption()+deviation;
            else wrongAnswer2 = activities.get(questionIndex).getConsumption()-deviation;

        answer = String.valueOf(activities.get(questionIndex).getConsumption());
        answers.add(answer);
        answers.add(String.valueOf(wrongAnswer1));
        answers.add(String.valueOf(wrongAnswer2));

        Collections.shuffle(answers);
    }

    /** Generates a question in the following format:
     * Comparing the energy usage of one activity to three other activities:
     *
     */
    private void generateTypeThree() {
        Collections.sort(activities);
        Collections.reverse(activities);
        question = activities.get(1).getTitle();

        answer = activities.get(0).getTitle();

        for(int i = 0; i < 4; i++) {
            if(i!=1) {
                answers.add(activities.get(i).getTitle()+"");
                url.add(activities.get(i).getImagePath());
            }
        }
        List<Integer> toShuffle = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            toShuffle.add(i);
        }
        Collections.shuffle(toShuffle);
        List<String> newUrls = List.copyOf(url);
        List<String> newAnswers = List.copyOf(answers);

        answers.clear();
        url.clear();
        url.add(activities.get(1).getImagePath());

        for(int i = 0; i < 3; i++) {
            answers.add(newAnswers.get(toShuffle.get(i)));
            url.add(newUrls.get(toShuffle.get(i)));
        }
    }

    /**
     * returns the answer
     * @return the answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * returns the question
     * @return the queston
     */
    public String getQuestion() {
        return question;
    }

    /**
     * returns all the possible answers
     * @return a list of all the possible answers
     */
    public List<String> getAnswers() {
        return answers;
    }

    /**
     * returns the type of the question(0, 1, 2)
     * @return the type of the question
     */
    public int getType() {
        return type;
    }

    /**
     * returns the 4 activities we fetched from the database
     * @return the 4 activities from the database
     */
    public List<Activity> getActivities() {
        return activities;
    }

    /**
     * returns the class but trimmed
     * @return trimmed question
     */
    public QuestionTrimmed getTrimmed() {
        return new QuestionTrimmed(question, answers, type, answer, url);
    }

    /**
     * returns the path to the image
     * @return path
     */
    public List<String> getUrl() {
        return url;
    }

    /**
     * returns the object as a string
     * @return
     */
    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", answers=" + answers +
                ", type=" + type +
                ", activities=" + activities +
                ", dt=" + dt +
                ", answer='" + answer + '\'' +
                '}';
    }
}
