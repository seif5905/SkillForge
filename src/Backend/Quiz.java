package Backend;

import java.util.ArrayList;

public class Quiz {

    private String quizId;
    private String title;
    private String passingScore;
    private ArrayList<Question> questions;

    public Quiz(String quizId, String passingScore, String title, ArrayList<Question> questions){
        this.quizId = quizId;
        this.passingScore = passingScore;
        this.title = title;
        if(questions != null)
            this.questions = questions;
        else
            this.questions = new ArrayList<>();
    }
    public Quiz(String quizId, String passingScore, String title){
        this.quizId = quizId;
        this.passingScore = passingScore;
        this.title = title;
        this.questions = new ArrayList<>();
    }

    public void addQuestion(Question question){
        questions.add(question);
    }

    public String getQuizId() {
        return quizId;
    }
    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPassingScore() {
        return passingScore;
    }
    public void setPassingScore(String passingScore) {
        this.passingScore = passingScore;
    }
    public ArrayList<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}
