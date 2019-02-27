import java.util.ArrayList;

public class Directory {

    private String directoryName;
    private ArrayList<Directory> directoryList;
    private ArrayList<File> fileList;
    private Permissions permissions;

    public Directory(User o, String dName) {

        this.directoryName = dName;
        this.directoryList = new ArrayList<Directory>();
        this.fileList = new ArrayList<File>();
        this.permission = new Permissions(o);
    }

    public Permissions getPermissions() {
        return this.permissions;
    }

    public void setDirectoryName(String newDirectoryName) {
        this.directoryName = newDirectoryName;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public void addFolder(Folder f) {
        this.directoryList.add(f);
    }

    public void deleteFolder(Folder f) {
        this.directoryList.remove(f);
    }

    public Directory getFolder(String fName) {
        for (Directory d : this.directoryList) {
            if (d.getDirectoryName() == fName) {
                return d;
            }
        }
        return null;
    }

    public ArrayList<Directory> getDirectoryFolders() {
        return this.directoryList;
    }

    public void addFile(File f) {
        this.fileList.add(f);
    }

    public void deleteFile(File f) {
        this.fileList.remove(f);
    }

    public Directory getFile(String fName) {
        for (File f : this.fileList) {
            if (f.getFileName() == fName) {
                return f;
            }
        }
        return null;
    }

    public ArrayList<File> getDirectoryFiles() {
        return this.directoryList;
    }
}