public class SecureFileSystem {

    private Directory rootDirectory;
    private ArrayList<User> userList;

    public SecureFileSystem() {

        rootDirectory = new Directory("root");
        userList = new ArrayList<User>();
    }

    public Directory returnRootDirectory() {
        return this.rootDirectory;
    }

    public ArrayList<User> returnUserList() {
        return this.userList;
    }

    public void addUser(User u) {
        this.userList.add(u);
        rootDirectory.addFolder(u.get);
    }

    public void removeUser(User u) {
        this.userList.remove(u);
    }
}