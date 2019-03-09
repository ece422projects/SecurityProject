import java.lang.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.io.File;

public class CommandLineHandler {

    private String JDBCDriver = "com.mysql.jdbc.Driver";
    private String databaseURL = "jdbc:mysql://localhost/secure_file_system?useSSL=false";
    private String databaseUsername = "root";
    private String databasePassword = "Project";
 
    private Connection myConnection;
    private Statement myStatement;

    private String rootPath;

    public CommandLineHandler() {
        this.rootPath = "/home/ubuntu";

        try {
            Class.forName(JDBCDriver);

            myConnection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
            myStatement = myConnection.createStatement();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }

        makeRootDirectory();
    }

    public void makeRootDirectory() {

        try {
            String command = "mkdir " + this.rootPath + "/users/";
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }         
    }

    public ArrayList<String> checkForCorruption(String username) {

        String query;
        query = "SELECT * from contents WHERE owner = '" + username + "'";
        ArrayList<String> corruptedFileNames = new ArrayList<String>();

        try {
            ResultSet resultSet = myStatement.executeQuery(query);

            while (resultSet.next()) {
                String type = resultSet.getString("type");
                String path = resultSet.getString("path");
                String filebody = resultSet.getString("filebody");

                if (type.equals("F")) {

                    String physicalFileBody = readFile(path);

                    if (!filebody.equals(physicalFileBody)) {
                        corruptedFileNames.add( path );
                    }
                }
            }

        } catch (SQLException se) {
            se.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }

        return corruptedFileNames;
    }

    public void createPhysicalDirectory(String path) {

        try {
            String command = "mkdir " + this.rootPath + path;
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }  
    }

    public void deletePhysicalDirectory(String path) {

        try {
            String command = "rm -rf " + this.rootPath + path;
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }         
    }

    public void createPhysicalFile(String path, String filebody) {

        try {
            path = path.substring(0, path.length() - 1);
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.rootPath + path.trim()));
            writer.write(filebody);
            writer.close();
        }  catch (IOException e) {
            e.printStackTrace();
        } 
    }

    public void deletePhysicalFile(String path) {

        try {
            String command = "rm " + this.rootPath + path;
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }         
    }

    public String readFile(String path) {

        String fileBody = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.rootPath + path) );
            StringBuilder sBuilder = new StringBuilder();
            String s = null;
            while ( (s = reader.readLine()) != null) {
                sBuilder.append(s);
            }

            fileBody = sBuilder.toString();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }     
        
        return fileBody;
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