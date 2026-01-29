package Backend;

import java.util.ArrayList;

public class Student extends User{

    private ArrayList<String> enrolledCourses;
    private ArrayList<String> progress;

    public Student(String userid, String username, String email, String passwordHash,
                   ArrayList<String> enrolledCourses, ArrayList<String> progress){
        super(userid, "Student", username, email, passwordHash);
        if(enrolledCourses != null)
            this.enrolledCourses = enrolledCourses;
        else
            this.enrolledCourses = new ArrayList<>();
        if(progress != null)
            this.progress = progress;
        else
            this.progress = new ArrayList<>();
    }
    public Student(String userid, String username, String email, String passwordHash){
        super(userid, "Student", username, email, passwordHash);
        this.enrolledCourses = new ArrayList<>();
        this.progress = new ArrayList<>();
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
    public ArrayList<String> getProgress() {
        return progress;
    }
    public void setProgress(ArrayList<String> progress) {
        this.progress = progress;
    }
}
