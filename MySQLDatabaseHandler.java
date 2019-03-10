import java.sql.*;
import java.util.ArrayList;

public class MySQLDatabaseHandler {

   private String JDBCDriver = "com.mysql.jdbc.Driver";
   private String databaseURL = "jdbc:mysql://localhost/secure_file_system?useSSL=false";
   private String databaseUsername = "root";
   private String databasePassword = "Project";

   private Connection myConnection;
   private Statement myStatement;
   private ResultSet resultSet;
   private SystemUser systemUser;

   public MySQLDatabaseHandler() {
       myConnection = null;
       myStatement = null;

       try {
            Class.forName(JDBCDriver);

            myConnection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
            myStatement = myConnection.createStatement();

            String query = "SELECT encryptionkey from system_user";
            ResultSet resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                systemUser = new SystemUser(resultSet.getString("encryptionkey"));
            }
        }catch(SQLException se) {
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
   }

//    public void createSystemUser() {
//        SystemUser systemUser = new SystemUser();

//        String query = "INSERT INTO system_user(encryptionkey) VALUES('" + systemUser.getEncryptionKey() + "')";
//        try {
//            myStatement.execute(query);
//        } catch (SQLException se) {
//            se.printStackTrace();
//        }
//    }

    public SystemUser returnSystemUser() {
        return systemUser;
    }

