package Backend;

import java.util.ArrayList;

public class Student extends User{

    private ArrayList<String> enrolledCourses;
    private ArrayList<String> progress;

    public Student(String userid, String role, String username, String email, String passwordHash,
                   ArrayList<String> enrolledCourses, ArrayList<String> progress){
        super(userid, role, username, email, passwordHash);
        enrolledCourses = new ArrayList<>();
        progress = new ArrayList<>();
    }

    public boolean enrollInCourse()
}
