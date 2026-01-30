package Backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DatabaseManager {

    public <T> void save(ArrayList<T> list, String filename){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try(FileWriter writer = new FileWriter(filename)){
            gson.toJson(list, writer);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

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
        save(users, "users.json");
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
        save(courses, "courses.json");
    }

    public ArrayList<Lesson> loadLessons(){
        Gson gson = new Gson();
        Type userListType = new TypeToken<ArrayList<Lesson>>() {}.getType();

        try(FileReader reader = new FileReader("lessons.json")){
            ArrayList<Lesson> lessons = gson.fromJson(reader, userListType);

            if(lessons != null)
                return lessons;
            return new ArrayList<>();
        }
        catch(IOException e){
            return new ArrayList<>();
        }
    }
    public void saveLessons(ArrayList<Lesson> lessons){
        save(lessons, "lessons.json");
    }

    public Course getCourseById(String courseId){
        ArrayList<Course> courses = loadCourses();

        for(Course course : courses){
            if(course.getCourseId().equalsIgnoreCase(courseId))
                return course;
        }
        return null;
    }
    public Lesson getLessonById(String lessonId){
        ArrayList<Lesson> lessons = loadLessons();

        for (Lesson lesson : lessons){
            if(lesson.getLessonId().equalsIgnoreCase(lessonId))
                return lesson;
        }
        return null;
    }
}
