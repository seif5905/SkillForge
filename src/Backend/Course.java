package Backend;

import java.util.ArrayList;

public class Course {

    private String courseId;
    private String title;
    private String description;
    private String instructorId;
    private ArrayList<String> Lessons;
    private ArrayList<String> students;

    public Course(String courseId, String title, String description, String instructorId,
                  ArrayList<String> Lessons, ArrayList<String> students){
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.Lessons = Lessons;
        this.students = students;
    }

    public Course(String courseId, String title, String description, String instructorId){
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
    }



    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public ArrayList<String> getStudents() {
        return students;
    }
    public void setStudents(ArrayList<String> students) {
        this.students = students;
    }
    public ArrayList<String> getLessons() {
        return Lessons;
    }
    public void setLessons(ArrayList<String> lessons) {
        Lessons = lessons;
    }
    public String getInstructorId() {
        return instructorId;
    }
    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }
    public String getCourseId() {
        return courseId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
