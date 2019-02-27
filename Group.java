public class Group {

    private ArrayList<User> groupMembers;

    public Group() {
        this.groupMembers = new ArrayList<User>();
    }

    public ArrayList<User> getGroupMembers() {
        return this.groupMembers;
    }

    public void addGroupMember(User u) {
        this.groupMembers.add(u);
    }

    public void removeGroupMember(User u) {
        this.groupMembers.remove(u);
    }
}