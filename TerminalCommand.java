import java.lang.Exception;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class TerminalCommand {

    private String rootPath;

    public TerminalCommand() {
        this.rootPath = "/Users/manuelakm/Desktop/root";

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

    public void addUser(String userName) {

        try {
            String command = "mkdir " + this.rootPath +"/" + userName;
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    public void returnUserFiles(String userName) {

        try {
            String command = "ls " + this.rootPath + "/" + userName;            
            Process p = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new 
            InputStreamReader(p.getInputStream()));

            // BufferedReader stdError = new BufferedReader(new 
            // InputStreamReader(p.getErrorStream()));

            ArrayList<String> files = new ArrayList<String>();
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                files.add(s);
            }

            System.out.println(files.toString());

            // // read any errors from the attempted command
            // System.out.println("Here is the standard error of the command (if any):\n");
            // while ((s = stdError.readLine()) != null) {
            //     System.out.println(s);
            // }


        } catch (IOException e) {
            e.printStackTrace();
        }  
    }

    public void updateFile(String userName, String fileName, String fileBody) {

        try {

     
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.rootPath + "/" + userName + "/" + fileName));            
            writer.write(fileBody);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    public String readFile(String userName, String fileName) {

        String fileBody = "";

        try {
     
            BufferedReader reader = new BufferedReader(new FileReader(this.rootPath + "/" + userName + "/" + fileName));
            StringBuilder sBuilder = new StringBuilder();
            String s = null;
            while ( (s = reader.readLine()) != null) {
                sBuilder.append(s);
            }

            fileBody = sBuilder.toString();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }     
        
        return fileBody;
    }

    public void deleteFile(String userName, String fileName) {

        try {
            String command = "rm " + this.rootPath + "/" + userName + "/" + fileName;            
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }  
    }

    public static void main(String[] args) {
        TerminalCommand tc = new TerminalCommand();
        tc.addUser("Manuela");
        tc.updateFile("Manuela", "Words.txt", "These are my words. I like them.");
        tc.updateFile("Manuela", "Sentences.txt", "Please stop!");
        tc.updateFile("Manuela", "Words.txt", "These are my new words.");
        tc.returnUserFiles("Manuela");
        tc.readFile("Manuela", "Words.txt");
        tc.returnUserFiles("Manuela");

    }
}