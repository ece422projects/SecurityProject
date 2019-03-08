import java.util.ArrayList;

public class SecureFileSystem {

    public SecureFileSystem() {

    }
    public static void main (String[] args) {

        Controller controller = new Controller();
        // mySQLDatabaseHandler.signUp("user1", "user1password");
        // mySQLDatabaseHandler.signUp("user2", "user2password");
        // mySQLDatabaseHandler.signUp("user3", "user3password");
        // mySQLDatabaseHandler.addFile("user1", "F", "/users/user1/hello.txt", "Hello World!");
        // mySQLDatabaseHandler.addFile("user1", "F", "/users/user1/goodbyte.txt", "Goodbye World!");
        // mySQLDatabaseHandler.addDirectory("user1", "D", "/users/user1/Pictures");
        // mySQLDatabaseHandler.addFile("user1", "F", "/users/user1/Pictures/pictures.txt", "Send me to a beach!");
        // mySQLDatabaseHandler.addFile("user2", "F", "/users/user2/World.txt", "I'm a butt!");
        // mySQLDatabaseHandler.addPermissions("user1", "/users/user1/goodbyte.txt", "team1", "Y", "Y");
        // System.out.println(mySQLDatabaseHandler.openDirectory("user1", "/users"));
        // System.out.println(mySQLDatabaseHandler.openDirectory("user2", "/users"));
        // System.out.println(mySQLDatabaseHandler.openDirectory("user3", "/users"));
        // System.out.println(mySQLDatabaseHandler.openDirectory("user1", "/users/user1").toString());
        // System.out.println(mySQLDatabaseHandler.openDirectory("user1", "/users/user2").toString());
        // System.out.println(mySQLDatabaseHandler.openDirectory("user1", "/users/user3"));
        // System.out.println(mySQLDatabaseHandler.openDirectory("user1", "/users/user1/Pictures").toString());
        // System.out.println(mySQLDatabaseHandler.openDirectory("user2", "/users/user1/yipaGWfUVeHZb%2Fgzr7JJ%2Fw%3D%3D").toString());
        // System.out.println(mySQLDatabaseHandler.openDirectory("user2", "/users/user2").toString());
        // System.out.println(mySQLDatabaseHandler.openDirectory("user2", "/users/user1").toString());
        // System.out.println(mySQLDatabaseHandler.openDirectory("user2", "/users/user3"));
        // System.out.println(mySQLDatabaseHandler.openFile("user1", "/users/user1/hello.txt"));
        // System.out.println(mySQLDatabaseHandler.openFile("user1", "/users/user1/goodbyte.txt"));
        // System.out.println(mySQLDatabaseHandler.editFile("user1", "/users/user1/goodbyte.txt", "THIS IS MY BRAND NEW BODY!!"));
        // System.out.println(mySQLDatabaseHandler.openFile("user1", "/users/user1/goodbyte.txt"));
        // System.out.println(mySQLDatabaseHandler.openFile("user1", "/users/user1/Pictures/pictures.txt"));
        // System.out.println(mySQLDatabaseHandler.openFile("user2", "/users/user2/World.txt"));
        // System.out.println(mySQLDatabaseHandler.openFile("user2", "/users/user1/goodbyte.txt"));
        controller.close();

        // String encryptedString = PathParsing.encryptPath(systemUser, "/manuela/pleasehelp/ihatethis");
        // System.out.println(encryptedString);
        // System.out.println(PathParsing.decryptPath(systemUser, encryptedString));
        // System.out.println(PathParsing.returnName(systemUser, encryptedString));

    }
}

                    // } else if (canRead.equals("Y") && hasGroupInCommon(username, owner)) {
                    //     System.out.println("CAN READ AND SHIT");
                    // // } else if (hasGroupInCommon(username, owner)) {
                    //     if (type.equals("D")) {
                    //         directories.add(systemUser.decryptData(name));
                    //     } else if (type.equals("F")) {
                    //         files.add(systemUser.decryptData(name));
                    //     }
                    // } else if (!hasGroupInCommon(username, owner)) {
                        // System.out.println("HERE!!");