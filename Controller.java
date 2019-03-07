import java.util.ArrayList;

public class Controller {

    private CommandLineHandler commandLineHandler;
    private MySQLDatabaseHandler mySQLDatabaseHandler;

    public Controller() {

        commandLineHandler = new CommandLineHandler();
        mySQLDatabaseHandler = new MySQLDatabaseHandler();
    }

    public void signUp(String username, String password) {

        User user = new User(username, password);
        mySQLDatabaseHandler.addToUsers(user);
        mySQLDatabaseHandler.addUserDirectory(user);
        commandLineHandler.createUserDirectory(user);
    }

    public User login(String username, String password) {

        if (mySQLDatabaseHandler.isCorrectLogin(username, password)) {
            return mySQLDatabaseHandler.getFromUsers(username, password);
        }
        else {
            return null;
        }
    }

    public ArrayList<String> openRootDirectory() {
        return mySQLDatabaseHandler.openRootDirectory();
    }

    public void addToGroup(User user, String groupname, ArrayList<String> usernameList) {

        for (String username : usernameList) {
            mySQLDatabaseHandler.addToGroups(groupname, username);
        }
    }

    public void removeFromGroup(String groupname, ArrayList<String> usernameList) {

        for (String username : usernameList) {
            mySQLDatabaseHandler.removeFromGroups(groupname, username);
        }      
    }

    public void editDirectoryPermissions(User user, String directorypath, String directoryname, String groupname, String canRead, String canWrite) {

        mySQLDatabaseHandler.addDirectoryPermissions(user, directorypath, directoryname, groupname, canRead, canWrite);

    }

    public void editFilePermissions(User user, String filepath, String filename, String groupname, String canRead, String canWrite) {

        mySQLDatabaseHandler.addFilePermissions(user, filepath, filename, groupname, canRead, canWrite);

    }

    public void createDirectory(User user, String directorypath, String directoryname) {

        mySQLDatabaseHandler.addToDirectories(user, directorypath, directoryname);
    }

    public ArrayList<ArrayList<String>> openDirectory(User user, String directorypath, String directoryname) {

        return mySQLDatabaseHandler.openDirectory(user, directorypath, directoryname);
    }

    public String openFile(User user, String filepath, String filename) {

        return mySQLDatabaseHandler.openFile(user, filepath, filename);
    }

    public Boolean canRenameDirectory(User user, String directorypath, String directoryname) {

        if (mySQLDatabaseHandler.canEditDirectory(user, directorypath, directoryname)) {
            return true;
        } else {
            return false;
        }
    }

    public void renameDirectory(User user, String directorypath, String directoryname, String newdirectoryname) {
  
        mySQLDatabaseHandler.renameDirectory(user, directorypath, directoryname, newdirectoryname);
    }

    public Boolean canRenameFile(User user, String filepath, String filename) {

        if (mySQLDatabaseHandler.canEditFile(user, filepath, filename)) {
            return true;
        } else {
            return false;
        }
    }

    public void renameFile(User user, String filepath, String filename, String newfilename) {
  
        mySQLDatabaseHandler.renameFile(user, filepath, filename, newfilename);
    }

    public Boolean canEditFile(User user, String filepath, String filename) {

        if (mySQLDatabaseHandler.canEditFile(user, filepath, filename)) {
            return true;
        } else {
            return false;
        }
    }

    public void editFile(User user, String filepath, String filename, String filebody) {

        mySQLDatabaseHandler.addToFiles(user, filepath, filename, filebody);
    }

    public Boolean canDeleteFile(User user, String filepath, String filename) {

        if (mySQLDatabaseHandler.isFileOwner(user, filepath, filename)) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteFile(User user, String filepath, String filename) {
  
        mySQLDatabaseHandler.deleteFile(user, filepath, filename);
    }

    public Boolean canDeleteDirectory(User user, String directorypath, String directoryname) {

        if (mySQLDatabaseHandler.isDirectoryOwner(user, directorypath, directoryname)) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteDirectory(User user, String directorypath, String directoryname) {
  
        mySQLDatabaseHandler.deleteDirectory(user, directorypath, directoryname);
    }

    public void close() {
        mySQLDatabaseHandler.close();
    }
}