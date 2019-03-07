import java.util.ArrayList;

public class SecureFileSystem {

    public SecureFileSystem() {

    }

    public static void main (String[] args) {

        Controller controller = new Controller();

        // controller.signUp("user1", "user1password");
        // controller.signUp("user2", "user2password");
        // controller.signUp("user3", "user3password");

        User user1 = controller.login("user1", "user1password");
        User user2 = controller.login("user2", "user2password");
        User user3 = controller.login("user3", "user3password");

        // controller.editFile(user1, "Hello.txt", "/user1", "Hello World!1");
        // controller.editFile(user1, "Goodbyte.txt", "/user1", "Goodbye World!1");
        // controller.editFile(user2, "Hi.txt", "/user2", "Hello World!2");
        // controller.editFile(user2, "Later.txt", "/user2", "Goodbye World!2");
        // controller.editFile(user3, "Sup.txt", "/user3", "Hello World!3");
        // controller.editFile(user3, "Nope.txt", "/user3", "Goodbye World!3");

        // System.out.println(controller.openRootDirectory());
        // System.out.println(controller.openDirectory(user1, "", "user1").toString());
        System.out.println(controller.openDirectory(user1, "", "user2").toString());
        System.out.println(controller.openDirectory(user1, "", "user3").toString());

        // System.out.println(controller.openFile(user1, "/user1", "Hello.txt"));
        // System.out.println(controller.openFile(user1, "/user1", "Goodbyte.txt"));
        // System.out.println(controller.openFile(user1, "/user2", "9Ldzues2x8Cq3XhiZsPNnQ%3D%3D"));
        // System.out.println(controller.openFile(user1, "/user2", "zhpsc4X7N9ZfsAjRYhsUFg%3D%3D"));

        System.out.println(controller.openDirectory(user2, "", "user1").toString());
        System.out.println(controller.openDirectory(user2, "", "user2").toString());
        System.out.println(controller.openDirectory(user2, "", "user3").toString());

        // System.out.println(controller.openFile(user2, "/user2", "Hi.txt"));
        // System.out.println(controller.openFile(user2, "/user2", "Later.txt"));
        // System.out.println(controller.openFile(user2, "/user1", "eyHDEmlBZ5WzUrufCBsQVg%3D%3D"));
        // System.out.println(controller.openFile(user2, "/user1", "JQF47kgM5ffpMe6vyXbHVA%3D%3D"));

        System.out.println(controller.openDirectory(user3, "", "user1").toString());
        System.out.println(controller.openDirectory(user3, "", "user2").toString());
        System.out.println(controller.openDirectory(user3, "", "user3").toString());

        // System.out.println(controller.openFile(user3, "/user3", "Sup.txt"));
        // System.out.println(controller.openFile(user3, "/user3", "Nope.txt"));

        // controller.createDirectory(user1, "/user1", "Pictures");
        // controller.editFile(user1, "/user1/Pictures", "Pictures.txt", "Let's imagine that I'm on a beach");
        System.out.println(controller.openRootDirectory());
        System.out.println(controller.openDirectory(user1, "", "user1"));
        System.out.println(controller.openFile(user1, "/user1/Pictures", "Pictures.txt"));
        System.out.println(controller.openDirectory(user2, "", "user1"));
        System.out.println(controller.openDirectory(user3, "", "user1"));

        // controller.editFilePermissions(user1, "/user1", "Goodbyte.txt", "team1", "Y", "Y");
        // System.out.println(controller.openDirectory(user1, "", "user1"));
        System.out.println(controller.openDirectory(user2, "", "user1"));
        System.out.println( controller.openFile(user2, "/user1", "Goodbyte.txt"));
        System.out.println( controller.canEditFile(user2, "/user1", "Goodbyte.txt"));
        // controller.editFile(user2, "/user1", "Goodbyte.txt", "NEW BODY"); //CREATING A NEW FILE SHOULD JUST UPDATE OLD ONE
        System.out.println(controller.openFile(user1, "/user1", "Goodbyte.txt"));
        controller.close();

    }
}