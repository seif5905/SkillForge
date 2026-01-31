package Backend;

public class QuizResult {

    private String resultId;
    private String quizId;
    private String lessonId;
    private String courseId;
    private String studentId;
    private int score;
    private boolean passed;

    public QuizResult(String resultId, String quizId, String lessonId, String courseId,
                      String studentId, int score, boolean passed){
        this.resultId = resultId;
        this.quizId = quizId;
        this.lessonId = lessonId;
        this.courseId = courseId;
        this.studentId = studentId;
        this.score = score;
        this.passed = passed;
    }


    public String getResultId() {
        return resultId;
    }
    public void setResultId(String resultId) {
        this.resultId = resultId;
    }
    public String getQuizId() {
        return quizId;
    }
    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
    public String getLessonId() {
        return lessonId;
    }
    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }
    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public boolean isPassed() {
        return passed;
    }
    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
