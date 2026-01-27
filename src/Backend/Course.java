package Backend;

import java.util.ArrayList;

public class Course {

    private String courseId;
    private String title;
    private String description;
    private String instructorId;
    private ArrayList<Lesson> lessons;
    private ArrayList<String> students;

    public Course(String courseId, String title, String description, String instructorId,
                  ArrayList<Lesson> lessons, ArrayList<String> students){
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        if(lessons != null)
            this.lessons = lessons;
        else
            this.lessons = new ArrayList<>();
        if(students != null)
            this.students = students;
        else
            this.students = new ArrayList<>();
    }

    public Course(String courseId, String title, String description, String instructorId){
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public void addLesson(Lesson lesson){
        this.lessons.add(lesson);
    }

    public void addStudent(Student student){
        this.students.add(student.getUserid());
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
    public ArrayList<Lesson> getLessons() {
        return lessons;
    }
    public void setLessons(ArrayList<Lesson> lessons) {
        this.lessons = lessons;
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
