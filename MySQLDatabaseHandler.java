import java.sql.*;
import java.util.ArrayList;

public class MySQLDatabaseHandler {

   private String JDBCDriver = "com.mysql.jdbc.Driver";
   private String databaseURL = "jdbc:mysql://localhost/secure_file_system?useSSL=false";
   private String username = "root";
   private String password = "Project";

   private Connection myConnection;
   private Statement myStatement;

   public MySQLDatabaseHandler() {
       myConnection = null;
       myStatement = null;

       try {
            Class.forName(JDBCDriver);

            myConnection = DriverManager.getConnection(databaseURL, username, password);
            myStatement = myConnection.createStatement();
        }catch(SQLException se) {            
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
   }

   public void close() {

        try {
            myConnection.close();
            myStatement.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
   }

   public Boolean addToUsers(User u) {

        String username = u.getUserName();
        String password = u.getPassword();
        String encryptionKey = u.getEncryptionKey();

        String query;
        query = "INSERT INTO users(username, password, encryptionkey) VALUES('" + username + "', '" + password + "',  '" + encryptionKey + "')";

        try {

            myStatement.execute(query);

        }
        catch (SQLException se) {
            se.printStackTrace();
            return null;
        }

        return true;
    }

    public User getUser(String username, String password) {
        String query;
        String encryptionKey = null;
        User oldUser;
        query = "SELECT encryptionkey FROM users WHERE username = '" + username + "' AND password = '" + password + "'";

        try {

            ResultSet resultSet = myStatement.executeQuery(query);

            while(resultSet.next()) {
                encryptionKey = resultSet.getString("encryptionkey");
            }

            oldUser = new User(username, password, encryptionKey);

        }
        catch (SQLException se) {
            se.printStackTrace();
            return null;
        }

        return oldUser;
    }

    public Boolean insertFile(String owner, String fName, String fBody) {
        String query;
        query = "INSERT INTO files(owner, filename, filebody) VALUES('" + owner + "', '" + fName + "',  '" + fBody + "')";

        try {

            myStatement.execute(query);

        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean updateFile(String owner, String fName, String fBody) {
        String query;
        query = "UPDATE files SET filebody = '" + fBody + "' WHERE owner = '" + owner + "' AND filename = '" + fName + "'";

        try {

            myStatement.execute(query);

        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }

        return true;
    }

    public Boolean addToGroups(String groupname, String username) {
        String query;
        query = "INSERT INTO groups(groupname, username) VALUES('" + groupname + "', '" + username + "')";

        try {

            myStatement.execute(query);

        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }

        return true;
    }

   public User isCorrectLogin(String username, String password) {
        String query;
        query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
        Boolean isCorrect = null;

        try {

            ResultSet resultSet = myStatement.executeQuery(query);

            if (resultSet.next() == false) {
                return null;
                // isCorrect = false;
            }
            else {
                return getUser(username, password);
                // isCorrect = true;
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }

        // return isCorrect;
        return null;
    }   

   public Boolean canEnterDirectory(User u, String dName) {
        String query;
        query = "SELECT username FROM groups WHERE groupname = (SELECT groupname FROM groups WHERE username = '" + u.getUserName() + "')"; 
        ArrayList<String> usernames = new ArrayList<String>();

        try {
            ResultSet resultSet = myStatement.executeQuery(query);

            while(resultSet.next()) {
                usernames.add(resultSet.getString("username"));
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }


        if (usernames.contains(dName)) {
            return true;
        }
        else {
            return false;
        }
   }

   public Boolean isOwner(User u, String fName) {
    String query;
    String owner = null;
    query = "SELECT owner FROM files WHERE filename = '" + fName + "'";

    try {
        ResultSet resultSet = myStatement.executeQuery(query);

        while(resultSet.next()) {
            owner = resultSet.getString("owner");
        }
    }
    catch (SQLException se) {
        se.printStackTrace();
    }


    if (u.getUserName().equals(owner)) {
        return true;
    }
    else {
        return false;
    }
}

   public Boolean canViewFiles(User u, String fName) {
        String query;
        String query1;
        query = "SELECT username FROM groups WHERE groupname = (SELECT groupname FROM groups WHERE username = '" + u.getUserName() + "')"; 
        query1 = "SELECT owner FROM files WHERE filename = '" + fName + "'";
        String owner = null;
        ArrayList<String> usernames = new ArrayList<String>();

        try {
            ResultSet resultSet = myStatement.executeQuery(query);

            while(resultSet.next()) {
                usernames.add(resultSet.getString("username"));
            }

            resultSet = myStatement.executeQuery(query1);

            while(resultSet.next()) {
                owner = resultSet.getString("owner");
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }

        if (usernames.contains(owner)) {
            return true;
        }
        else {
            return false;
        }
    }
}