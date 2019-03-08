import java.util.ArrayList;

public class Controller {

    private CommandLineHandler commandLineHandler;
    private MySQLDatabaseHandler mySQLDatabaseHandler;

    public Controller() {

        commandLineHandler = new CommandLineHandler();
        mySQLDatabaseHandler = new MySQLDatabaseHandler();
    }

    public void signUp(String username, String password) {

        mySQLDatabaseHandler.signUp(username, password);
    }

    public ArrayList<String> login(String username, String password) {

        Boolean correctLogin = mySQLDatabaseHandler.logIn(username, password);

        if (correctLogin) {
            return commandLineHandler.checkForCorruption(username);
        } else {
            return null;
        }
    }

    public void addToGroup(String groupname, ArrayList<String> usernameList) {

        for (String username : usernameList) {
            mySQLDatabaseHandler.addToGroups(groupname, username);
        }
    }

    public void removeFromGroup(String groupname, ArrayList<String> usernameList) {

        for (String username : usernameList) {
            mySQLDatabaseHandler.removeFromGroups(groupname, username);
        }
    }

    public void addDirectory(String username, String path) {

        mySQLDatabaseHandler.addDirectory(username, "D", path);
    }

    public void addFile(String username, String path, String fileBody) {

        mySQLDatabaseHandler.addFile(username, "F", path, fileBody);
    }

    public ArrayList<ArrayList<String>> openDirectory(String username, String path) {

        return mySQLDatabaseHandler.openDirectory(username, path);
    }

    public String openFile(String username, String path) {

        return mySQLDatabaseHandler.openFile(username, path);
    }

    public boolean canEdit(String username, String path) {

        return mySQLDatabaseHandler.canEditFile(username, path);
    }

    public void editFile(String username, String path, String newFilebody) {

        mySQLDatabaseHandler.editFile(username, path, newFilebody);
    }

    public boolean canRename(String username, String path) {

        return mySQLDatabaseHandler.isOwner(username, path);
    }

    public void rename(String username, String path, String newName) {

        mySQLDatabaseHandler.rename(username, path, newName);
    }

    public boolean canDelete(String username, String path) {

        return mySQLDatabaseHandler.isOwner(username, path);
    }

    public void delete(String username, String path) {

        mySQLDatabaseHandler.delete(username, path);
    }

    public void close() {
        commandLineHandler.deleteRootDirectory();
        commandLineHandler.makeRootDirectory();
        commandLineHandler.makePhysicalRecord();
        commandLineHandler.close();
        mySQLDatabaseHandler.close();
    }
}
