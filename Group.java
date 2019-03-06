import java.util.ArrayList;

public class Group {

    private String groupName;
    private ArrayList<User> groupMembers;

    public Group(String gName) {

        this.groupName = gName;
        this.groupMembers = new ArrayList<User>();
    }

    public void setGroupName(String newGroupName) {
        this.groupName = newGroupName;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public ArrayList<User> getGroupMembers() {
        return this.groupMembers;
    }

    public void addGroupMember(User u) {
        u.setGroupName(this.groupName);
        this.groupMembers.add(u);
    }

    public void removeGroupMember(User u) {
        this.groupMembers.remove(u);
    }
}