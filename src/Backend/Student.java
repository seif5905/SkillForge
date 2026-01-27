package Backend;

import java.util.ArrayList;

public class Student extends User{

    private ArrayList<Course> enrolledCourses;
    private ArrayList<String> progress;

    public Student(String userid, String role, String username, String email, String passwordHash,
                   ArrayList<String> enrolledCourses, ArrayList<String> progress){
        super(userid, role, username, email, passwordHash);
        enrolledCourses = new ArrayList<>();
        progress = new ArrayList<>();
    }

    public boolean enrollInCourse(String courseId){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        ArrayList<User> users = db.loadUsers();

        for(Course course : courses){
            if(course.getCourseId().equalsIgnoreCase(courseId)){
                if(!(course.getStudents().contains(this))){
                    course.addStudent(this);
                    this.enrolledCourses.add(course);
                    db.saveCourses(courses);
                    db.saveUsers(users);
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Course> getEnrolledCourses() {
        return enrolledCourses;
    }
    public void setEnrolledCourses(ArrayList<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }
    public ArrayList<String> getProgress() {
        return progress;
    }
    public void setProgress(ArrayList<String> progress) {
        this.progress = progress;
    }
}
