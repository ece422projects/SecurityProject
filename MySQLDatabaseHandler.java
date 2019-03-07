import java.sql.*;
import java.util.ArrayList;

public class MySQLDatabaseHandler {

   private String JDBCDriver = "com.mysql.jdbc.Driver";
   private String databaseURL = "jdbc:mysql://localhost/secure_file_system?useSSL=false";
   private String username = "root";
   private String password = "Project";

   private Connection myConnection;
   private Statement myStatement;
   private ResultSet resultSet;

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

   public Boolean isCorrectLogin(String username, String password) {

        String query;
        Boolean isCorrect;
        query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";

        try {
            resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                isCorrect = true;
            }
            else {
                isCorrect = false;
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
            isCorrect = null;
        }

        return isCorrect;
    }

    public User getFromUsers(String username, String password) {

        String query;
        query = "SELECT encryptionkey FROM users WHERE username = '" + username + "' AND password = '" + password + "'";

        try {
             resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                String encryptionKey = resultSet.getString("encryptionkey");
                return new User(username, password, encryptionKey);
            } else {
                return null;
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
    }

    public Boolean addToUsers(User u) {

        String username = u.getUserName();
        String password = u.getPassword();
        String encryptionKey = u.getEncryptionKey();
        String query;
        query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";

        try {
             resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                return false;
            } else {
                query = "INSERT INTO users(username, password, encryptionkey) VALUES('" + username + "', '" + password + "',  '" + encryptionKey + "')";

                myStatement.execute(query);
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
            return null;
        }

        return true;
    }

    public ArrayList<String> getGroupNames(String username) {
      
        String query;
        ArrayList<String> groups = new ArrayList<String>();
        query = "SELECT groupname FROM groups WHERE username = '" + username + "'";

        try {
             resultSet = myStatement.executeQuery(query);
            
            while (resultSet.next()) {
                groups.add(resultSet.getString("groupname"));
            }

        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }

        return groups;
    }

    public Boolean isInGroup(String username, String groupname) {

        ArrayList<String> groups = getGroupNames(username);

        if (groups.contains(groupname)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean hasGroupInCommon(ArrayList<String> userGroups, ArrayList<String> ownerGroups) {

        for (String groupname : userGroups) {
            if (ownerGroups.contains(groupname)) {
                return true;
            }
        }

        return false;
    }

    public Boolean addToGroups(String groupname, String username) {

        String query;
        query = "SELECT * FROM groups WHERE groupname = '" + groupname + "' AND username = '" + username + "'";

        try {
             resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                return false;
            } else {
                query = "SELECT * FROM groups WHERE groupname = '" + groupname + "'";

                resultSet = myStatement.executeQuery(query);

                if (resultSet.next() == false) {
    
                    User dummyUser = new User(groupname, "password");
                    query = "INSERT INTO group_encryption(groupname, encryptionkey) VALUES('" + groupname +  "', '" + dummyUser.getEncryptionKey() + "')";
    
                    myStatement.execute(query);
                }
    
                query = "INSERT INTO groups(groupname, username) VALUES('" + groupname + "', '" + username + "')";
                
                myStatement.execute(query);
                
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }

        return true;
    }

    public Boolean removeFromGroups(String groupname, String username) {

        String query;
        query = "DELETE FROM groups WHERE groupname = '" + groupname + "AND username = '" + username + "'";

        try {
            myStatement.execute(query);

        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }

        return true;
    }

    public User getGroupEncrptionUser(String groupname) {

        String query;
        query = "SELECT encryptionkey FROM group_encryption WHERE groupname = '" + groupname + "'";

        try {
             resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                String encryptionKey = resultSet.getString("encryptionkey");
                return new User(groupname, "password", encryptionKey);
            }
            else {
                return null;
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
    }

    public Boolean isFileOwner(User user, String filepath, String filename) {

        String query;
        String owner = "";

        query = "SELECT owner FROM files WHERE filename = '" + filename + "' AND filepath = '" + filepath + "'";
    
        try {
            Connection connection = DriverManager.getConnection(databaseURL, username, password);
            Statement statement = connection.createStatement();             
            ResultSet rs = statement.executeQuery(query);
    
            if(rs.next()) {
                owner = rs.getString("owner");
            }

            connection.close();
            statement.close();
            rs.close();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    
        if (user.getUserName().equals(owner)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean hasViewFilePermissions(User user, String filepath, String filename) {

        String query;
        ArrayList<String> userGroups = getGroupNames(user.getUserName());
        ArrayList<String> ownerGroups = new ArrayList<String>();

        query = "SELECT owner from files WHERE filename = '" + filename + "' AND filepath = '" + filepath + "'";

        try {
            Connection connection = DriverManager.getConnection(databaseURL, username, password);
            Statement statement = connection.createStatement();             
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                String owner = rs.getString("owner");
                ownerGroups = getGroupNames(owner);
            }
            connection.close();
            statement.close();
            rs.close();

        } catch (SQLException se) {
            se.printStackTrace();
        }

        return hasGroupInCommon(userGroups, ownerGroups);
    }

    public Boolean hasReadFilePermissions(User user, String filepath, String filename) {

        String query;
        String canRead = "";
        String fileGroupname = "";

        query = "SELECT canread, groupname FROM files WHERE filename = '" + filename + "' AND filepath = '" + filepath + "'";

        try {
            Connection connection = DriverManager.getConnection(databaseURL, username, password);
            Statement statement = connection.createStatement();             
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                canRead = rs.getString("canread");
                fileGroupname = rs.getString("groupname");
            }

            connection.close();
            statement.close();
            rs.close();

            if (canRead.equals("Y") && isInGroup(user.getUserName(), fileGroupname) ) {
                return true;
            } else {
                return false;
            }

        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public Boolean hasEditFilePermissions(User user, String filepath, String filename) {

        String query;
        String canEdit = "";
        String fileGroupName = "";
        ArrayList<String> userGroups = getGroupNames(user.getUserName());
        // User groupUser = getGroupEncrptionUser( userGroups.get(0) );
        String groupname = getGroupnameFromFile(user, filepath, filename);
        if (groupname.equals("")) {
            return false;
        }

        User groupUser = getGroupEncrptionUser( groupname );


        query = "SELECT canedit, groupname FROM files WHERE filename = '" + groupUser.encryptData(filename) + "' AND filepath = '" + filepath + "'";

        try {
            Connection connection = DriverManager.getConnection(databaseURL, username, password);
            Statement statement = connection.createStatement();             
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                canEdit = rs.getString("canedit");
                fileGroupName = rs.getString("groupname");
            }

            connection.close();
            statement.close();
            rs.close();

            if (canEdit.equals("Y") && userGroups.contains(fileGroupName)) {
                return true;
            } else {
                return false;
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public Boolean isDirectoryOwner(User user, String directorypath, String directoryname) {

        String query;
        String owner = "";
        query = "SELECT owner FROM directories WHERE directoryname = '" + directoryname + "' AND directorypath = '" + directorypath + "'";
    
        try {
            Connection connection = DriverManager.getConnection(databaseURL, username, password);
            Statement statement = connection.createStatement();             
            ResultSet rs = statement.executeQuery(query);
    
            if (rs.next()) {
                owner = rs.getString("owner");
            }

            connection.close();
            statement.close();
            rs.close();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    
    
        if (user.getUserName().equals(owner)) {
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean hasViewDirectoryPermissions(User user, String directorypath, String directoryname) {

        String query;
        ArrayList<String> userGroups = getGroupNames(user.getUserName());
        ArrayList<String> ownerGroups = new ArrayList<String>();
        query = "SELECT owner from directories WHERE directoryname = '" + directoryname + "' AND directorypath = '" + directorypath + "'";

        try {
            Connection connection = DriverManager.getConnection(databaseURL, username, password);
            Statement statement = connection.createStatement();             
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                String owner = rs.getString("owner");
                ownerGroups = getGroupNames(owner);
            }

            connection.close();
            statement.close();
            rs.close();

        } catch (SQLException se) {
            se.printStackTrace();
        }

        return hasGroupInCommon(userGroups, ownerGroups);
    }

    public Boolean hasReadDirectoryPermissions(User user, String directorypath, String directoryname) {

        String query;
        String canRead = "";
        String directoryGroupName = "";

        query = "SELECT canread, groupname FROM directories WHERE directoryname = '" + directoryname + "' AND directorypath = '" + directorypath + "'";

        try {
            Connection connection = DriverManager.getConnection(databaseURL, username, password);
            Statement statement = connection.createStatement();             
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                canRead = rs.getString("canread");
                directoryGroupName = rs.getString("groupname");

            }

            connection.close();
            statement.close();
            rs.close();

            if (canRead.equals("Y") && isInGroup(user.getUserName(), directoryGroupName)) {
                return true;
            } else {
                return false;
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public Boolean hasEditDirectoryPermissions(User user, String directorypath, String directoryname) {

        String query;
        String canEdit = "";
        ArrayList<String> userGroups = getGroupNames(user.getUserName());
        // User groupUser = getGroupEncrptionUser( userGroups.get(0) );
        String groupname = getGroupnameFromDirectory(user, directorypath, directoryname);
        if (groupname.equals("")) {
            return false;
        }
        User groupUser = getGroupEncrptionUser( groupname );
        String directoryGroupName = "";

        query = "SELECT canedit, groupname FROM directories WHERE directoryname = '" + groupUser.encryptData(directoryname) + "' AND directorypath = '" + groupUser.encryptData(directorypath)+ "'";

        try {
            Connection connection = DriverManager.getConnection(databaseURL, username, password);
            Statement statement = connection.createStatement();             
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                canEdit = rs.getString("canedit");
                directoryGroupName = rs.getString("groupname");
            }

            connection.close();
            statement.close();
            rs.close();

            if (canEdit.equals("Y") && isInGroup(user.getUserName(), directoryGroupName)) {
                return true;
            } else {
                return false;
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public Boolean canViewDirectory(User user, String directorypath, String directoryname) {

        if (hasViewDirectoryPermissions(user, directorypath, directoryname)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean canViewFile(User user, String filepath, String filename) {

        if (hasViewFilePermissions(user, filepath, filename)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean canReadDirectory(User user, String directorypath, String directoryname) {

        if (isDirectoryOwner(user, directorypath, directoryname)) {
            return true;
        } else if (hasReadDirectoryPermissions(user, directorypath, directoryname)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean canReadFile(User user, String filepath, String filename) {

        if (isFileOwner(user, filepath, filename)) {
            return true;
        } else if (hasReadFilePermissions(user, filepath, filename)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean canEditDirectory(User user, String directorypath, String directoryname) {

        if (isDirectoryOwner(user, directorypath, directoryname)) {
            return true;
        } else if (hasEditDirectoryPermissions(user, directorypath, directoryname)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean canEditFile(User user, String filepath, String filename) {

        if (isFileOwner(user, filepath, filename)) {
            return true;
        } else if (hasEditFilePermissions(user, filepath, filename)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean renameDirectory(User user, String directorypath, String directoryname, String newDirectoryname) {

        if (canEditDirectory(user, directorypath, directoryname)) {
            String query;
            String owner = user.getUserName();
            query = "UPDATE files SET directoryname = '" + user.encryptData(newDirectoryname) + "' WHERE owner = '" + owner + "' AND directorypath = '" + directorypath + "' AND directoryname = '" + directoryname + "'";

            try {
                myStatement.execute(query);
            }
            catch (SQLException se) {
                se.printStackTrace();
                return null;
            }
        return true;
        }
        else {
            return false;
        }

    }

    public Boolean renameFile(User user, String filepath, String filename, String newFilename) {

        if (canEditFile(user, filename, filepath)) {
            String query;
            String owner = user.getUserName();
            query = "UPDATE files SET filename = '" + user.encryptData(newFilename) + "' WHERE owner = '" + owner + "' AND filepath = '" + filepath + "' AND filename = '" + filename + "'";

            try {
                myStatement.execute(query);
            }
            catch (SQLException se) {
                se.printStackTrace();
                return false;
            }
        return true;
        }
        return false;
    }

    public Boolean deleteDirectory(User user, String directorypath, String directoryname) {

        String encrypteddirectoryname = user.encryptData(directoryname);
        String encrypteddirectorypath = user.encryptData(directorypath);

        if (isFileOwner(user, encrypteddirectorypath, encrypteddirectoryname)) {
            String query;
            query = "DELETE FROM directories WHERE directoryname = '" + encrypteddirectoryname + "' AND directorypath = '" + encrypteddirectorypath + "'";
            
            try {
                myStatement.execute(query);
            }
            catch (SQLException se) {
                se.printStackTrace();
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public Boolean deleteFile(User user, String filepath, String filename) {

        String encryptedfilename = user.encryptData(filename);

        if (isFileOwner(user, filepath, encryptedfilename)) {
            String query;
            query = "DELETE FROM directories WHERE directoryname = '" + encryptedfilename + "' AND directorypath = '" + filepath + "'";
            
            try {
                myStatement.execute(query);
            }
            catch (SQLException se) {
                se.printStackTrace();
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public Boolean addDirectoryPermissions(User user, String directorypath, String directoryname, String groupname, String canRead, String canEdit) {

        String query;
        String decryptedDirectoryName = user.decryptData(directoryname);
        User groupUser = getGroupEncrptionUser(groupname);

        query = "UPDATE directories SET directoryname = '" + groupUser.encryptData(decryptedDirectoryName)
                    + ", groupname = '" + groupname + "', canread = '" + canRead +
                        "', canedit = '" + canEdit + "' WHERE directoryname = '" + directoryname + "' AND directorypath = '" + directorypath + "' AND owner = '" + user.getUserName() + "'";
        try {
                myStatement.execute(query);
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }

        return true;
    }

    public Boolean removeDirectoryPermissions(User user, String directorypath, String directoryname, String groupname) {

        String query;
        String decryptedDirectoryName = user.decryptData(directoryname);
        User groupUser = getGroupEncrptionUser(groupname);

        query = "UPDATE directories SET directoryname = '" + groupUser.encryptData(decryptedDirectoryName)
                    + " groupname = '" + groupname + "', canread = '" + "NULL" +
                        "', canedit = '" + "NULL" + "' WHERE directoryname = '" + directoryname + "' AND directorypath = '" + directorypath + "' AND owner = '" + user.getUserName() + "'";
        
        try {
                myStatement.execute(query);
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }

        return true;
    }

    public Boolean addFilePermissions(User user, String filepath, String filename, String groupname, String canRead, String canEdit) {

        String query;
        String fileBody = "";
        User groupUser = getGroupEncrptionUser(groupname);

        query = "SELECT filebody FROM files WHERE filename = '" + user.encryptData(filename) + "' AND filepath = '" + filepath + "'";

        try {
             resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {
                fileBody = resultSet.getString("filebody");
            }
            else {
                fileBody = "";
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

        String decryptedFileBody = user.decryptData(fileBody);


        query = "UPDATE files SET filename = '" + groupUser.encryptData(filename) + "', filepath = '" + filepath
                    + "', filebody = '" + groupUser.encryptData(decryptedFileBody) + "', canread = '" + canRead +
                        "', canedit = '" + canEdit +  "', groupname = '" + groupname + "' WHERE filename = '" + user.encryptData(filename) + "' AND filepath = '" + filepath + "' AND owner = '" + user.getUserName() + "'";

        try {
                myStatement.execute(query);
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }

        return true;
    }

    public Boolean addToDirectories(User user, String directorypath, String directoryname) {

        String query;
        String owner = user.getUserName();
        query = "INSERT INTO directories(owner, directoryname, directorypath) VALUES('" 
                    + owner + "', '" + user.encryptData(directoryname) + "', '" + directorypath + "')";

        try {
            myStatement.execute(query);
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean addToFiles(User user, String filepath, String filename, String filebody) {

        String query;
        String owner = user.getUserName();
        query = "SELECT * FROM files WHERE filepath = '" + filepath + "' AND filename = '" + user.encryptData(filename) + "'";

        System.out.println(query);
        
        try {
             resultSet = myStatement.executeQuery(query);

            if (resultSet.next()) {

                query = "UPDATE files SET filebody = '" + user.encryptData(filebody) + "' WHERE owner = '" + owner + 
                "' AND filename = '" + user.encryptData(filename) + "' filepath = '" + filepath + "'";
                System.out.println(query);
                myStatement.execute(query);
                
            } else {

                query = "INSERT INTO files(owner, filepath, filename, filebody) VALUES('" 
                + owner + "', '" + filepath + "', '" + user.encryptData(filename) + "',  '" + user.encryptData(filebody) + "')";
                System.out.println(query);      
                myStatement.execute(query);
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public String openFile(User user, String filepath, String filename) {

        String query;
        String groupname = getGroupnameFromFile(user, filepath, filename);
        System.out.println("USER: " + user.getUserName() + "  GROUPNAME: " + groupname);

        // String groupname = getGroupNames(user.getUserName()).get(0);
        String userEncryptedfilename = user.encryptData(filename);

        
        if (isFileOwner(user, filepath, userEncryptedfilename)) {

            query = "SELECT filebody FROM files WHERE filename = '" + userEncryptedfilename + "' AND filepath = '" + filepath + "'";

            try {
                 resultSet = myStatement.executeQuery(query);
    
                if (resultSet.next()) {
                    String filebody = resultSet.getString("filebody");
                    return user.decryptData(filebody);
                }
            }
            catch (SQLException se) {
                se.printStackTrace();
                return null;
            }
        } else if (groupname.equals("")) {
            return null;
        }
        
        User groupUser = getGroupEncrptionUser(groupname);
        String groupEncryptedfilename = groupUser.encryptData(filename);

        if (hasReadFilePermissions(user, filepath, groupEncryptedfilename)) {

            query = "SELECT owner, groupname, filebody FROM files WHERE filename = '" + groupEncryptedfilename + "' AND filepath = '" + filepath + "'";     
            
            try {
                 resultSet = myStatement.executeQuery(query);
    
                if (resultSet.next()) {
                    String filebody = resultSet.getString("filebody");
                    return groupUser.decryptData(filebody);
                }
            }
            catch (SQLException se) {
                se.printStackTrace();
                return null;
            }
        }

        return null;
    }

    public ArrayList<ArrayList<String>> openDirectory(User user, String directorypath, String directoryname) {

        ArrayList<ArrayList<String>> directoryContents = new ArrayList<ArrayList<String>>();
        String newPath = directorypath + "/" + directoryname;
        String userEncrypteddirectiryname = user.encryptData(directoryname);

        if (isDirectoryOwner(user, directorypath, directoryname)) { //root directory

            System.out.println("IN MY ROOT DIRECTORY");

            directoryContents.add(returnFiles(user, newPath));
            directoryContents.add(returnDirectories(user, newPath));

            return directoryContents;

        } else if (isDirectoryOwner(user, directorypath, userEncrypteddirectiryname)) { // owns other directory

            System.out.println("ANOTHER DIRECTORY NOT ROOT");

            directoryContents.add(returnFiles(user, newPath));
            directoryContents.add(returnDirectories(user, newPath));

            return directoryContents;

        }

        ArrayList<String> userGroup = getGroupNames(user.getUserName());
        for (String groupname : userGroup) {
            User groupUser = getGroupEncrptionUser(groupname);
            String groupEncrypteddirectoryname = groupUser.encryptData(directoryname);

            if (hasReadDirectoryPermissions(user, directorypath, groupEncrypteddirectoryname)) {

                System.out.println("CAN READ OTHERS DIRECTIORY");

                directoryContents.add(returnFiles(user, newPath));
                directoryContents.add(returnDirectories(user, newPath));

                return directoryContents;
            }
        }
        
        if (hasViewDirectoryPermissions(user, directorypath, directoryname)) {

            System.out.println("CAN VIEW OTHERS DIRECTIORY");

            directoryContents.add(returnFiles(user, newPath));
            directoryContents.add(returnDirectories(user, newPath));

            return directoryContents;
        }

        return directoryContents;
    }

    public ArrayList<String> returnDirectories(User user, String directorypath) {

        String query;
        String directoryname = null;
        String groupname;
        ArrayList<ArrayList<String>> allDirectoryInfo = new ArrayList<ArrayList<String>>();
        ArrayList<String> directories = new ArrayList<String>();

        query = "SELECT directoryname, groupname FROM directories WHERE directorypath = '" + directorypath + "'";

        try {
            resultSet = myStatement.executeQuery(query);

            while (resultSet.next()) {

                ArrayList<String> directoryInfo = new ArrayList<String>();

                directoryname = resultSet.getString("directoryname");
                groupname = resultSet.getString("groupname");

                directoryInfo.add(directoryname);
                directoryInfo.add(groupname);
                allDirectoryInfo.add( directoryInfo );  
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        for (ArrayList<String> a : allDirectoryInfo) {

            directoryname = a.get(0);
            groupname = a.get(1);

            if ( canReadDirectory(user, directorypath, directoryname) ) {
                if (groupname == null) {
                    directories.add(user.decryptData(directoryname));
                }
                else {
                    User groupUser = getGroupEncrptionUser(groupname);
                    directories.add(groupUser.decryptData(directoryname));
                }
            }
            else if ( canViewDirectory(user, directorypath, directoryname) ) {
                directories.add(directoryname);
            }
        }

        return directories;
    }

    public ArrayList<String> returnFiles(User user, String filepath) {

        String query;
        String filename = null;
        String groupname;
        ArrayList<ArrayList<String>> allFileInfo = new ArrayList<ArrayList<String>>();
        ArrayList<String> files = new ArrayList<String>();

        query = "SELECT filename, groupname FROM files WHERE filepath = '" + filepath + "'";

        try {
             resultSet = myStatement.executeQuery(query);

            while (resultSet.next()) {

                ArrayList<String> fileInfo = new ArrayList<String>();

                filename = resultSet.getString("filename");
                groupname = resultSet.getString("groupname");

                fileInfo.add(filename);
                fileInfo.add(groupname);
                allFileInfo.add( fileInfo );                
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

        for (ArrayList<String> a : allFileInfo) {

            filename = a.get(0);
            groupname = a.get(1);

            if ( canReadFile(user, filepath, filename) ) {

                if (groupname == null) {
                    files.add(user.decryptData(filename));
                }
                else {
                    User groupUser = getGroupEncrptionUser(groupname);
                    files.add(groupUser.decryptData(filename));
                }
            }
            else if ( canViewFile(user, filepath, filename) ) {
                files.add(filename);
            }
        }

        return files;
    }

    public Boolean addUserDirectory(User user) {
        String query;
        String owner = user.getUserName();
        query = "INSERT INTO directories(owner, directoryname, directorypath) VALUES('" + owner + "', '" + owner + "', '" + " " + "')";

        try {
            myStatement.execute(query);
        } catch (SQLException se) {
        
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<String> openRootDirectory() {

        ArrayList<String> userdirectories = new ArrayList<String>();
        String query;
        query = "SELECT directoryname FROM directories WHERE directorypath = ' '";
        
        try {
             resultSet = myStatement.executeQuery(query);

            while (resultSet.next()) {
                userdirectories.add(resultSet.getString("directoryname"));
            }
        } catch (SQLException se) {
        
            se.printStackTrace();
            return null;
        }

        return userdirectories;
    }

    public String getGroupnameFromFile(User user, String filepath, String filename) {

        ArrayList<String> userGroups = getGroupNames(user.getUserName());
        String query;
        User groupUser;

        for (String groupname : userGroups) {

            groupUser = getGroupEncrptionUser(groupname);

            query = "SELECT * FROM files WHERE filepath = '" + filepath + "' AND filename = '" + groupUser.encryptData(filename) + "'";

            try {
                Connection connection = DriverManager.getConnection(databaseURL, username, password);
                Statement statement = connection.createStatement();             
                ResultSet rs = statement.executeQuery(query);
                
                if (rs.next() == true) {
                    connection.close();
                    statement.close();
                    rs.close();
                    return groupname;
                }

                connection.close();
                statement.close();
                rs.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return "";
    }

    public String getGroupnameFromDirectory(User user, String directorypath, String directoryname) {

        ArrayList<String> userGroups = getGroupNames(user.getUserName());
        String query;
        User groupUser;

        for (String groupname : userGroups) {

            groupUser = getGroupEncrptionUser(groupname);

            query = "SELECT * FROM directories WHERE directorypath = '" + directorypath + "' AND directoryname = '" + groupUser.encryptData(directoryname) + "'";

            try {
                Connection connection = DriverManager.getConnection(databaseURL, username, password);
                Statement statement = connection.createStatement();             
                ResultSet rs = statement.executeQuery(query);
                
                if (rs.next() == true) {
                    connection.close();
                    statement.close();
                    rs.close();
                    return groupname;
                }

                connection.close();
                statement.close();
                rs.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return "";
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