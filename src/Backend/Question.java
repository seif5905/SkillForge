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
}
