import java.util.ArrayList;

public class SecureFileSystem {

    public SecureFileSystem() {

    }

    public static void main (String[] args) {

        TerminalCommand tc = new TerminalCommand();
        MySQLDatabaseHandler sqlHandler = new MySQLDatabaseHandler();
        User user1 = sqlHandler.isCorrectLogin("user1", "user1password");
        User user2 = sqlHandler.isCorrectLogin("user2", "user2password");
        User user3 = sqlHandler.isCorrectLogin("user3", "user3password");

        System.out.println(tc.changeDirectory(user1, "user1"));

        System.out.println(tc.returnFileNames(user1));
        System.out.println(tc.readFile(user1, "Sentences.txt"));
        System.out.println(tc.readFile(user1, "Words.txt"));

        tc.changeDirectory(user2, "user2");
        System.out.println(tc.returnFileNames(user2));
        System.out.println(tc.readFile(user2, "Hello.txt"));
        tc.changeDirectory(user3, "user3");
        System.out.println(tc.returnFileNames(user3));
        System.out.println(tc.readFile(user2, "Never.txt"));

        System.out.println(tc.changeDirectory(user1, "user2"));
        ArrayList<String> fileNames = tc.returnFileNames(user1);
        System.out.println(fileNames);
        System.out.println(tc.readFile(user1, fileNames.get(0)));
        System.out.println(tc.changeDirectory(user1, "user3"));

        sqlHandler.close();

    }
}