package Backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DatabaseManager {

    public ArrayList<User> loadUsers(){
        Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new UserAdapter()).create();

        Type userListType = new TypeToken<ArrayList<User>>() {}.getType();

        try(FileReader reader = new FileReader("users.json")){
            ArrayList<User> users = gson.fromJson(reader, userListType);

            if(users != null)
                return users;
            return new ArrayList<>();
        }
        catch(IOException e){
            return new ArrayList<>();
        }
    }

    public void saveUsers(ArrayList<User> users){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try(FileWriter writer = new FileWriter("users.json")){
            gson.toJson(users, writer);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public ArrayList<Course> loadCourses(){
        Gson gson = new Gson();
        Type userListType = new TypeToken<ArrayList<Course>>() {}.getType();

        try(FileReader reader = new FileReader("courses.json")){
            ArrayList<Course> courses = gson.fromJson(reader, userListType);

            if(courses != null)
                return courses;
            return new ArrayList<>();
        }
        catch(IOException e){
            return new ArrayList<>();
        }
    }

    public void saveCourses(ArrayList<Course> courses){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try(FileWriter writer = new FileWriter("courses.json")){
            gson.toJson(courses, writer);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public Course getCourseById(String courseId){
        ArrayList<Course> courses = loadCourses();

        for(Course course : courses){
            if(course.getCourseId().equalsIgnoreCase(courseId))
                return course;
        }
        return null;
    }
}
