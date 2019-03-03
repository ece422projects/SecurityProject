import java.lang.Exception;
import java.io.*;
import java.util.ArrayList;
import javax.crypto.spec.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class TerminalCommand {

    private String rootPath;
    private String currentDirectory;

    public TerminalCommand() {
        this.rootPath = "/Users/manuelakm/Desktop/root";

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

    public void addUser(User u) {

        try {
            String command = "mkdir " + this.rootPath +"/" + u.getUserName();
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }


    public void changeDirectory(User u, String directoryName) {

        this.currentDirectory = directoryName;
    }

    public void returnFiles(User u) {

        try {
            String command = "ls " + this.rootPath + "/" + this.currentDirectory + "/";            
            Process p = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new 
                                            InputStreamReader(p.getInputStream()));

            ArrayList<String> files = new ArrayList<String>();
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                files.add(u.decryptData(s));
                // files.add(s);
            }

            System.out.println(files.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    public void writeToFile(User u, String fileName, String fileBody) {

        try {
     
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.rootPath + "/" + this.currentDirectory + "/" + u.encryptData(fileName) )); 
            writer.write(u.encryptData(fileBody));
            // BufferedWriter writer = new BufferedWriter(new FileWriter(this.rootPath + "/" + u.getUserName() + "/" + fileName));           
            // writer.write(fileBody);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    public String readFile(User u, String fileName) {

        String fileBody = "";

        try {
     
            BufferedReader reader = new BufferedReader(new FileReader(this.rootPath + "/" + this.currentDirectory + "/" + u.encryptData(fileName) ) );
            // System.out.println(u.encryptData(fileName));
            // BufferedReader reader = new BufferedReader(new FileReader(this.rootPath + "/" + u.getUserName() + "/" + fileName));
            StringBuilder sBuilder = new StringBuilder();
            String s = null;
            while ( (s = reader.readLine()) != null) {
                sBuilder.append(s);
            }

            fileBody = u.decryptData(sBuilder.toString());
            // fileBody = sBuilder.toString();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }     
        
        System.out.println(fileBody);
        return fileBody;
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