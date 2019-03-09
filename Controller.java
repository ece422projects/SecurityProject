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
        mySQLDatabaseHandler.addDirectory(username, "D", "/users/" + username);
        commandLineHandler.createPhysicalDirectory("/users/" + username);
    }

    public ArrayList<String> login(String username, String password) {

        SystemUser systemUser = mySQLDatabaseHandler.returnSystemUser();
        Boolean correctLogin = mySQLDatabaseHandler.logIn(username, password);
        
        if (correctLogin) {
            ArrayList<String> encryptedFileNames = commandLineHandler.checkForCorruption(username);
            ArrayList<String> decryptedNames = new ArrayList<String>();

            for (String path : encryptedFileNames) {
                String decryptedPath = PathParsing.decryptPath(systemUser, path);
                decryptedNames.add( PathParsing.returnElementName(systemUser, decryptedPath));
            }

            return decryptedNames;        
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

        SystemUser systemUser = mySQLDatabaseHandler.returnSystemUser();
        String encryptedPath = PathParsing.encryptPath(systemUser, path).trim();
        mySQLDatabaseHandler.addDirectory(username, "D", path);
        commandLineHandler.createPhysicalDirectory(encryptedPath);
    }

    public void addFile(String username, String path, String fileBody) {

        SystemUser systemUser = mySQLDatabaseHandler.returnSystemUser();
        String encryptedPath = PathParsing.encryptPath(systemUser, path).trim();
        String encryptedFilebody = systemUser.encryptData(fileBody);
        mySQLDatabaseHandler.addFile(username, "F", path, fileBody);
        commandLineHandler.createPhysicalFile(encryptedPath, encryptedFilebody);
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

        SystemUser systemUser = mySQLDatabaseHandler.returnSystemUser();
        String encryptedPath = PathParsing.encryptPath(systemUser, path).trim();
        String encryptedFilebody = systemUser.encryptData(newFilebody);
        mySQLDatabaseHandler.editFile(username, path, newFilebody);
        commandLineHandler.deletePhysicalFile(encryptedPath);
        commandLineHandler.createPhysicalFile(encryptedPath, encryptedFilebody);
    }

    public boolean canRename(String username, String path) {

        return mySQLDatabaseHandler.isOwner(username, path);
    }

    public void renameFile(String username, String path, String newName) {

        mySQLDatabaseHandler.rename(username, path, newName);
    }

    public void renameDirectory(String username, String path, String newName) {

        mySQLDatabaseHandler.rename(username, path, newName);
    }

    public boolean canDelete(String username, String path) {

        return mySQLDatabaseHandler.isOwner(username, path);
    }

    public void deleteFile(String username, String path) {

        SystemUser systemUser = mySQLDatabaseHandler.returnSystemUser();
        String encryptedPath = PathParsing.encryptPath(systemUser, path).trim();
        mySQLDatabaseHandler.delete(username, path);
        commandLineHandler.deletePhysicalFile(encryptedPath);
    }

    public void deleteDirectory(String username, String path) {

        SystemUser systemUser = mySQLDatabaseHandler.returnSystemUser();
        String encryptedPath = PathParsing.encryptPath(systemUser, path).trim();
        mySQLDatabaseHandler.delete(username, path);
        commandLineHandler.deletePhysicalDirectory(encryptedPath);
    }

    public void close() {
        mySQLDatabaseHandler.close();
        commandLineHandler.close();
    }
}