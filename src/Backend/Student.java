package Backend;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Student extends User{

    private ArrayList<String> enrolledCourses;
    private ArrayList<String> completedLessons;
    private ArrayList<QuizResult> quizResults;
    private ArrayList<Backend.Certificate> certificates;

    public Student(String userid, String username, String email, String passwordHash,
                   ArrayList<String> enrolledCourses, ArrayList<String> completedLessons,
                   ArrayList<QuizResult> quizResults, ArrayList<Backend.Certificate> certificates){
        super(userid, "Student", username, email, passwordHash);
        if(enrolledCourses != null)
            this.enrolledCourses = enrolledCourses;
        else
            this.enrolledCourses = new ArrayList<>();
        if(completedLessons != null)
            this.completedLessons = completedLessons;
        else
            this.completedLessons = new ArrayList<>();
        if(quizResults != null)
            this.quizResults = quizResults;
        else
            this.quizResults = new ArrayList<>();
        if(certificates != null)
            this.certificates = certificates;
        else
            this.certificates = new ArrayList<>();
    }
    public Student(String userid, String username, String email, String passwordHash){
        super(userid, "Student", username, email, passwordHash);
        this.enrolledCourses = new ArrayList<>();
        this.completedLessons = new ArrayList<>();
        this.quizResults = new ArrayList<>();
        this.certificates = new ArrayList<>();
    }

    public boolean enrollInCourse(String courseId){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        ArrayList<User> users = db.loadUsers();

        for(Course course : courses){
            if(course.getCourseId().equalsIgnoreCase(courseId)){
                if(!(course.getStudents().contains(this.getUserid()))){
                    course.addStudent(this);
                    this.enrolledCourses.add(courseId);

                    for(int i = 0; i < users.size(); i++){  // this loop is to update the users list
                        if(this.getUserid().equalsIgnoreCase(users.get(i).getUserid())){
                            users.set(i,this);
                            break;
                        }
                    }

                    db.saveCourses(courses);
                    db.saveUsers(users);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean markLessonAsCompleted(String lessonId){
        DatabaseManager db = new DatabaseManager();
        ArrayList<User> users = db.loadUsers();
        ArrayList<Lesson> lessons = db.loadLessons();

        // getting lesson
        Lesson targetLesson = null;
        for (Lesson lesson : lessons){
            if (lesson.getLessonId().equalsIgnoreCase(lessonId)){
                targetLesson = lesson;
                break;
            }
        }

        if (targetLesson == null) return false;

        //checking if student passed all quizzes of this lesson
        if(!targetLesson.getQuizzes().isEmpty()){
            for (Quiz quiz : targetLesson.getQuizzes()){

                boolean passedTheQuiz = false;
                for (QuizResult result : this.quizResults){
                    if (result.getLessonId().equalsIgnoreCase(lessonId) &&
                        result.getQuizId().equalsIgnoreCase(quiz.getQuizId()) &&
                        result.isPassed()){
                        passedTheQuiz = true;
                        break;
                    }
                }
                if(!passedTheQuiz) return false;
            }
        }

        // marking lesson as complete
        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getUserid().equalsIgnoreCase(this.getUserid())){
                if(!((Student) users.get(i)).getCompletedLessons().contains(lessonId)){
                    ((Student) users.get(i)).getCompletedLessons().add(lessonId);
                }
                db.saveUsers(users);
                return true;
            }
        }
        return false;
    }

    public boolean generateCertificate(String courseId){
        DatabaseManager db = new DatabaseManager();
        ArrayList<User> users = db.loadUsers();

        // Get all lessons for this course
        ArrayList<Lesson> courseLessons = db.getLessonsForCourse(courseId);
        if (courseLessons.isEmpty()) return false;

        // Check if student completed ALL lessons in this course
        for (Lesson lesson : courseLessons) {
            if (!this.completedLessons.contains(lesson.getLessonId())) {
                return false; // Student hasn't completed all lessons
            }
        }

        // Get course details
        Course course = db.getCourseById(courseId);
        if (course == null) return false;

        // Check if certificate already exists for this course
        for (Backend.Certificate cert : this.certificates) {
            if (cert.getCourseId().equalsIgnoreCase(courseId)) {
                return false; // Already has certificate for this course
            }
        }

        // Generate certificate
        Random rand = new Random();
        String certificateId = String.valueOf(rand.nextInt(99999 - 10000 + 1) + 10000);
        Backend.Certificate certificate = new Backend.Certificate(
                certificateId,
                this.getUserid(),      // studentId
                this.getUsername(),    // studentName
                courseId,
                course.getTitle()      // courseTitle
        );

        // Add to student and save
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserid().equalsIgnoreCase(this.getUserid())) {
                ((Student) users.get(i)).getCertificates().add(certificate);
                db.saveUsers(users);
                this.certificates.add(certificate); // Update current object too
                return true;
            }
        }

        return false;
    }

    public boolean saveQuizResult(String courseId, String lessonId, String quizId, int score, boolean passed){
        DatabaseManager db = new DatabaseManager();
        ArrayList<User> users = db.loadUsers();

        // Generate unique result ID
        Random rand = new Random();
        String resultId = String.valueOf(rand.nextInt(99999 - 10000 + 1) + 10000);

        // Create quiz result
        QuizResult result = new QuizResult(resultId, quizId, lessonId, courseId,
                this.getUserid(), score, passed);

        // Find and update this student in the database
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserid().equalsIgnoreCase(this.getUserid())) {
                // Check if result already exists for this quiz (optional - prevent duplicates)
                Student student = (Student) users.get(i);

                // Option A: Replace old result if exists
                for (int j = 0; j < student.getQuizResults().size(); j++) {
                    if (student.getQuizResults().get(j).getQuizId().equalsIgnoreCase(quizId)) {
                        student.getQuizResults().remove(j);
                        break;
                    }
                }

                // Option B: Or just keep adding all attempts (comment out the above loop)

                // Add new result
                student.getQuizResults().add(result);
                this.quizResults.add(result); // Update current object too

                users.set(i, student);
                db.saveUsers(users);
                return true;
            }
        }

        return false;
    }

    public ArrayList<Course> getAvailableCourses(){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        ArrayList<Course> availableCourses = new ArrayList<>();

        for (Course course : courses){
            if(course.getStatus().equalsIgnoreCase("approved"))
                availableCourses.add(course);
        }
        return availableCourses;
    }


    public ArrayList<String> getEnrolledCourses() {
        return enrolledCourses;
    }
    public void setEnrolledCourses(ArrayList<String> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }
    public ArrayList<String> getCompletedLessons() {
        return completedLessons;
    }
    public void setCompletedLessons(ArrayList<String> completedLessons) {
        this.completedLessons = completedLessons;
    }
    public ArrayList<QuizResult> getQuizResults() {
        return quizResults;
    }
    public void setQuizResults(ArrayList<QuizResult> quizResults) {
        this.quizResults = quizResults;
    }
    public ArrayList<Backend.Certificate> getCertificates() {
        return certificates;
    }
    public void setCertificates(ArrayList<Backend.Certificate> certificates) {
        this.certificates = certificates;
    }
}
