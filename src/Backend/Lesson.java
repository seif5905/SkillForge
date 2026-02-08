package Backend;

import java.util.ArrayList;

public class Lesson {

    private String lessonId;
    private String courseId;
    private String title;
    private String content;
    private ArrayList<Quiz> quizzes;

    public Lesson(String lessonId, String courseId, String title, String content){
        this.lessonId = lessonId;
        this.courseId = courseId;
        this.title = title;
        this.content = content;
        this.quizzes = new ArrayList<>();
    }

    public void addQuiz(Quiz quiz){
        quizzes.add(quiz);
    }

    public String getLessonId() {
        return lessonId;
    }
    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public ArrayList<Quiz> getQuizzes() {
        return quizzes;
    }
    public void setQuizzes(ArrayList<Quiz> quizzes) {
        this.quizzes = quizzes;
    }
    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
