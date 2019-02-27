public class User {

    private String userName;
    private Directory userDirectory;

    public User(String uName) {
        this.userName = uName;
        this.userDirectory = new Directory(uName);
    }
}