import java.io.Serializable;
import java.sql.Blob;

public class File implements java.io.Serializable {

    private String fileOwner;
    private String fileName;
    private String fileBody;

    public File(User fOwner, String fName, String fBody) {

        this.fileOwner = fOwner.getUserName();
        this.fileName = fName;
        this.fileBody = fBody;
    }

    public String getFileOwner() {
        return this.fileOwner;
    }

    public void setFileName(String newFileName) {
        this.fileName = newFileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileBody(String newFileBody) {
        this.fileBody = newFileBody;
    }

    public String getFileBody() {
        return this.fileBody;
    }
}