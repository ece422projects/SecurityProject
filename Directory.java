import java.util.ArrayList;

public class Directory {

    private String directoryOwner;
    private String directoryName;
    private ArrayList<Directory> directoryList;
    private ArrayList<File> fileList;

    public Directory(User dOwner, String dName) {

        this.directoryOwner = dOwner.getUserName();
        this.directoryName = dName;
        this.directoryList = new ArrayList<Directory>();
        this.fileList = new ArrayList<File>();
    }

    public String getDirectoryOwner() {
        return this.directoryOwner;
    }

    public void setDirectoryName(String newDirectoryName) {
        this.directoryName = newDirectoryName;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public void addDirectory(Directory directory) {
        this.directoryList.add(directory);
    }

    public void deleteDirectory(Directory directory) {
        this.directoryList.remove(directory);
    }

    public Directory getDirectory(String dName) {
        for (Directory directory : this.directoryList) {
            if (directory.getDirectoryName().equals(fName)) {
                return directory;
            }
        }
        return null;
    }

    public ArrayList<Directory> getDirectories() {
        return this.directoryList;
    }

    public void addFile(File file) {
        this.fileList.add(file);
    }

    public void deleteFile(File file) {
        this.fileList.remove(file);
    }

    public Directory getFile(String fName) {
        for (File file : this.fileList) {
            if (file.getFileName() == fName) {
                return file;
            }
        }
        return null;
    }

    public ArrayList<File> getFiles() {
        return this.directoryList;
    }
}