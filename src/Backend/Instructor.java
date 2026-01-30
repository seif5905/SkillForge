package Backend;

import java.util.ArrayList;

public class Instructor extends User{

    private ArrayList<String> createdCourses;

    public Instructor(String userid, String username, String email, String passwordHash,
                   ArrayList<String> createdCourses){
        super(userid, "Instructor", username, email, passwordHash);
        if(createdCourses != null)
            this.createdCourses = createdCourses;
        else
            this.createdCourses = new ArrayList<>();
    }

    public Instructor(String userid, String username, String email, String passwordHash){
        super(userid, "Instructor", username, email, passwordHash);
        this.createdCourses = new ArrayList<>();
    }

    public boolean createCourse(String courseId, String title, String description){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        ArrayList<User> users = db.loadUsers();
        for(Course course : courses){
            if(course.getCourseId().equalsIgnoreCase(courseId) ||
                    course.getTitle().equalsIgnoreCase(title))
                return false;
        }
        Course course = new Course(courseId, title, description, this.getUserid());
        courses.add(course);
        createdCourses.add(courseId);

        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUserid().equalsIgnoreCase(this.getUserid())){
                users.set(i,this);
                break;
            }
        }

        db.saveCourses(courses);
        db.saveUsers(users);
        return true;
    }

    public boolean deleteCourse(String courseId){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        ArrayList<User> users = db.loadUsers();
        ArrayList<Lesson> lessons = db.loadLessons();

        for(int i = 0; i < courses.size(); i++){

            if(courses.get(i).getCourseId().equalsIgnoreCase(courseId) &&
                    courses.get(i).getInstructorId().equalsIgnoreCase(this.getUserid())){

                courses.remove(i);
                createdCourses.remove(courseId);

                for (int j = 0; j < users.size(); j++) {
                    // for instructors
                    if(users.get(j).getUserid().equalsIgnoreCase(this.getUserid())){
                        users.set(j,this);
                    }
                    // for students
                    else if(users.get(j) instanceof Student){

                        Student student = (Student) users.get(j);
                        if(student.getEnrolledCourses().contains(courseId)){

                            student.getEnrolledCourses().remove(courseId);
                            users.set(j,student);
                        }
                    }
                }

                for (int j = lessons.size() - 1; j >= 0; j--) {
                    if (lessons.get(j).getCourseId().equalsIgnoreCase(courseId)){
                        lessons.remove(j);
                    }
                }

                db.saveUsers(users);
                db.saveCourses(courses);
                db.saveLessons(lessons);
                return true;
            }
        }
        return false;
    }

    public boolean addLesson(String courseId, String lessonId, String title, String content){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        ArrayList<Lesson> lessons = db.loadLessons();

        for(Course course : courses){

            if(courseId.equalsIgnoreCase(course.getCourseId())){

                if(course.getInstructorId().equalsIgnoreCase(this.getUserid())) {

                    Lesson lesson = new Lesson(lessonId, courseId , title, content);
                    lessons.add(lesson);
                    course.addLesson(lesson);
                    db.saveLessons(lessons);
                    db.saveCourses(courses);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean deleteLesson(String lessonId){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        ArrayList<Lesson> lessons = db.loadLessons();

        boolean flag = false;

        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getInstructorId().equalsIgnoreCase(this.getUserid())) {

                for (int j = 0; j < lessons.size(); j++) {
                    if(lessons.get(j).getLessonId().equalsIgnoreCase(lessonId)){
                        lessons.remove(j);
                        flag = true;
                        break;
                    }
                }

                for (int j = 0; j < courses.get(i).getLessons().size(); j++) {
                    if (courses.get(i).getLessons().get(j).equalsIgnoreCase(lessonId)) {
                        courses.get(i).getLessons().remove(j);
                        flag = true;
                        break;
                    }
                }
                if(flag) {
                    db.saveLessons(lessons);
                    db.saveCourses(courses);
                }
            }
        }
        return flag;
    }

    public boolean addQuiz(String courseId, String lessonId, String quizId, String passingScore, String title){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Lesson> lessons = db.loadLessons();

        for (Lesson lesson : lessons){
            if (lesson.getLessonId().equalsIgnoreCase(lessonId) && lesson.getCourseId().equalsIgnoreCase(courseId)){

                Quiz quiz = new Quiz(quizId, passingScore, title);
                lesson.addQuiz(quiz);
                db.saveLessons(lessons);
                return true;
            }
        }

        return false;
    }
    /*
    public boolean deleteQuiz(){

    }
     */

    public boolean addQuestion(String courseId, String lessonId, String quizId, String questionId,
                               String questionTitle, ArrayList<String> choices, int passingScore){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Lesson> lessons = db.loadLessons();

        for (Lesson lesson : lessons){
            if(lesson.getLessonId().equalsIgnoreCase(lessonId) && lesson.getCourseId().equalsIgnoreCase(courseId)){
                for (Quiz quiz : lesson.getQuizzes()){
                    if (quiz.getQuizId().equalsIgnoreCase(quizId)){

                        Question question = new Question(questionId, questionTitle, choices, passingScore);
                        quiz.addQuestion(question);
                        db.saveLessons(lessons);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /*
    public boolean deleteQuestion(){

    }
     */

    public ArrayList<String> viewEnrolledStudents(String courseId){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        for(String courseid : this.createdCourses){
            if(courseId.equalsIgnoreCase(courseid)){
                Course course = db.getCourseById(courseId);
                return course.getStudents();
            }
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getCreatedCourses() {
        return createdCourses;
    }
    public void setCreatedCourses(ArrayList<String> createdCourses) {
        this.createdCourses = createdCourses;
    }
}