    public Boolean signUp(String username, String password) {

        String query;
        query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";

        try {
            resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                return false;
            } else {
                query = "INSERT INTO users(username, password) VALUES('" + username + "', '" + password + "')";
                myStatement.execute(query);
            }

        }
        catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
        return true;
    }

   public Boolean logIn(String username, String password) {

        String query;
        query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";

        try {
            resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
    }

    public Boolean isOwner(String username, String path) {

        String query;
        ArrayList<String> groups = new ArrayList<String>();
        String encryptedPath = PathParsing.encryptPath(systemUser, path);
        query = "SELECT owner FROM groups WHERE path = '" + encryptedPath + "'";

        try {
            ResultSet rs = myStatement.executeQuery(query);

            if (rs.next()) {
                String owner = rs.getString("owner");

                if (owner.equals(username)) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;

        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getGroupsUserOwns(String username) {

        String query;
        ArrayList<String> groups = new ArrayList<String>();
        query = "SELECT DISTINCT groupname FROM groups WHERE owner = '" + username + "'";

        try {
            Connection connection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                groups.add(rs.getString("groupname"));
            }

            connection.close();
            statement.close();
            rs.close();

        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }

        if (groups.size() == 0) {
            groups.add(username);
        }

        return groups;
    }

    public ArrayList<String> getUserGroups(String username) {

        String query;
        ArrayList<String> groups = new ArrayList<String>();
        query = "SELECT groupname FROM groups WHERE username = '" + username + "'";

        try {
            Connection connection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                groups.add(rs.getString("groupname"));
            }

            connection.close();
            statement.close();
            rs.close();

        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }

        if (groups.size() == 0) {
            groups.add(username);
        }

        return groups;
    }

    public Boolean inGroup(String username, String groupname) {

        ArrayList<String> userGroups = getUserGroups(username);

        return userGroups.contains(groupname);
    }

    public Boolean hasGroupInCommon(String username, String owner) {

        ArrayList<String> userGroups = getUserGroups(username);
        ArrayList<String> ownerGroups = getUserGroups(owner);

        for (String groupname : userGroups) {
            if (ownerGroups.contains(groupname)) {
                return true;
            }
        }
        return false;
    }

    public Boolean addToGroups(String owner, String username, String groupname) {

        String query;
        query = "SELECT * FROM groups WHERE groupname = '" + groupname + "' AND username = '" + username + "' AND owner = '" + owner + "'";

        try {
             resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                return false;
            } else {
                query = "INSERT INTO groups(owner, groupname, username) VALUES('" + owner + "', '" + groupname + "', '" + username + "')";
                myStatement.execute(query);
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
        return true;
    }

    public Boolean removeFromGroups(String owner, String groupname, String username) {

        String query;
        query = "DELETE FROM groups WHERE groupname = '" + groupname + "' AND username = '" + username + "' AND owner = '" + owner + "'";

        try {
            myStatement.execute(query);
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }


    public Boolean addFile(String username, String type, String path, String fileBody) {

        String query;
        String encryptedPath = PathParsing.encryptPath(systemUser, path);
        String encryptedBody = systemUser.encryptData(fileBody);
        query = "INSERT INTO contents(owner, path, type, filebody) VALUES('" + username + "', '" + encryptedPath + "', '" + type + "', '" + encryptedBody + "')";

        try {
            myStatement.execute(query);
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean addDirectory(String username, String type, String path) {

        String query;
        String encryptedPath = PathParsing.encryptPath(systemUser, path);
        query = "INSERT INTO contents(owner, path, type) VALUES('" + username + "', '" + encryptedPath + "', '" + type + "')";

        try {
            myStatement.execute(query);
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean addPermissions(String username, String path, String groupname, String canRead, String canEdit) {

        String query;
        String encryptedPath = PathParsing.encryptPath(systemUser, path);

        query = "UPDATE contents SET groupname = '" + groupname + "', canread = '" + canRead + "', canedit = '" + canEdit + "' WHERE path = '" + encryptedPath + "'";

        try {
                myStatement.execute(query);
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean canEditFile(String username, String path) {

        String query;
        String encryptedPath = PathParsing.encryptPath(systemUser, path);

        try {
            query = "SELECT owner FROM contents WHERE path = '" + encryptedPath + "'";

            ResultSet resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                String owner = resultSet.getString("owner");

                if (hasGroupInCommon(username, owner)) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }


    public Boolean editFile(String username, String path, String newFilebody) {

        String query;
        String encryptedPath = PathParsing.encryptPath(systemUser, path);
        String encryptedFilebody = systemUser.encryptData(newFilebody);

        try {
            query = "UPDATE contents SET filebody = '" + encryptedFilebody + "' WHERE path = '" + encryptedPath + "'";

            myStatement.execute(query);
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean rename(String username, String path, String newName) {

        String query;
        String newPath = PathParsing.renameElement(systemUser, path, newName);
        String encryptedPath = PathParsing.encryptPath(systemUser, path);
        String encryptedNewPath = PathParsing.encryptPath(systemUser, newPath);

        try {
            query = "UPDATE contents SET path = '" + encryptedNewPath + "' WHERE path = '" + encryptedPath + "'";

            myStatement.execute(query);
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean delete(String username, String path) {

        String query;
        String encryptedPath = PathParsing.encryptPath(systemUser, path);

        try {
            query = "DELETE FROM contents WHERE path LIKE '" + encryptedPath + "'";

            myStatement.execute(query);
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public String openFile(String username, String path) {

        String query;
        String encryptedPath = PathParsing.encryptPath(systemUser, path);
        String filebody = null;

        try {
            query = "SELECT filebody FROM contents WHERE path = '" + encryptedPath + "'";

            ResultSet resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                filebody = systemUser.decryptData(resultSet.getString("filebody"));
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return filebody;
    }

    public ArrayList<ArrayList<String>> openDirectory(String username, String path) {

        String query;
        Boolean isEmpty = true;
        String encryptedPath = PathParsing.encryptPath(systemUser, path);
        ArrayList<ArrayList<String>> directoryContents = new ArrayList<ArrayList<String>>();
        ArrayList<String> files = new ArrayList<String>();
        ArrayList<String> directories = new ArrayList<String>();

        try {

            if (encryptedPath.equals("/users/")) {

                query = "SELECT type, path, canread, owner FROM contents WHERE path REGEXP '^" + encryptedPath + "[^/]+/$'";
                ResultSet resultSet = myStatement.executeQuery(query);

                while (resultSet.next()) {

                    isEmpty = false;
                    String type = resultSet.getString("type");
                    String name = PathParsing.returnElementName(systemUser, resultSet.getString("path"));
                    String owner = resultSet.getString("owner");

                    if (type.equals("D")) {
                        directories.add(name);
                    } else if (type.equals("F")) {
                        files.add(name);
                    }
                }
            }

            if (path.matches("/[^/]+/[^/]+")) {
                String name = PathParsing.returnElementName(systemUser, path);
                if (!hasGroupInCommon(username, name)) {
                    return null;
                }
            }

            if (isEmpty) {

                query = "SELECT type, path, groupname, canread, owner FROM contents WHERE path REGEXP '^" + encryptedPath + "[^/]+/$'";
                resultSet = myStatement.executeQuery(query);

                while (resultSet.next()) { // CAN READ

                    isEmpty = false;
                    String type = resultSet.getString("type");
                    String name = PathParsing.returnElementName(systemUser, resultSet.getString("path"));
                    String canRead = resultSet.getString("canread");
                    String owner = resultSet.getString("owner");
                    String groupname = resultSet.getString("groupname");

                    if (path.matches("/[^/]+/[^/]+")) {
                        if (owner.equals(username)) {
                            if (type.equals("D")) {
                                directories.add(systemUser.decryptData(name));
                            } else if (type.equals("F")) {
                                files.add(systemUser.decryptData(name));
                            }
                        } else if (canRead.equals("Y") && inGroup(username, groupname)) {
                            if (type.equals("D")) {
                                directories.add(systemUser.decryptData(name));
                            } else if (type.equals("F")) {
                                files.add(systemUser.decryptData(name));
                            }
                        } else {
                            if (type.equals("D")) {
                                directories.add(name);
                            } else if (type.equals("F")) {
                                files.add(name);
                            }
                        }
                    } else {
                        if (type.equals("D")) {
                            directories.add(systemUser.decryptData(name));
                        } else if (type.equals("F")) {
                            files.add(systemUser.decryptData(name));
                        }
                    }
                }
            }

            if (isEmpty) {

                query = "SELECT type, path, groupname, canread FROM contents WHERE path REGEXP '^" + path + "/[^/]+/$'";
                resultSet = myStatement.executeQuery(query);

                while (resultSet.next()) { // CAN VIEW

                    String type = resultSet.getString("type");
                    String name = PathParsing.returnElementName(systemUser, resultSet.getString("path"));
                    String groupname = resultSet.getString("groupname");
                    String canRead = resultSet.getString("canread");

                    if (canRead.equals("Y") && inGroup(username, groupname)) {
                        if (type.equals("D")) {
                            directories.add(systemUser.decryptData(name));
                        } else if (type.equals("F")) {
                            files.add(systemUser.decryptData(name));
                        }
                    } else {
                        if (type.equals("D")) {
                            directories.add(name);
                        } else if (type.equals("F")) {
                            files.add(name);
                        }
                    }
                }
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }

        directoryContents.add(files);
        directoryContents.add(directories);

        return directoryContents;
    }

    public void close() {

        try {
            myConnection.close();
            myStatement.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
   }
}
