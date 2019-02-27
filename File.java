public class File {

    private String fileName;
    private String byteRepresentation;
    private Boolean isCorrupted;
    private Permissions permissions;

    public File(User owner, String fName, String bRepresentation) {

        this.fileName = f;
        this.byteRepresentation = bRepresentation;
        this.isCorrupted = false;
        this.permissions = new Permissions(owner);
    }

    public void setFileName(String newFileName) {
        this.fileName = newFileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setByteRepresentation(String newByteRepresentation) {
        this.byteRepresentation = newByteRepresentation;
    }

    public String getByteRepresentation() {
        return this.byteRepresentation;
    }

    public void setCorrutionStatus(Boolean newCorruptionStatus) {
        this.isCorrupted = newCorruptionStatus;
    }

    public Boolean getCorruptionStatus() {
        return this.isCorrupted;
    }

    public Permissions getPermissions() {
        return this.permissions;
    }
}