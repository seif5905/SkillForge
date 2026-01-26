package Backend;

import java.util.ArrayList;

public class Instructor extends User{

    private ArrayList<Course> createdCourses;

    public Instructor(String userid, String role, String username, String email, String passwordHash,
                   ArrayList<Course> createdCourses){
        super(userid, role, username, email, passwordHash);
        createdCourses = new ArrayList<>();
    }
    public boolean createCourse(String courseId, String title, String description){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        for(Course course : courses){
            if(course.getCourseId().equalsIgnoreCase(courseId) ||
                    course.getTitle().equalsIgnoreCase(title))
                return false;
        }
        Course course = new Course(courseId, title, description, this.getUserid());
        courses.add(course);
        createdCourses.add(course);
        return true;
    }

    public boolean addLesson(String courseId, String lessonId, String title, String content){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        for(Course course : this.createdCourses){
            if(courseId.equalsIgnoreCase(course.getCourseId())){
                Lesson lesson = new Lesson(lessonId, title, content);
                course.addLesson(lesson);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Student> viewEnrolledStudents(String courseId){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        for(Course course : this.createdCourses){
            if(courseId.equalsIgnoreCase(course.getCourseId())){
                return course.getStudents();
            }
        }
        return new ArrayList<>();
    }
}
