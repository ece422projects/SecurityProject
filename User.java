import java.util.*;

public class User {

    private String username;
    private String password;

    public User(String username, String password) {

        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String eKey) {

        this.username = username;
        this.password = password;
    }

    public String getusername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}