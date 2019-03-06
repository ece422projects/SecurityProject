import java.lang.*;
import java.util.*;
import java.io.*;

public class TerminalCommand {

    private String rootPath;
    private String currentDirectory;

    public TerminalCommand() {
        this.rootPath = "/home/ubuntu/root";

        makeRootDirectory();
    }

    public void makeRootDirectory() {

        try {
            String command = "mkdir " + this.rootPath;
            Process p = Runtime.getRuntime().exec(command);
            this.currentDirectory = "root";
        } catch (IOException e) {
            e.printStackTrace();
        }         
    }

    public void createUserDirectory(User u) {

        MySQLDatabaseHandler sqlHandler = new MySQLDatabaseHandler();
        sqlHandler.addToUsers(u);
        sqlHandler.close(); 

        try {
            String command = "mkdir " + this.rootPath + "/" + u.getUserName();
            Process p = Runtime.getRuntime().exec(command);  
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    public void addToGroup(User u, String groupName) {

        MySQLDatabaseHandler sqlHandler = new MySQLDatabaseHandler();
        sqlHandler.addToGroups(groupName, u.getUserName());
        sqlHandler.close();    
    }

    public Boolean changeDirectory(User u, String directoryName) {

        if (directoryName.equals("root")) {
            this.currentDirectory = "";
            return true;
        }

        MySQLDatabaseHandler sqlHandler = new MySQLDatabaseHandler();
        if (sqlHandler.canEnterDirectory(u, directoryName)) {
            this.currentDirectory = directoryName;
            sqlHandler.close();
            return true;
        }
        else {
            sqlHandler.close();
            return false;
        }
    }

    public ArrayList<String> returnFileNames(User u) {     

        MySQLDatabaseHandler sqlHandler = new MySQLDatabaseHandler();
        ArrayList<String> files = new ArrayList<String>();

        try {
            String command = "ls " + this.rootPath + "/" + this.currentDirectory + "/";            
            Process p = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new 
                                            InputStreamReader(p.getInputStream()));

            String s = null;
            while ((s = stdInput.readLine()) != null) {

                if (sqlHandler.isOwner(u, s)) {
                    files.add(u.decryptData(s));
                }
                else if (sqlHandler.canViewFiles(u, s)) {
                    files.add(s);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        sqlHandler.close();
        return files;
    }

    public void createFile(User u, String fileName, String fileBody) {

        MySQLDatabaseHandler sqlHandler = new MySQLDatabaseHandler();

        try {     
            String encryptedFileName = u.encryptData(fileName);
            String encryptedFileBody = u.encryptData(fileBody);
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.rootPath + "/" + this.currentDirectory + "/" + encryptedFileName )); 
            writer.write(encryptedFileBody);
            sqlHandler.insertFile(u.getUserName(), encryptedFileName, encryptedFileBody);
            writer.close();
            sqlHandler.close();

        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    public void updateFile(User u, String fileName, String fileBody) {

        MySQLDatabaseHandler sqlHandler = new MySQLDatabaseHandler();

        try {

            String encryptedFileName = u.encryptData(fileName);
            String encryptedFileBody = u.encryptData(fileBody);
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.rootPath + "/" + this.currentDirectory + "/" + encryptedFileName )); 
            writer.write(encryptedFileBody);
            sqlHandler.updateFile(u.getUserName(), encryptedFileName, encryptedFileBody);
            writer.close();
            sqlHandler.close();

        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    public String readFile(User u, String fileName) {

        MySQLDatabaseHandler sqlHandler = new MySQLDatabaseHandler();
        String encryptedFilename = u.encryptData(fileName);
        String fileBody = "";

        if (sqlHandler.isOwner(u, encryptedFilename)) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.rootPath + "/" + this.currentDirectory + "/" + encryptedFilename ) );
                StringBuilder sBuilder = new StringBuilder();
                String s = null;
                while ( (s = reader.readLine()) != null) {
                    sBuilder.append(s);
                }

                fileBody = u.decryptData(sBuilder.toString());
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }     
            
            sqlHandler.close();
            return fileBody;
        }
        else {
            sqlHandler.close();
            return null;
        }
    }

    public void deleteFile(User u, String fileName) {

        try {
            String command = "rm " + this.rootPath + "/" + this.currentDirectory + "/" + fileName;            
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}