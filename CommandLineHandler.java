import java.lang.*;
import java.util.*;
import java.io.*;

public class CommandLineHandler {

    private String rootPath;

    public CommandLineHandler() {
        this.rootPath = "/home/ubuntu/root/";

        makeRootDirectory();
    }

    public void makeRootDirectory() {

        try {
            String command = "mkdir " + this.rootPath;
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }         
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void createUserDirectory(User u) {

        try {
            String command = "mkdir " + this.rootPath + u.getUserName();
            Process p = Runtime.getRuntime().exec(command);  
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    public ArrayList<String> returnAllFiles(String directoryPath) {     

        ArrayList<String> files = new ArrayList<String>();

        try {

            String command = "ls -p " + this.rootPath + directoryPath + " | grep -v /";
            Process p = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new 
                                            InputStreamReader(p.getInputStream()));

            String s = null;
            while ((s = stdInput.readLine()) != null) {

                files.add(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }

    public ArrayList<String> returnAllDirectories(String directoryPath) {     

        ArrayList<String> files = new ArrayList<String>();

        try {

            String command = "ls -p " + directoryPath + " | grep /";
            Process p = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new 
                                            InputStreamReader(p.getInputStream()));

            String s = null;
            while ((s = stdInput.readLine()) != null) {

                files.add(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }

    public void createFile(User u, String filePath, String fileName, String fileBody) {

        try {     
            String encryptedFileName = u.encryptData(fileName);
            String encryptedFileBody = u.encryptData(fileBody);
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.rootPath + "/" + filePath + "/" + encryptedFileName )); 
            writer.write(encryptedFileBody);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    public void updateFile(User u, String filePath, String fileName, String fileBody) {


        try {

            String encryptedFileName = u.encryptData(fileName);
            String encryptedFileBody = u.encryptData(fileBody);
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.rootPath + "/" + filePath + "/" + encryptedFileName )); 
            writer.write(encryptedFileBody);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    public String readFile(User u, String fileName, String filePath) {

        String encryptedFilename = u.encryptData(fileName);
        String fileBody = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.rootPath + "/" + filePath + "/" + encryptedFilename ) );
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
        
        return fileBody;
    }

    public void deleteFile(User u, String fileName, String filePath) {

        try {
            String command = "rm -rf " + this.rootPath + "/" + filePath + "/" + fileName;            
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}