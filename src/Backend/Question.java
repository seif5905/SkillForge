package Backend;

import java.util.ArrayList;

public class Question {

    private String questionId;
    private String questionTitle;
    private ArrayList<String> choices;
    private int correctChoice;

    public Question(String questionId, String questionTitle, ArrayList<String> choices, int correctChoice){
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.correctChoice = correctChoice;
        if(choices != null)
            this.choices = choices;
        else
            this.choices = new ArrayList<>();
    }
    public Question(String questionId, String questionTitle){
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.correctChoice = 0;
        this.choices = new ArrayList<>();
    }

    public String getQuestionId() {
        return questionId;
    }
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    public String getQuestionTitle() {
        return questionTitle;
    }
    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }
    public ArrayList<String> getChoices() {
        return choices;
    }
    public void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }
    public int getCorrectChoice() {
        return correctChoice;
    }
    public void setCorrectChoice(int correctChoice) {
        this.correctChoice = correctChoice;
    }
}
