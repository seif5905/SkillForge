package Backend;

import java.util.ArrayList;

public class Admin extends User{

    public Admin(String userid, String username, String email, String passwordHash){
        super(userid, "Admin", username, email, passwordHash);
    }

    public boolean setCourseStatus(String courseId, String state){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();

        for (int i = 0; i < courses.size(); i++) {
            if(courses.get(i).getCourseId().equalsIgnoreCase(courseId)){
                courses.get(i).setStatus(state);
                db.saveCourses(courses);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Course> getPendingCourses(){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        ArrayList<Course> pendingCourses = new ArrayList<>();

        for(Course course : courses){
            if(course.getStatus().equalsIgnoreCase("pending")){
                pendingCourses.add(course);
            }
        }
        return pendingCourses;
    }

    public boolean deleteCourse(String courseId){
        DatabaseManager db = new DatabaseManager();
        ArrayList<Course> courses = db.loadCourses();
        ArrayList<User> users = db.loadUsers();
        ArrayList<Lesson> lessons = db.loadLessons();

        for (int i = 0; i < courses.size(); i++) {
            if(courses.get(i).getCourseId().equalsIgnoreCase(courseId)){
                courses.remove(i);

                for (int j = 0; j < users.size(); j++) {
                    if(users.get(j) instanceof Student){
                        for (int k = 0; k < ((Student) users.get(j)).getEnrolledCourses().size(); k++) {
                            if(((Student) users.get(j)).getEnrolledCourses().get(k).equalsIgnoreCase(courseId)){
                                ((Student) users.get(j)).getEnrolledCourses().remove(k);
                                break;
                            }
                        }
                    }
                }
                for (int j = 0; j < users.size(); j++) {
                    if(users.get(j) instanceof Instructor){
                        for (int k = 0; k < ((Instructor) users.get(j)).getCreatedCourses().size(); k++) {
                            if(((Instructor) users.get(j)).getCreatedCourses().get(k).equalsIgnoreCase(courseId)){
                                ((Instructor) users.get(j)).getCreatedCourses().remove(k);
                                break;
                            }
                        }
                    }
                }
                for (int j = lessons.size() - 1; j >= 0; j--) {
                    if (lessons.get(j).getCourseId().equalsIgnoreCase(courseId)) {
                        lessons.remove(j);
                    }
                }
                db.saveCourses(courses);
                db.saveUsers(users);
                db.saveLessons(lessons);
                return true;
            }
        }
        return false;
    }
    public boolean deleteUser(String userId){
        DatabaseManager db = new DatabaseManager();
        ArrayList<User> users = db.loadUsers();
        ArrayList<Course> courses = db.loadCourses();
        ArrayList<Lesson> lessons = db.loadLessons();

        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getUserid().equalsIgnoreCase(userId)) {
                users.remove(i);

                // if user removed is student
                for (int j = 0; j < courses.size(); j++) {
                    for (int k = 0; k < courses.get(j).getStudents().size(); k++) {
                        if(courses.get(j).getStudents().get(k).equalsIgnoreCase(userId)) {
                            courses.get(j).getStudents().remove(k);
                            break;
                        }
                    }
                }
                // if user removed is instructor
                for (int j = 0; j < courses.size(); j++) {
                    if(courses.get(j).getInstructorId().equalsIgnoreCase(userId)){
                        String courseId = courses.get(j).getCourseId();
                        courses.remove(j);
                        j--;

                        for (int k = 0; k < users.size(); k++) {
                            if(users.get(k) instanceof Student){
                                for (int p = 0; p < ((Student) users.get(k)).getEnrolledCourses().size(); p++) {
                                    if(((Student) users.get(k)).getEnrolledCourses().get(p).equalsIgnoreCase(courseId)){
                                        ((Student) users.get(k)).getEnrolledCourses().remove(p);
                                        break;
                                    }
                                }
                            }
                        }
                        for (int k = lessons.size() - 1; k >= 0; k--) {
                            if (lessons.get(k).getCourseId().equalsIgnoreCase(courseId)){
                                lessons.remove(k);
                            }
                        }
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

}
