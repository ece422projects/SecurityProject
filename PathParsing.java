import java.net.URLEncoder;
import java.util.*;
import java.util.Map;

public class PathParsing {

    public static String encryptPath(SystemUser systemUser, String path) {

        StringBuilder encryptedPath = new StringBuilder();
        String[] pathElements = path.split("/");

        if (pathElements.length == 2) {
            return path + "/";
        }

        encryptedPath.append("/");
        encryptedPath.append(pathElements[1]);
        encryptedPath.append("/");
        encryptedPath.append(pathElements[2]);

        for (int i = 3; i < pathElements.length; i++) {
            encryptedPath.append("/");
            encryptedPath.append(systemUser.encryptData(pathElements[i]));
        }
        encryptedPath.append("/");

        return encryptedPath.toString();
    }

    public static String decryptPath(SystemUser systemUser, String path) {

        StringBuilder decryptedPath = new StringBuilder();
        String[] pathElements = path.split("/");

        if (pathElements.length == 2) {
            return path + "/";
        }

        decryptedPath.append("/");
        decryptedPath.append(pathElements[1]);
        decryptedPath.append("/");
        decryptedPath.append(pathElements[2]);

        for (int i = 3; i < pathElements.length; i++) {
            decryptedPath.append("/");
            decryptedPath.append(systemUser.decryptData(pathElements[i]));
        }
        decryptedPath.append("/");

        return decryptedPath.toString();
    }

    public static String returnElementName(SystemUser systemUser, String path) {
        String[] pathElements = path.split("/");

        return pathElements[pathElements.length - 1];
    }

    public static String renameElement(SystemUser systemUser, String path, String newName) {
        String[] pathElements = path.split("/");
        String newPath = "";

        for(int i = 1; i < pathElements.length - 1; i++) {
            newPath += "/" + pathElements[i];
        }
        newPath += "/" + newName;

        return newPath;
    }
}