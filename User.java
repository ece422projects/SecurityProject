public class User {

    private String userName;
    private int userGroup;

    public User(String uName) {
        this.userName = uName;
    }

    public String returnUserName() {
        return this.userName;
    }
}