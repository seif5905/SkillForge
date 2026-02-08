package Backend;

import java.util.ArrayList;

public class User {

    private String userid;
    private String role;
    private String username;
    private String email;
    private String passwordHash;

    public User(String userid, String role, String username, String email, String passwordHash){
        this.userid = userid;
        this.role = role;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    public User(String userid, String role, String username, String passwordHash){
        this.userid = userid;
        this.role = role;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public boolean signup(){
        DatabaseManager db = new DatabaseManager();
        ArrayList<User> users = db.loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getUsername().equalsIgnoreCase(this.username) ||
                    users.get(i).getEmail().equalsIgnoreCase(this.email))
                return false;
        }
        users.add(this);
        db.saveUsers(users);
        return true;
    }

    public User login() {
        DatabaseManager db = new DatabaseManager();
        ArrayList<User> users = db.loadUsers();
        for (User user : users) {
            if ((user.getUsername().equalsIgnoreCase(this.username) || user.getEmail().equalsIgnoreCase(this.email)) &&
                    user.getRole().equalsIgnoreCase(this.role) &&
                    user.getPasswordHash().equalsIgnoreCase(this.passwordHash))
                return user;
        }
        return null;
    }



    public void setUserid(String userid){
        this.userid = userid;
    }
    public String getUserid(){
        return this.userid;
    }
    public void setRole(String role){
        this.role = role;
    }
    public String getRole(){
        return this.role;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return this.email;
    }
    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }
    public String getPasswordHash(){
        return this.passwordHash;
    }
}
