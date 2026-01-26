package Backend;

import java.io.*;
import java.util.ArrayList;

public class DatabaseManager {

    public ArrayList<User> loadUsers(){
        ArrayList<User> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String parts[] = line.split(",");
                User user = new User(parts[0],parts[1],parts[2],parts[3],parts[4]);
                users.add(user);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public void saveUsers(ArrayList<User> users){
        try (FileWriter writer = new FileWriter("users.txt")) {
            for (User user : users) {
                String line =
                        user.getUserid() + "," +
                                user.getRole() + "," +
                                user.getUsername() + "," +
                                user.getEmail() + "," +
                                user.getPasswordHash() + ",";

                writer.write(line);
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
